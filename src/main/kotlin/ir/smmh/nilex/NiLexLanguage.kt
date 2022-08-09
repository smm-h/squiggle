package ir.smmh.nilex

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.lingu.Tokenizer.Companion.tokens
import ir.smmh.nilex.NiLexFactory.Companion.kept
import ir.smmh.nilex.NiLexFactory.Companion.streak
import ir.smmh.nilex.NiLexFactory.Companion.verbatim
import ir.smmh.serialization.json.Json

object NiLexLanguage : Language.HasFileExt.Impl("nlx"), Language.Construction<Json.Array> {
    override val construction = Code.Aspect<Json.Array>("construction")

    private val meta = (NiLexFactory()
            + kept("single_quotes", "'", "'")
            + kept("double_quotes", "\"", "\"")
            + kept("comment", "#", "\n", true)
            + kept("single_line_comment", "//", "\n", true)
            + kept("multi_line_comment", "/*", "*/", true)
            + streak("whitespace", "\t\n\r ", true)
            + streak("identifier", listOf("[0-9]", "[A-Z]", "[a-z]", "_-"))
            + verbatim(listOf("ext", "import", "keep", "ignore", "verbatim", "streak", "...", "as"))
            )

    override val process: Code.Process = meta() + { code ->
        Json.Array.empty().apply {
            println(tokens of code)
            code[construction] = this
        }
    }
}