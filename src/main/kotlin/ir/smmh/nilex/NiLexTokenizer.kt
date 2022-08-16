package ir.smmh.nilex

import ir.smmh.lingu.Code
import ir.smmh.lingu.Token
import ir.smmh.lingu.TokenizationUtil.toCharSet
import ir.smmh.lingu.Tokenizer
import ir.smmh.nile.Order
import ir.smmh.serialization.json.Json


class NiLexTokenizer() : Tokenizer {

    //    private val charsThatStartVerbatims: MutableSet<Char> = HashSet()
    private val verbatimsByWhichTokenTheyStart: MutableMap<String, Order<Verbatim>> = HashMap()
    private val keptOpenersThatStartWithChar: MutableMap<Char, Order<Kept>> = HashMap()
    private val streaksThatContainChar: MutableMap<Char, Order<Streak>> = HashMap()

    private var sealed: Boolean = false

    companion object {
        private val dataLength: (Verbatim) -> Int = { -it.data.length }
        private val charSetSize: (Streak) -> Int = { +it.charSet.size }
        private val openerLength: (Kept) -> Int = { +it.opener.length }
    }

    fun define(definition: Json.Object) {
        if (sealed) throw Exception("cannot redefine sealed")
        else when (val kind = definition["kind"] as String?) {
            "verbatim" -> {
                // TODO repeated verbatims should be idempotent
                when (val it = definition["data"]) {
                    is String -> Verbatim(it)
                    is List<*> -> for (i in it) Verbatim(i as String)
                }
            }
            "streak" -> {
                val name = definition["name"] as String
                val charSet: Set<Char> = when (val it = definition["char-set"]) {
                    is String -> it.toCharSet()
                    is List<*> -> {
                        val s: MutableSet<Char> = HashSet()
                        for (i in it) when (i as String) {
                            "[0-9]" -> "0123456789"
                            "[A-Z]" -> "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                            "[a-z]" -> "abcdefghijklmnopqrstuvwxyz"
                            else -> i
                        }.forEach { s.add(it) }
                        s
                    }
                    else -> throw Exception("wrong type")
                }
                val type = Streak(name, charSet)
                charSet.forEach {
                    streaksThatContainChar.computeIfAbsent(it) { Order.by(charSetSize) }.enter(type)
                }
            }
            "kept" -> {
                val name = definition["name"] as String?
                val opener = definition["opener"] as String
                val closer = definition["closer"] as String
                val type = Kept(opener, closer, name)
                keptOpenersThatStartWithChar.computeIfAbsent(opener[0]) { Order.by(openerLength) }.enter(type)
            }
            "seal" -> {
                sealed = true
//                println(streaksThatContainChar.entries.map { "${it.key.code}:${it.value}" })
            }
            null -> throw Exception("missing key 'kind'")
            else -> throw Exception("undefined kind: $kind")
        }
    }

    private fun canMake(tokens: List<Token>, index: Int, pattern: List<String>): Boolean {
        if (index + pattern.size >= tokens.size) return false
        pattern.forEachIndexed { i, s -> if (tokens[index + i].data != s) return false }
        return true
    }

    override fun tokenize(code: Code): List<Token> {
        val string = code.string

        val tokens: MutableList<Token> = ArrayList()

        // keep track of where (in which kept, if any) we are in the code
        var where: Kept? = null

        // which character we are looking at
        var fwFlag = 0

        // portion of string we have added all the tokens of
        var bwFlag = 0

        // loop through all of the characters in the string
        while (fwFlag < string.length) {

            // if we are not inside a kept
            if (where == null) {

                // see if we can enter a kept
                var toEnter: Kept? = null

                // get the character at the forward flag
                val character = string[fwFlag]

                // if we have a kept that starts with that character
                val kepts = keptOpenersThatStartWithChar[character]

                if (kepts != null) {

                    // search all kepts that start with that character
                    for (kept in kepts) {

                        // figure out where the opener of that kept would end if it had appeared
                        val endIndex = fwFlag + kept.opener.length

                        // if that opener can appear and does appear
                        if (endIndex <= string.length && string.substring(fwFlag, endIndex) == kept.opener) {

                            // we found our kept
                            toEnter = kept

                            // so stop the search
                            break
                        }
                    }
                }

                // if we found a kept that we must enter
                if (toEnter != null) {

                    // take whatever is before it, break it into tokens, and add them
                    for (token in makeupVerbatims(makeupStreaks(bwFlag, string.substring(bwFlag, fwFlag)))) {
                        if (token.type is UnknownChar) code.issue(UnknownChar.Mishap(token))
                        tokens.add(token)
                    }

                    // enter that kept
                    where = toEnter

                    // add the opener to this kept
                    tokens.add(Token(where.opener, where.keeper, fwFlag))

                    // move the flag forward
                    fwFlag += where.opener.length - 1

                    // everything until here is added so also move the backward flag
                    bwFlag = fwFlag + 1
                }
            }

            // if we are inside a kept
            else {

                // figure out where its closer would end if it had appeared
                val endIndex = fwFlag + where.closer.length

                // if that closer can appear and does appear
                if (endIndex <= string.length && string.substring(fwFlag, endIndex) == where.closer) {

                    // add the kept
                    tokens.add(Token(string.substring(bwFlag, fwFlag), where, bwFlag))

                    // also add its closer
                    tokens.add(Token(where.closer, where.keeper, bwFlag))

                    // TODO test a kept with an at least 3 characters long closer

                    // move the flag forward
                    fwFlag = endIndex - 1

                    // everything until here is added so also move the backward flag
                    bwFlag = fwFlag + 1

                    // exit the kept
                    where = null
                }
            }

            // move the flag over to the next character
            fwFlag++
        }

        // when the flag reaches the end, not everything has been added
        if (bwFlag < string.length) {

            // either a non-kept portion remains
            if (where == null) {

                // break it into tokens and add them
                for (token in makeupVerbatims(makeupStreaks(bwFlag, string.substring(bwFlag, fwFlag)))) {
                    if (token.type is UnknownChar) code.issue(UnknownChar.Mishap(token))
                    tokens.add(token)
                }
            } else {

                // unless its closer is entirely whitespace,
                if (where.closer.isNotBlank()) {

                    // attach a mishap to its opener
                    code.issue(Kept.Unclosed(tokens[tokens.size - 1]))
                }

                // and add it
                tokens.add(Token(string.substring(bwFlag, fwFlag), where, bwFlag))
            }
        }

        return tokens
    }

    private fun getLongestPossible(verbatims: Order<Verbatim>, tokens: List<Token>, index: Int): Verbatim? {
        for (v in verbatims) if (canMake(tokens, index, v.pattern)) return v
        return null
    }

    private fun makeupVerbatims(tokens: List<Token>): List<Token> {
        val done = ArrayList<Token>()
        var index = 0
        while (index < tokens.size) {
            val token = tokens[index]
            val verbatimsThatStartWithToken = verbatimsByWhichTokenTheyStart[token.data]
            if (verbatimsThatStartWithToken != null) {
                val longestPossible = getLongestPossible(verbatimsThatStartWithToken, tokens, index)
                if (longestPossible != null) {
                    val length = longestPossible.pattern.size
                    // for (i in 0..length -1) tokens.removeAt(index)
                    done.add(index, Token(longestPossible.data, longestPossible, token.position))
                    index += length
                    continue
                }
            }
            done.add(token)
            index++
        }
        return done
    }

    private fun makeupStreaks(offset: Int, string: String): List<Token> {
        val nullTerminatedString = string + 0.toChar()
        val output: MutableList<Token> = ArrayList()
        val builder = StringBuilder()
        var previous: Order<Streak>
        var current: Order<Streak> = Order.by(charSetSize)
        var index = 0
        while (index < nullTerminatedString.length) {
            val character = nullTerminatedString[index]
            previous = current
            current = streaksThatContainChar[character] ?: run {
                if (builder.isEmpty())
                    output.add(Token(character.toString(), UnknownChar, offset + index))
                Order.by(charSetSize)
            }
            if (previous.isNotEmpty()) {
                val temp = Order.by(charSetSize)
                temp.enterAll(current.intersect(previous)) // TODO test
                current = temp
            }
            if (current.isEmpty()) {
                if (previous.isNotEmpty()) {
                    output.add(Token(builder.toString(), previous.first(), offset + index - builder.length))
                    index--
                }
                builder.clear()
            } else {
                builder.append(character)
            }
            index++
        }
        output.removeAt(output.size - 1)
        return output
    }

    sealed class TokenType : ir.smmh.lingu.Token.Type {
        override fun toString() = name
    }

    object UnknownChar : TokenType() {
        override val name = "unknown-character"

        class Mishap(override val token: Token) : Code.Mishap() {
            override val message = "unknown character ${token.data}"
            override val level = Level.ERROR
            override val fatal = false
        }
    }

    inner class Verbatim(val data: String) : TokenType() {
        override val name = "<$data>" // "verbatim"
        val pattern: List<String> = makeupStreaks(0, data).map { it.data }

        init {
//            for (token in pattern) if (token.type is UnknownChar) charsThatStartVerbatims.add(token.data[0])
            verbatimsByWhichTokenTheyStart.computeIfAbsent(pattern[0]) { Order.by(dataLength) }.enter(this)
        }
    }

    class Streak(override val name: String, val charSet: Set<Char>) : TokenType()
//        init { println(charSet.map { it.code }) }

    class Kept(
        val opener: String,
        val closer: String,
        name: String? = null
    ) : TokenType() {
        override val name: String = name ?: "$opener...$closer"
        val keeper = Keeper("$name-keeper")

        class Keeper(override val name: String) : TokenType()

        class Unclosed(override val token: Token) : Code.Mishap() {
            override val message = "opened but not closed ${token.data}"
            override val level = Level.ERROR
            override val fatal = false

            init {
                assert(token.type is Keeper)
            }
        }
    }

    class Exception(message: String) : kotlin.Exception(message)

}