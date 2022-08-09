package ir.smmh.util

import ir.smmh.markup.Html
import ir.smmh.nile.Order
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

object StringReplacerTest {

    @Test
    fun testExample() {
        val s = "This is <pretty> cool & 'awesome'"
        val e = Html.escape.invoke(s)
        assertEquals("This is &lt;pretty&gt; cool &amp; &#39;awesome&#39;", e)
        assertEquals(5, Html.escape.count(s))
        assertEquals(51, Html.escape.calculateCapacityFor(s))
        assertEquals(51, e.length)
    }

    @Test
    fun testAmbiguity() {
        val m = mapOf("He" to "She", "Hell" to "Heaven")
        val s = "Hello, World!"
        assertEquals(
            "Shello, World!",
            StringReplacer(m, Order.shortestFirst)(s)
        )
        assertEquals(
            "Heaveno, World!",
            StringReplacer(m, Order.longestFirst)(s)
        )
    }
}