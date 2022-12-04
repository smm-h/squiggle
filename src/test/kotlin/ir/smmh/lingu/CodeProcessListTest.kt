package ir.smmh.lingu

import ir.smmh.lingu.Tokenizer.Companion.Tokens
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CodeProcessListTest {

    @Test
    fun testReversed() {
        val reversed = Code.Aspect<String>("reversed")
        val process = Code.Process.empty + { it[reversed] = it.string.reversed() }
        val code = Code("Hey!", null)
        process(code)
        assertEquals("!yeH", reversed of code)
    }

    @Test
    fun testSplitter() {
        val tokenCount = Code.Aspect<Int>("token-count")
        val process = Splitter.Predefined.splitter + { it[tokenCount] = it[Tokens].size }
        val code = Code("Hi, it's nice to finally meet you!", null)
        process(code)
        println(Tokens of code)
        assertEquals(7, tokenCount of code)
    }
}