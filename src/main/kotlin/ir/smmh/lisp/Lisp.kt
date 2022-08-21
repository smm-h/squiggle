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

class Lisp(add: Brackets.() -> Unit) : Language.Construction<Runnable> {

    private val brackets = BracketsImpl().apply(add)

    interface Brackets {
        fun addBrackets(opener: String, closer: String, f: Value.f)
        fun addSquareBrackets(f: Value.f)
        fun addCurlyBraces(f: Value.f)
        fun addArrowBrackets(f: Value.f)
    }

    private class BracketsImpl : Brackets {
        val openers = HashMap<String, Value.f>()
        val closers = HashSet<String>().apply { add(v(")")) }
        val tokenizerAppendix = StringBuilder()
        var balances = Code.Process.empty

        override fun addBrackets(opener: String, closer: String, f: Value.f) {
            if (opener == "(" || opener == ")" || closer == "(" || closer == ")")
                throw Exception("cannot add '(' or ')' as custom brackets")
            openers += v(opener) to f;
            closers += v(closer)
            tokenizerAppendix.append("verbatim '$opener' verbatim '$closer'")
            balances += assertBalance(v(opener), v(closer), FilteredTokens)
        }

        override fun addSquareBrackets(f: Value.f) =
            addBrackets("[", "]", f)

        override fun addCurlyBraces(f: Value.f) =
            addBrackets("{", "}", f)

        override fun addArrowBrackets(f: Value.f) =
            addBrackets("<", ">", f)
    }

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
        """ + brackets.tokenizerAppendix.toString()
    )

    override val process: Code.Process = tokenize +
            filterOut("opener", "closer", "whitespace", "comment", "multiLineComment") +
            brackets.balances + { code ->
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
            } else if (typeName in brackets.openers) {
                currentFrame = StackFrame(currentFrame)
                currentFrame.add(brackets.openers[typeName]!!)
            } else {
                val value: Value? = if (typeName in brackets.closers) {
                    val previousFrame = currentFrame
                    currentFrame = previousFrame.parent!!
                    previousFrame.evaluate()
                } else when (typeName) {
                    "digits" -> Value._number(token.data.toDouble())
                    "string" -> Value._string(token.data)
                    "id" -> {
                        val id = token.data
                        currentFrame[id]?.value ?: Value._identifier(id)
                    }
                    else -> {
                        code.issue(token, "unknown token type $typeName")
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

    companion object {
        val defaultFlavor = Lisp {
            addCurlyBraces(Type._Callable._block)
        }

        @JvmStatic
        fun main(args: Array<String>) {
            defaultFlavor.code(File("res/lisp-test")).beConstructedInto<Runnable>().run()
        }
    }
}