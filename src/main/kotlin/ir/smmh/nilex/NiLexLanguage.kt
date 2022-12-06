package ir.smmh.nilex

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.lingu.Token
import ir.smmh.lingu.Tokenizer.Companion.Tokens
import ir.smmh.nilex.NiLexTokenizer.Companion.v
import ir.smmh.serialization.json.Json
import java.io.File

object NiLexLanguage : Language.HasFileExt.Impl("nlx"), Language.Construction<Json.Array> {
    override val construction = Code.Aspect<Json.Array>("construction")

    private val meta = (NiLexTokenizerFactory()
            + kept("string", "'", "'")
            + kept("comment", "//", "\\n")
            + kept("multiLineComment", "/*", "*/")
            + streak("whitespace", "\\t ")
            + streak("newline", "\\t\\n\\r ")
            + streak("identifier", "[0-9][A-Z][a-z]_-")
            + verbatim("import")
            + verbatim("keep")
            + verbatim("verbatim")
            + verbatim("streak")
            + verbatim("...")
            + verbatim("as")
            )()

    override val process: Code.Process = meta + { code ->
        val array = Json.Array.Mutable.empty()
        addToArray(array, code)
        code[construction] = array
    }

    fun assert(assertion: (Code) -> String?) = Code.Process {
        var message = assertion(it)
        if (message != null) {
            it.issue(Code.Mishap.Impl(null, message, Code.Mishap.Level.ERROR, true))
        }
    }

    fun assertCountIsEven(type: String, aspect: Code.Aspect<List<Token>> = Tokens) = assert {
        var count = 0
        for (token in it.get(aspect)) if (token.type.name == type) count++
        if (count % 2 == 0) null
        else "type count not even: '$type' ($count)"
    }

    fun assertCountsAreEqual(opener: String, closer: String, aspect: Code.Aspect<List<Token>> = Tokens) =
        if (opener == closer) assertCountIsEven(opener, aspect)
        else assert {
            var openers = 0
            var closers = 0
            for (token in it.get(aspect)) when (token.type.name) {
                opener -> openers++
                closer -> closers++
            }
            if (openers == closers) null
            else "type counts not equal: '$opener' ($openers), '$closer' ($closers)"
        }

    fun assertBalance(opener: String, closer: String, aspect: Code.Aspect<List<Token>> = Tokens) =
        if (opener == closer) assertCountIsEven(opener, aspect)
        else assert {
            var balance = 0
            for (token in it.get(aspect)) when (token.type.name) {
                opener -> balance++
                closer -> {
                    balance--
                    if (balance < 0) break
                }
            }
            if (balance == 0) null
            else "types unbalanced: '$opener', '$closer' ($balance)"
        }

    val FilteredTokens = Code.Aspect<List<Token>>("filtered-tokens")

    fun filterOut(vararg tags: String) = filterOut(createFilter(*tags))
    fun filterOut(filter: (Token) -> Boolean) = Code.Process { code ->
        code[FilteredTokens] =
            (if (FilteredTokens in code) code.get(FilteredTokens) else code.get(Tokens)).filter(filter)
    }

    fun createFilter(vararg tags: String): (Token) -> Boolean = { token ->
        filterFunction(tags, token)
    }

    private fun filterFunction(tags: Array<out String>, token: Token): Boolean {
        for (tag in tags)
            if (tag in token.type)
                return false
        return true
    }

    private val filter = createFilter("opener", "closer", "whitespace")

    private fun addToArray(array: Json.Array.Mutable, code: Code) {
        // do combinations and then remove gaps:
        val q = ArrayDeque(code.get(Tokens).filter(filter))
        while (q.isNotEmpty()) {
            val token = q.removeFirst()
            when (token.type.name) {
                "newline" -> continue
                v("import") -> get(code, token, q, "string")?.also {
                    addToArray(array, Code(File(it[0] + "." + fileExt)))
                }
                v("verbatim") -> get(code, token, q, "string")?.also {
                    array.add(verbatim(it[0]))
                }
                v("streak") -> get(code, token, q, "string", v("as"), "identifier")?.also {
                    array.add(streak(it[2], it[0]))
                }
                v("keep") -> get(code, token, q, "string", v("..."), "string", v("as"), "identifier")?.also {
                    array.add(kept(it[4], it[0], it[2]))
                }
                else -> code.issue(token, "unexpected token: $token")
            }
        }
    }

    private fun get(code: Code, initialToken: Token, q: ArrayDeque<Token>, vararg types: String): List<String>? {
        val dataList: MutableList<String> = ArrayList()
        var token: Token = initialToken
        types.forEach { expectedType ->
            while (true) {
                val previousToken = token
                token = q.removeFirst()
                when (token.type.name) {
                    "whitespace", "multiLineComment" -> {
                        continue
                    }
                    expectedType -> {
                        dataList.add(token.data)
                        break
                    }
                    "newline" -> {
                        code.issue(previousToken, "missing token, expected: $expectedType")
                        return null
                    }
                    else -> {
                        code.issue(token, "wrong token type, expected: $expectedType, got ${token.type.name}")
                        return null
                    }
                }
            }
        }
        return dataList
    }

    private fun verbatim(data: String?) = Json.Object.of(
        "kind" to "verbatim",
        "data" to data,
    )

    private fun streak(name: String?, charSet: String?) = Json.Object.of(
        "kind" to "streak",
        "name" to name,
        "char-set" to charSet,
    )

    private fun kept(name: String?, opener: String?, closer: String?) = Json.Object.of(
        "kind" to "kept",
        "name" to name,
        "opener" to opener,
        "closer" to closer,
    )
}