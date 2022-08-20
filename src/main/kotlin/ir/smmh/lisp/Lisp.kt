package ir.smmh.lisp

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.lingu.Tokenizer
import ir.smmh.lisp.Type.*
import ir.smmh.lisp.Value.*
import ir.smmh.nilex.NiLexLanguage.FilteredTokens
import ir.smmh.nilex.NiLexLanguage.filterOut
import ir.smmh.nilex.NiLexTokenizer.Companion.v
import ir.smmh.nilex.NiLexTokenizerFactory
import java.io.File

object Lisp : Language.Processable {

    private val tokenize: Tokenizer = NiLexTokenizerFactory.create(
        """
        streak '\t\n\r ' as whitespace
        streak '[0-9]' as digits
        streak '[A-Z][a-z][0-9]_' as id
        keep '"' ... '"' as string
        keep '//' ... '\n' as comment
        verbatim '('
        verbatim ')'
        """
    )

    override val process: Code.Process = tokenize +
            filterOut("opener", "closer", "whitespace", "comment") + { code ->
        val queue = ArrayDeque((FilteredTokens of code)!!)
        var currentFrame = StackFrame(null).apply {

            Type.setAll(::set)
            Value.setAll(::set)

            // basics
            this["block"] = _callable(_Callable.Statements) {
                _statement {
                    it.forEach { (it as _statement).runnable.run() }
                }
            }
            this["if"] = _callable(_Callable.Simple(_Statement, _Boolean, _Statement, _Statement)) {
                _statement {
                    if (it[0] as _boolean == _boolean.TRUE)
                        (it[1] as _statement).runnable.run()
                    else
                        (it[2] as _statement).runnable.run()
                }
            }
            this["for"] = _callable(_Callable.Simple(_Statement, _Boolean, _Statement, _Statement)) {
                _statement {
                    if (it[0] as _boolean == _boolean.TRUE)
                        (it[1] as _statement).runnable.run()
                    else
                        (it[2] as _statement).runnable.run()
                }
            }

            // io
            this["print"] = _callable(_Callable.Simple(_Statement, _String)) {
                _statement {
                    println((it[0] as _string).string)
                }
            }

        }
        while (queue.isNotEmpty()) {
            val token = queue.removeFirst()
            if (token.type.name == v("(")) {
                currentFrame = StackFrame(currentFrame)
            } else {
                val value: Value? = when (token.type.name) {
                    v(")") -> {
                        val frame = currentFrame
                        val parent = frame.parent
                        if (parent == null) {
                            code.issue(token, "premature EOF")
                            null
                        } else {
                            currentFrame = parent
                            frame.evaluate()
                        }
                    }
                    "id" -> currentFrame[token.data].value
                    "digits" -> _number(token.data.toDouble())
                    "string" -> _string(token.data)
                    else -> {
                        code.issue(token, "unknown token type")
                        null
                    }
                }
                if (value != null) currentFrame.add(value)
            }
        }
        (currentFrame.evaluate() as _statement).runnable.run()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        Lisp.code(File("res/lisp-test")).beProcessed()
    }
}