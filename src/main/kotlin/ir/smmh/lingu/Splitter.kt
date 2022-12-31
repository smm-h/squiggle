package ir.smmh.lingu

import ir.smmh.lingu.TokenizationUtil.DIGITS
import ir.smmh.lingu.TokenizationUtil.LETTERS
import ir.smmh.lingu.TokenizationUtil.LIGATURES
import ir.smmh.lingu.TokenizationUtil.isComprisedOnlyOf
import ir.smmh.lingu.TokenizationUtil.toCharSet
import ir.smmh.lingu.TokenizationUtil.toStringSet


class Splitter(private val delimiters: Set<Char> = "\t\n\r ".toCharSet()) : Tokenizer {

    private inner class TokenBuilder {
        private var data: String = ""
        private var position: Int = 0
        operator fun plusAssign(char: Char) {
            data += char
        }

        fun reset(index: Int) {
            data = ""
            position = index + 1
        }

        fun hasToken() = data.isNotEmpty()
        fun getToken() = Token(data, getTokenTypeOf(data), position)
    }

    private val types: MutableList<TokenType> = ArrayList()

    fun defineByPredicate(title: String, predicate: (String) -> Boolean) = TokenType(title, predicate)

    fun defineCharSetOnly(title: String, chars: Set<Char>) = TokenType(title) { it.isComprisedOnlyOf(chars) }

    fun defineExactSet(title: String, exacts: Set<String>) = TokenType(title) { it in exacts }

    object Predefined {
        val splitter = Splitter()
        val NUMBER = splitter.defineCharSetOnly("NUMBER", DIGITS + '.')
        val WORD = splitter.defineCharSetOnly("WORD", LETTERS.union(DIGITS) + '_' + '-')
        val OP = splitter.defineExactSet(
            "OP",
            "+-*/=!@#$%^&|[]{}()<>\"'.,:;\\/~".toStringSet()
                    + setOf("+=", "-=", "*=", "/=", "==", "<=", ">=", "^^", "&&", "||")
                    + LIGATURES
        )
    }

    val OTHER = defineByPredicate("OTHER") { false }

    init {
        types.remove(OTHER)
    }

    fun getTokenTypeOf(string: String): Token.Type.Atomic {
        for (t in types) if (t.predicate(string)) return t
        return OTHER
    }

    override fun tokenize(code: Code): List<Token> {
        val tokens: MutableList<Token> = ArrayList()
        val builder = TokenBuilder()
        code.string.forEachIndexed { index, char ->
            if (char in delimiters) {
                if (builder.hasToken()) tokens.add(builder.getToken())
                builder.reset(index)
            } else {
                builder += char
            }
        }
        if (builder.hasToken()) tokens.add(builder.getToken())
        return tokens
    }

    inner class TokenType(name: String, val predicate: (String) -> Boolean) : Token.Type.Atomic(name) {
        init {
            types.add(this)
        }

        override fun toString() = name
    }
}