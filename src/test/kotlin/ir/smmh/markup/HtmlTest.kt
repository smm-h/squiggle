package ir.smmh.markup

import ir.smmh.markup.MarkupTest.documents
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

object HtmlTest {
    @Test
    fun testTools() {
        val fragment = Markup.Tools.run {
            line() + "Hello, " + bold("World!")
        }
        assertEquals("Hello, <b>World!</b>", fragment.toString(Html))
        assertEquals("Hello, *World\\!*", fragment.toString(TelegramMarkdown))
    }

    @Test
    fun testDocuments() {
        documents.forEach { println(Html.compile(it)) }
    }
}