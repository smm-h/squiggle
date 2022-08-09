package ir.smmh.markup

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

object MarkupTest {
    @Test
    fun testToString() {
        assertEquals("(b: bold text)", Markup.Tools.bold("bold text").toString())
    }

    @Test
    fun testDocuments() {
        documents.forEach { println(it) }
    }

    val documents: List<Markup.Document> = listOf(
        Markup.Document("welcome") {
            heading(line() + "Welcome to " + italic("PlotByX") + " bot!") {
                paragraph("You can type in expressions and get their plots in return. For example try sending the bot a simple 'x' and see what happens!")
                paragraph("Use the /help command to get started.")
            }
        },
        Markup.Document("getting-started") {
            heading("Getting Started") {
                paragraph("In this simple bot, you can type in expressions and get their plots in return.")
                heading("Expressions and operations") {
                    paragraph(
                        "Expressions are written in " + link(
                            "post-fix notation",
                            "https://en.wikipedia.org/wiki/Reverse_Polish_notation"
                        ) + " and consist entirely of numbers and words, no special characters. Words denote operations and variables."
                    )
                    paragraph("Simple examples include:")
                    list() {
                        item(code("x"))
                        item(code("x abs"))
                        paragraph("Powers:")
                        list() {
                            item(code("x 2 pow"))
                            item(code("2 x pow"))
                        }
                    }
                    paragraph("You can /builtin to see the full list of built-in operations, or /userDefined for user-defined ones!")
                    heading("User-defined operations") {
                        paragraph("You can define your own operations using the following syntax:")
                        paragraph("An expression that includes your bound variables, then those variables themselves, then an integer indicating their count, then a name for the function, and then define.")
                        paragraph("For example:")
                        list() {
                            item("Unary: " + code("a a mul a 1 sqr define"))
                            item("Binary: " + code("a sqr b sqr sqrt a b 2 hypotenuse define"))
                            item("Nullary: " + code("0.5 0 half define"))
                        }
                        paragraph("Make sure to not use names of free variables or reserved words for the names of your bound variables.")
                    }
                }
                heading("Free and bound variables") {
                    paragraph("You can use the " + code("assign") + " operation to assign a value to a variable.")
                    paragraph("For example:")
                    list() {
                        item(code("24 age assign"))
                        item(code("weight height sqr div assign"))
                    }
                    paragraph("If you do this, you will be able to use these variables in your expressions, but you will not be able to use them as bound variables in operation definitions.")
                }
                heading("Commands") {
                    paragraph("In addition to expressions, you can use /commands to interact with the bot.")
                }
            }
        }
    )
}