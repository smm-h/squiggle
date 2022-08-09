package ir.smmh.autodoc

import ir.smmh.lingu.Language
import ir.smmh.lingu.Language.Companion.lateFileExt
import ir.smmh.markup.Html
import ir.smmh.markup.Markdown
import ir.smmh.markup.Markup
import ir.smmh.markup.Markup.joinToFragment
import ir.smmh.nile.Order
import ir.smmh.util.FileUtil.touch
import ir.smmh.util.FileUtil.writeTo
import ir.smmh.util.ReflectUtil.intendedName
import ir.smmh.util.StringReplacer
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.concurrent.thread
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility

class AutoDoc<L> private constructor(
    private val language: L,
    private val metadata: String? = null,
    private val publicClassesOnly: Boolean = true,
) where L : Language.Markup, L : Language.HasFileExt {
    companion object {
        val withHtml = AutoDoc(
            Html, Html.defaultMetadata
                    + """<link rel="icon" href="${File("res/favicon.ico").absolutePath}">"""
//                    + """<base href="autodocs/" target="_blank">"""
        )

        val withMarkdown = AutoDoc(Markdown, "")
    }

    private val baseDir = "autodocs/${language.fileExt}/"
    private val r = StringReplacer("." to "/")
    private val processing: MutableSet<KClass<*>> = HashSet()
    private val processed: MutableMap<KClass<*>, File> = HashMap()
    private val indexed: MutableMap<Path, Order<Pair<String, String>>> = HashMap()

    class Exception(message: String) : kotlin.Exception(message)

    fun getFile(c: KClass<*>, sleep: Long = 250): File {
        if (!process(null, c)) throw Exception("class is private")
        var file: File? = null
        println("Please wait for ${c.simpleName}")
        while (file == null || processed.size < processing.size) {
            file = processed[c]
            printProgress()
            Thread.sleep(sleep)
        }
        printProgress()
        return file
    }

    private fun printProgress() {
        if (processed.size > 0)
            println("${processed.size}/${processing.size}\t = ${100.0 * processed.size / processing.size}%")
    }

    private fun getFilename(c: KClass<*>): String {
        return r(c.qualifiedName!!) + "/" + c.simpleName!! + "." + language.fileExt
    }

    private val dot = Markup.Tools.atom(" \u00b7 ")
    private val indexFn = "index.$lateFileExt"

    @Synchronized
    private fun process(o: KClass<*>?, c: KClass<*>): Boolean {
        if (publicClassesOnly && c.visibility != KVisibility.PUBLIC) return false
        if (c in processing) return true
        thread {
            processing.add(c)
            val simpleName = c.simpleName ?: throw Exception("class is anonymous")
            Markup.Document(simpleName) {
                val filename = baseDir + getFilename(c)
                val up = "../".repeat(filename.count { it == '/' }) + baseDir // TODO test up with -2
                heading(simpleName) {
                    val parts = c.qualifiedName!!.split('.')
                    var level = parts.size - 1
                    paragraph(parts.joinToFragment(dot) { link(code(it), "../".repeat(level++) + indexFn) })
                    val e = if (o == null) italic("<user-requested>") else encounter(c, o, up)!!
                    comment(line() + "Encountered in: " + e)
                    comment(code("toString()") + ": " + c.toString())
                    val qualities = StringJoiner(" ").apply {
                        add(c.visibility.toString().lowercase())
                        if (c.isAbstract)
                            add("abstract")
                        if (c.isCompanion)
                            add("companion")
                        if (c.isData)
                            add("data")
                        if (c.isFinal)
                            add("final")
                        if (c.isFun)
                            add("fun")
                        if (c.isInner)
                            add("inner")
                        if (c.isOpen)
                            add("open")
                        if (c.isSealed)
                            add("sealed")
                        if (c.isValue)
                            add("value")
                    }
                    paragraph(line() + "Qualities: " + code(qualities.toString()))
                    fillIntensions(c, heading("Intensions"), up)
                    val nestedClasses = c.nestedClasses.toMutableSet()
                    nestedClasses.removeAll(c.sealedSubclasses.toSet())
                    if (nestedClasses.isNotEmpty()) heading("Nested Classes") {
                        list() {
                            nestedClasses.forEach {
                                encounter(c, it, up)?.let { item(it) }
                            }
                            sort()
                        }
                    }
                }
                val contents = language.compile(this, metadata + """<title>$simpleName</title>""")
//                val contents = "<pre>${linksOnly of this}</pre>"
                val file = contents writeTo touch(filename)
                processed[c] = file
//                val theirFirst = Comparator <Pair<String, String>> { it, other -> it.first.compareTo(other.first) }
//                indexed.computeIfAbsent(Path.of(filename).parent) { Order.by(theirFirst) }.enter()
            }
        }
        return true
    }

    private fun encounter(o: KClass<*>, c: KClass<*>, up: String): Markup.Fragment? = Markup.Tools.run {
        if (process(o, c)) link(code(c.intendedName), up + getFilename(c)) else null
    }

    private fun fillIntensions(c: KClass<*>, s: Markup.Section.CanContainList, up: String) {
        val i = c.supertypes
        if (i.isNotEmpty()) {
            s.list() {
                i.forEach {
                    val f = it.classifier
                    if (f is KClass<*>) {
                        encounter(c, f, up)?.let { fragment ->
                            item(fragment) // TODO assert non-leaf are isSealed
                            fillIntensions(f, this, up)
                        }
                    }
                }
                // TODO sort()
            }
        }
    }

//    private val linksOnly = object : Language.Markup {
//
//        override val fileExt: String = Html.fileExt
//
//        override fun compile(document: Markup.Document, metadata: String?) =
//            Html.compile(document, metadata)
//
//        override fun compile(it: Markup.Text): String {
//            return if (it is Markup.Fragment.Link) Html.compile(it)
//            else NoMarkup.compile(it)
//        }
//    }
}