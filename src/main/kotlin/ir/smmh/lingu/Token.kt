package ir.smmh.lingu

import ir.smmh.lingu.TokenizationUtil.visualizeWhitespace
import ir.smmh.nilex.NiLexTokenizer.Companion.v

data class Token(
    val data: String,
    val type: Token.Type,
    val position: Int,
) {
    val length: Int by data::length

    override fun toString() =
        (if (v(data) == type.name) ""
        else (if (data.isEmpty()) "()"
        else if (data.isBlank()) visualizeWhitespace(data)
        else "($data)") + " as ") + "${type.name} @$position" +
                if (length > 1) "-${position + length}" else ""

    sealed class Type(val name: String) {
        override fun toString() = name

        private val tags: MutableSet<String> = HashSet()
        operator fun contains(tag: String) = tag in tags
        operator fun plusAssign(tag: String) {
            tags.add(tag)
        }

        init {
            this += name
        }

        open class Atomic(name: String) : Type(name)
        open class Compound(name: String, val pattern: List<Type>) : Type(name)
    }

    companion object {
        val ROOT_TYPE = Token.Type.Atomic("root")
        val ROOT = Token("", ROOT_TYPE, 0)
    }

    sealed class Structure() {
        data class Leaf(val token: Token) : Structure() {
            override fun toString(): String = token.toString()
        }

        data class Node(val list: List<Structure>) : Structure() {
            override fun toString(): String = list.toString()
        }
    }
}