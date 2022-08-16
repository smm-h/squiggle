package ir.smmh.nilex

import ir.smmh.lingu.Code
import ir.smmh.lingu.Tokenizer.Companion.tokens
import org.junit.jupiter.api.Test
import java.io.File

class NiLexTokenizerTest {

    @Test
    fun testExample() {
        val process = NiLexLanguage.process +
                { println(it[tokens]!!.joinToString(separator = "\n")) } +
                { println(it[tokens]!!.size) }

        process(Code(File("res/test-lang.nlx")))
    }
}