package ir.smmh.nilex

import ir.smmh.lingu.Code
import ir.smmh.lingu.Tokenizer.Companion.Tokens
import org.junit.jupiter.api.Test
import java.io.File

class NiLexTokenizerTest {

    @Test
    fun testExample() {
        val process = NiLexLanguage.process +
                { println(it[Tokens].joinToString(separator = "\n")) } +
                { println(it[Tokens].size) }

        process(Code(File("res/test-lang.nlx")))
    }
}