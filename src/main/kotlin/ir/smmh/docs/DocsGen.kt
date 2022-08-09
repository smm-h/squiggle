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
object DocsGen {
    val lang: Language.Markup = Markdown // Html
    private val fileExt = if (lang is Language.HasFileExt) lang.fileExt else "txt"
    private val metadata: String? = null
    private val docs: MutableMap<String, Markup.Document> = HashMap()
    private val packageStack = ArrayDeque<String>()

    fun usingPackage(packageName: String, block: () -> Unit) {
        packageStack.addLast(packageName)
        block()
        packageStack.removeLast()
    }

    init {
        usingPackage("ir.smmh") {
            docs += "readme" to Markup.Document {
                heading("Squiggle") {
                    paragraph("Squiggle is an aggregation of my old Java projects converted into Kotlin and my new Kotlin projects. These projects include:")
                    list {
                        item(src("AutoDoc", "autodoc.AutoDoc") + ": for automatic " + wikipedia("documentation generation") + " from Kotlin source files")
                        item(src("Blockchain", "blockchain.Blockchain") + ": a simple " + wikipedia("blockchain") + " model")
                        item(src("Helium", "helium.Helium") + ": a high-level " + wikipedia("homoiconic") + " programming language with " + wikipedia("duck typing"))
                        item(src("Json", "serialization.json.Json") + ": " + link("JSON", "json.org") + " for Kotlin")
                        item(src("Lingu", "lingu.Language") + ": the base for Helium, Markup and Json projects")
                        item(src("Mage", "mage.core") + ": a simple platform-independent game engine that can run on desktop and Android devices")
                        item(src("Markup", "markup.Markup") + ": generate " + wikipedia("HTML") + " or " + wikipedia("Markdown") + " files from language-independent documents, described in Kotlin")
                        item(pkg("Nile", "nile") + ": versatile data structures and utilities")
                        item(src("NiLex", "niLex.NiLex") + ": a declarative lexer generator")
                        item(src("Nitron", "nitron.Nitron") + ": a non-relational " + wikipedia("DBMS"))
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
    }

    private fun Markup.InlineHelpers.pkg(text: String, name: String = text, pkg: String = packageStack.last(), module: String = "main") =
        src(text, name, pkg, module, ext = "")

    private fun Markup.InlineHelpers.src(text: String, name: String = text, pkg: String = packageStack.last(), module: String = "main", lang: String = "kotlin", ext: String = "kt", bookmark: String = "") =
        link(text, "src/$module/$lang/${"$pkg.$name".replace('.', '/')}.$ext", bookmark)

    private fun Markup.InlineHelpers.wikipedia(text: String, query: String = text, lang: String = "en", bookmark: String = "") =
        link(text, "https://$lang.wikipedia.org/wiki/${query.replace(" ", "%20")}", bookmark)

    fun generate() {
        docs.forEach { url, doc ->
            val contents = bindFileExt(lang.compile(doc, metadata), fileExt)
            val filename = bindFileExt(url + "." + lateFileExt, fileExt)
            contents writeTo touch(filename)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        generate()
    }
}