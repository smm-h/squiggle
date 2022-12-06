package ir.smmh.markup

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.markup.Markup.Fragment.Effect.*
import ir.smmh.nile.or.Or
import ir.smmh.util.StringReplacer
import java.io.File
import java.util.*

object Html : Language.HasFileExt.Impl("html"), Language.Markup {

    val defaultMetadata = """<link rel="stylesheet" href="${
        File("css/default.css").absolutePath
    }">"""

    val syntaxHighlighting = Code.Aspect<SyntaxHighlighting>("syntax-highlighting")

    interface SyntaxHighlighting {
        fun compile(): String

        class Impl(private val fileExt: String) : SyntaxHighlighting {
            private val prefix = fileExt.lowercase() + "_"
            private val parts: MutableList<Pair<String, String>> = ArrayList()
            private val allClassNames: MutableSet<String> = HashSet()
            private var lineCount = 1
            private var sealed = false

            fun add(data: String, classNames: String) {
                if (sealed) throw Language.Exception("trying to add to sealed SyntaxHighlighting")
                else if (data == "\n") {
                    parts.add(Pair(data, ""))
                    lineCount++
                } else if ('\n' in data) {
                    data.split('\n').also { list ->
                        if (list.isNotEmpty()) {
                            list.forEach {
                                if (it.isNotEmpty()) {
                                    add(it, classNames)
                                }
                                add("\n", "")
                            }
                            removeLast()
                        }
                    }
                } else {
                    val itsClassNames = StringJoiner(" ")
                    classNames.lowercase().split(' ').map { it.trim() }.forEach {
                        if (it.isNotEmpty()) {
                            val className = prefix + it
                            itsClassNames.add(className)
                            allClassNames.add(className)
                        }
                    }
                    parts.add(Pair(data, itsClassNames.toString()))
                }
            }

            fun getClassNames(): Set<String> = allClassNames

            fun seal() {
                if (!sealed) sealed = true
            }

            override fun compile() = compile(true, 120)

            fun compile(lineNumbers: Boolean = false, maxLineLength: Int = -1): String = StringBuilder().run {
                val lineNumberMaxDigitCount = if (lineNumbers) lineCount.toString().length else 0
                val gutterLength = if (lineNumbers) lineNumberMaxDigitCount + 5 else 0
                append("\n<pre>\n")
                var lineLength = 0
                var lineNumner = 1
                if (lineNumbers) {
                    val lineNumberString = lineNumner.toString()
                    append("<span class=\"code_line_numbers\">")
                    append(" ".repeat(lineNumberMaxDigitCount - lineNumberString.length))
                    append(lineNumberString)
                    append(".</span>    ")
                }
                for ((data, className) in parts) {
                    if (data == "\n") {
                        append('\n')
                        lineLength = 0
                        if (lineNumbers) {
                            lineNumner++
                            val lineNumberString = lineNumner.toString()
                            append("<span class=\"code_line_numbers\">")
                            append(" ".repeat(lineNumberMaxDigitCount - lineNumberString.length))
                            append(lineNumberString)
                            append(".</span>    ")
                        }
                    } else {
                        if (className.isEmpty()) {
                            append("<span>")
                        } else {
                            append("<span class=\"")
                            append(className)
                            append("\">")
                        }
                        if (maxLineLength != -1 && lineLength + data.length > maxLineLength) {
                            val i = maxLineLength - lineLength
                            append(Html.escape(data.substring(0, i)))
                            append('\n')
                            append(" ".repeat(gutterLength))
                            var theRest = data.substring(i)
                            while (theRest.length > maxLineLength) {
                                append(Html.escape(theRest.substring(0, maxLineLength)))
                                append('\n')
                                append(" ".repeat(gutterLength))
                                theRest = theRest.substring(maxLineLength)
                            }
                            append(Html.escape(theRest))
                            lineLength = 0
                        } else {
                            append(Html.escape(data))
                            lineLength += data.length
                        }
                        append("</span>")
                    }
                }
                append("</pre>\n")
                toString()
            }

            fun removeLast() {
                if (sealed) throw Language.Exception("trying to remove from sealed SyntaxHighlighting")
                else parts.removeLast()
            }
        }
    }

    override fun compile(it: Markup.Text): String = when (it) {
        is Markup.Fragment.Atom -> escape(it.data)
        is Markup.Fragment.Unescaped -> it.data
        is Markup.Fragment.Affected -> tag(
            when (it.effect) {
                BOLD -> "b"
                ITALIC -> "i"
                UNDERLINE -> "u"
                STRIKETHROUGH -> "s"
            }, compile(it.core)
        )
        is Markup.Fragment.InlineCode -> tag("code", it.codeString)
        is Markup.Fragment.Link -> {
            val url = bindFileExt(it.url)
            val bookmark = it.bookmark
            tag("a", compile(it.core), attributes("href" to if (bookmark == "") url else "$url#$bookmark"))
        }
        is Markup.Fragment.Span -> tag("span", compile(it.core), it.attributes)
        is Markup.Fragment.Multitude -> it.joinToString("") { compile(it) }
        is Markup.Section.Paragraph -> tagLn("p", compile(it.contents))
        is Markup.Section.Comment -> "<!-- ${compile(it.contents)} --> \n"
        is Markup.Section.Quotation -> {
            val by = if (it.by == null) "" else "\n\n— " + compile(it.by)
            tagLn("blockquote", compile(it.contents) + by)
        }
        is Markup.Section.CodeBlock -> {
            val sh = it.code.getNullable(syntaxHighlighting)
            if (sh == null) tagLn("pre", it.code.string)
            else sh.compile()
        }
        is Markup.Section.List -> StringBuilder().run {
            val l = if (it.numbered) "ol" else "ul"
            append("<p>")
            append("<$l>\n")
            for (item in it) {
                append("<li>")
                append(compile(Or.generalize(item)))
                append("</li>\n")
            }
            append("</$l>")
            append("</p>\n")
            toString()
        }
        Markup.Section.HorizontalRule -> "<hr>\n"
        is Markup.Section.Heading -> heading(it, 1)
        is Markup.Document -> compile(it, "")
        is Markup.Table -> StringBuilder().run {
            append("<table>\n")
            append("<tr>")
            for (c in it.overColumns()) {
                append(c.titleHyperdata?.let { "<th $it>" } ?: "<th>")
                append(c.titleFragment.toString(Html))
                append("</th>")
            }
            append("</tr>\n")
            for (k in it) {
                append(it.rowHyperdata[k]?.let { "<tr $it>" } ?: "<tr>")
                for (c in it.overColumns()) {
                    append(c.cellHyperdata[k]?.let { "<td $it>" } ?: "<td>")
                    append(c[k].toString(Html))
                    append("</td>")
                }
                append("</tr>\n")
            }
            append("</table>\n")
            toString()
        }
    }

    private fun heading(heading: Markup.Section.Heading, depth: Int): String = StringBuilder().run {
        val i = depth.coerceAtMost(6).toString()
        append("<h$i><span class=\"heading heading$i\">")
        append(compile(heading.heading))
        append("</span></h$i>\n")
        for (section in heading)
            append(if (section is Markup.Section.Heading) heading(section, depth + 1) else compile(section))
        toString()
    }

    override fun compile(document: Markup.Document, metadata: String?): String = StringBuilder().run {
        append("<html>\n")

        if (metadata != null) {
            append("<head>\n")
            append(metadata)
            append("</head>\n")
        }

        append("<body>\n")
        for (topHeading in document)
            append(heading(topHeading, 1))
        append("</body>\n")

        append("</html>\n")
        toString()
    }

    fun head(
        title: String? = null,
        base: String? = null,
        link: String? = null,
        meta: String? = null,
        script: String? = null,
        style: String? = null,
    ): String = StringJoiner("\n").run {
        if (title != null) add(tag("title", title))
        if (base != null) add(tag("base", base)) // default address/target for all links
        if (link != null) add(tag("link", link)) // relationship with an external resource
        if (meta != null) add(tag("meta", meta))
        if (script != null) add(tag("script", script))
        if (style != null) add(tag("style", style))
        toString()
    }

    fun attributes(vararg attributes: Pair<String, String>) =
        attributes(mapOf(*attributes))

    fun attributes(attributes: Map<String, String>): String {
        return attributes.keys.joinToString { " " + it.lowercase() + "=\"" + attributes[it] + "\"" }
    }

    fun tag(tag: String, data: String, attributes: String? = null) = "<$tag${attributes ?: ""}>$data</$tag>"

    fun tagLn(tag: String, data: String, attributes: String? = null) = tag(tag, data, attributes) + "\n"

    val escape = StringReplacer(
        "&" to "&amp;",
        "<" to "&lt;",
        ">" to "&gt;",
        "\"" to "&quot;",
        "'" to "&#39;",
    )
}