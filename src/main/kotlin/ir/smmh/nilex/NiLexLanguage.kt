package ir.smmh.nilex

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.lingu.Token
import ir.smmh.lingu.Tokenizer.Companion.Tokens
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

    /**
     * Types that have no participation in the interpretation of the code.
     */
    private val gaps: Set<String> = setOf("whitespace", "comment", "multiLineComment")

    private fun addToArray(array: Json.Array.Mutable, code: Code) {
        // do combinations and then remove gaps:
        val q = ArrayDeque((Tokens of code)!!.filter {
            it.type.name !in gaps &&
                    it.type !is NiLexTokenizer.Kept.Opener &&
                    it.type !is NiLexTokenizer.Kept.Closer
        })
        while (q.isNotEmpty()) {
            val token = q.removeFirst()
            when (token.type.name) {
                "newline" -> continue
                "«import»" -> get(code, token, q, "string")?.also {
                    addToArray(array, Code(File(it[0] + "." + fileExt)))
                }
                "«verbatim»" -> get(code, token, q, "string")?.also {
                    array.add(verbatim(it[0]))
                }
                "«streak»" -> get(code, token, q, "string", "«as»", "identifier")?.also {
                    array.add(streak(it[2], it[0]))
                }
                "«keep»" -> get(code, token, q, "string", "«...»", "string", "«as»", "identifier")?.also {
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