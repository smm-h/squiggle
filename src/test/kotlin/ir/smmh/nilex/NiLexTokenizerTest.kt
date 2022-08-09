package ir.smmh.nilex

import ir.smmh.lingu.Code
import ir.smmh.lingu.Tokenizer
import ir.smmh.nilex.NiLexTokenizer
import ir.smmh.nilex.NiLexLanguage
import org.junit.jupiter.api.Test
import java.io.File

class NiLexTokenizerTest {

    @Test
    fun testExample() {
        val process = NiLexLanguage.process +
                { println(it[NiLexTokenizer.strippedTokens].joinToString(separator = "\n")) } +
                { println(it[Tokenizer.tokens].size) }

        val code = Code(File("res/test-lang.nlx"))
        process(code)
    }
}