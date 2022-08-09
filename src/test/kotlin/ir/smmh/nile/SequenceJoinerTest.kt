package ir.smmh.nile


// This is important
import ir.smmh.nile.SequenceJoiner.Companion.plus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SequenceJoinerTest {
    @Test
    fun testJoin() {
        val a = Sequential.ofArguments(1, 2, 3)
        val b = Sequential.ofArguments("a", "b", "c")
        val c = a + b // NOT Iterable.plus(Iterable)
        assertEquals(6, c.size)
    }
}