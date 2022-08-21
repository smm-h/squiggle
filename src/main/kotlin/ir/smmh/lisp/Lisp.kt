package ir.smmh.lisp

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.lingu.Tokenizer
import ir.smmh.nilex.NiLexLanguage.FilteredTokens
import ir.smmh.nilex.NiLexLanguage.assertBalance
import ir.smmh.nilex.NiLexLanguage.filterOut
import ir.smmh.nilex.NiLexTokenizer.Companion.v
import ir.smmh.nilex.NiLexTokenizerFactory
import java.io.File

object Lisp : Language.Construction<Runnable> {

    override val construction = Code.Aspect<Runnable>("root-block")

    private val tokenize: Tokenizer = NiLexTokenizerFactory.create(
        """
        streak '\t\n\r ' as whitespace
        streak '[0-9]' as digits
        streak '[A-Z][a-z][0-9]_' as id
        keep '"' ... '"' as string
        keep '//' ... '\n' as comment
        keep '/*' ... '*/' as multiLineComment
        verbatim '('
        verbatim ')'
        verbatim '{'
        verbatim '}'
        """
    )

    override val process: Code.Process = tokenize +
            filterOut("opener", "closer", "whitespace", "comment", "multiLineComment") +
            assertBalance(v("("), v(")"), FilteredTokens) +
            assertBalance(v("{"), v("}"), FilteredTokens) + { code ->
        val queue = ArrayDeque((FilteredTokens of code)!!)
        var currentFrame = StackFrame(null).apply {

            Type.setAll(::set)
            Value.setAll(::set)
            Type._Callable.setAll(::set)

        }
        while (queue.isNotEmpty()) {
            val token = queue.removeFirst()
            val typeName = token.type.name
            if (typeName == v("(")) {
                currentFrame = StackFrame(currentFrame)
            } else if (typeName == v("{")) {
                currentFrame = StackFrame(currentFrame)
                currentFrame.add(Type._Callable._block)
            } else {
                val value: Value? = when (typeName) {
                    v(")"), v("}") -> {
                        val previousFrame = currentFrame
                        currentFrame = previousFrame.parent!!
                        previousFrame.evaluate()
                    }
                    "digits" -> Value._number(token.data.toDouble())
                    "string" -> Value._string(token.data)
                    "id" -> {
                        val id = token.data
                        currentFrame[id]?.value ?: Value._identifier(id)
                    }
                    else -> {
                        code.issue(token, "unknown token type")
                        null
                    }
                }
                if (value != null) currentFrame.add(value)
            }
        }
        code[construction] = Runnable {
            (currentFrame.evaluate() as Value.f).callable.call(emptyList())
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        Lisp.code(File("res/lisp-test")).beConstructedInto<Runnable>().run()
    }
}