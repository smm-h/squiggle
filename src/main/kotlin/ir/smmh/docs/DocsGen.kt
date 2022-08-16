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
                    item(src("AutoDoc", "ir.smmh.autodoc.AutoDoc") + ": for automatic " + wikipedia("documentation generation") + " from Kotlin source files")
                    item(src("Blockchain", "ir.smmh.blockchain.Blockchain") + ": a simple " + wikipedia("blockchain") + " model")
                    item(src("Helium", "ir.smmh.helium.Helium") + ": a high-level " + wikipedia("homoiconic") + " programming language with " + wikipedia("duck typing"))
                    item(src("Json", "ir.smmh.serialization.json.Json") + ": " + link("JSON", "json.org") + " for Kotlin")
                    item(src("Lingu", "ir.smmh.lingu.Language") + ": the base for Helium, Markup and Json projects")
                    item(src("Mage", "ir.smmh.mage.core") + ": a simple platform-independent game engine that can run on desktop and Android devices")
                    item(src("Markup", "ir.smmh.markup.Markup") + ": generate " + wikipedia("HTML") + " or " + wikipedia("Markdown") + " files from language-independent documents, described in Kotlin")
                    item(pkg("Nile", "ir.smmh.nile") + ": versatile data structures and utilities")
                    item(src("NiLex", "ir.smmh.niLex.NiLex") + ": a declarative lexer generator")
                    item(src("Nitron", "ir.smmh.nitron.Nitron") + ": a non-relational " + wikipedia("DBMS"))
                }
            }
        }
        docs += "docs/language" to Markup.Document {
            heading("How to create a Language using Lingu") {
                paragraph("")
            }
        }
        docs += "docs/nilex" to Markup.Document {
            heading("How to create a Lexer using NiLex") {
                paragraph("")
            }
        }
    }

    private fun Markup.InlineHelpers.pkg(text: String, name: String = text, module: String = "main", lang: String = "kotlin") =
        link(text, "src/$module/$lang/${name.replace('.', '/')}")

    private fun Markup.InlineHelpers.src(text: String, name: String = text, module: String = "main", lang: String = "kotlin", ext: String = "kt", bookmark: String = "") =
        link(text, "src/$module/$lang/${name.replace('.', '/')}.$ext", bookmark)

    private fun Markup.InlineHelpers.wikipedia(text: String, query: String = text, lang: String = "en", bookmark: String = "") =
        link(text, "https://$lang.wikipedia.org/wiki/${query.replace(" ", "%20")}", bookmark)

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