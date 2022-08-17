package ir.smmh.docs

import ir.smmh.lingu.Language
import ir.smmh.lingu.Language.Companion.lateFileExt
import ir.smmh.lingu.Language.HasFileExt.Companion.bindFileExt
import ir.smmh.markup.Markdown
import ir.smmh.markup.Markup
import ir.smmh.util.FileUtil.touch
import ir.smmh.util.FileUtil.writeTo

/**
 * See [Links]
 */
class DocsGen(
    val markupLanguage: Language.Markup,
    private val metadata: String? = null,
) {

    private val fileExt = if (markupLanguage is Language.HasFileExt) markupLanguage.fileExt else "txt"
    private val docs: MutableMap<String, Markup.Document> = HashMap()

    init {
        docs += "readme" to Markup.Document {
            heading("Squiggle") {
                paragraph("Squiggle is an aggregation of my old Java projects converted into Kotlin and my new Kotlin projects. These projects include:")
                list {
                    item(src("AutoDoc", "ir.smmh.autodoc.AutoDoc") + ": for automatic " + wiki("documentation generation") + " from Kotlin source files")
                    item(src("Blockchain", "ir.smmh.blockchain.Blockchain") + ": a simple " + wiki("blockchain") + " model")
                    item(src("Helium", "ir.smmh.helium.Helium") + ": a high-level " + wiki("homoiconic") + " programming language with " + wiki("duck typing"))
                    item(src("Json", "ir.smmh.serialization.json.Json") + ": " + link("JSON", "json.org") + " for Kotlin")
                    item(src("Lingu", "ir.smmh.lingu.Language") + ": the base for Helium, Markup and Json projects")
                    item(src("Mage", "ir.smmh.mage.core") + ": a simple platform-independent game engine that can run on desktop and Android devices")
                    item(src("Markup", "ir.smmh.markup.Markup") + ": generate " + wiki("HTML") + " or " + wiki("Markdown") + " files from language-independent documents, described in Kotlin")
                    item(pkg("Nile", "ir.smmh.nile") + ": versatile data structures and utilities")
                    item(src("NiLex", "ir.smmh.niLex.NiLex") + ": a declarative lexer generator")
                    item(src("Nitron", "ir.smmh.nitron.Nitron") + ": a non-relational " + wiki("DBMS"))
                }
            }
        }
        docs += "docs/lingu" to Markup.Document {
            heading("A guide to Lingu") {
                heading("What is Lingu?") {
                    paragraph(
                        line() +
                                "Lingu is short for " + wikt("linguistics") +
                                ". As a package, Lingu provides the most basic means to create and use " +
                                wiki("computer languages") + "."
                    )
                }
                heading("How it works") {
                    paragraph("")
                }
                heading(line() + "Creating your own " + code("Language")) {
                    paragraph("")
                }
            }
        }
        docs += "docs/nilex" to Markup.Document {
            heading("A guide to NiLex") {
                paragraph(italic(line() + "You may benefit from learning about " + doc("Lingu", "lingu") + " before reading this."))
                heading("What is NiLex?") {
                    paragraph(
                        line() +
                                "NiLex is a combination of the words Nile and " + wikt("Lex") +
                                ". It is a collection of tools that generate " +
                                src("Tokenizer", "ir.smmh.lingu.Tokenizer") + "s and " +
                                src("Parser", "ir.smmh.lingu.Parser") + "s from instructions written in NiLex DSL."
                    )
                }
                heading("How it works") {
                    paragraph("")
                }
                heading("NiLex DSL") {
                    paragraph("")
                }
                heading(line() + "Creating your own " + code("Tokenizer")) {
                    paragraph("")
                }
                heading(line() + "Creating your own " + code("Parser")) {
                    paragraph("")
                }
            }
        }
    }

    private fun Markup.InlineHelpers.doc(text: String, address: String, bookmark: String = "") =
        link(text, address + "." + lateFileExt, bookmark)

    private fun Markup.InlineHelpers.pkg(text: String, name: String, module: String = "main", lang: String = "kotlin") =
        link(text, "src/$module/$lang/${name.replace('.', '/')}")

    private fun Markup.InlineHelpers.src(text: String, name: String, module: String = "main", lang: String = "kotlin", ext: String = "kt", bookmark: String = "") =
        link(text, "src/$module/$lang/${name.replace('.', '/')}.$ext", bookmark)

    private fun Markup.InlineHelpers.wiki(text: String, query: String = text, lang: String = "en", bookmark: String = "") =
        link(text, "https://$lang.wikipedia.org/wiki/${query.replace(" ", "%20")}", bookmark)

    private fun Markup.InlineHelpers.wikt(text: String, query: String = text, lang: String = "en", bookmark: String = "") =
        link(text, "https://$lang.wiktionary.org/wiki/${query.replace(" ", "%20")}", bookmark)

    fun generate() {
        docs.forEach { url, doc ->
            val contents = bindFileExt(markupLanguage.compile(doc, metadata), fileExt)
            val filename = bindFileExt(url + "." + lateFileExt, fileExt)
            contents writeTo touch(filename)
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            DocsGen(Markdown).generate() // Html
        }
    }
}