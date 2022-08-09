package ir.smmh.markup

import ir.smmh.lingu.Language
import ir.smmh.nile.or.Or
import kotlin.math.abs
import kotlin.math.max

object Markdown : Language.HasFileExt.Impl("md"), Language.Markup {

    override fun compile(document: Markup.Document, metadata: String?): String = StringBuilder().run {
//        if (metadata != null) {
//            append("<head>")
//            append(metadata)
//            append("</head>\n\n")
//        }
        for (topHeading in document)
            append(heading(topHeading, 1))

        deleteCharAt(length - 1)

        toString()
    }

    override fun compile(it: Markup.Text): String = when (it) {
        is Markup.Fragment.Atom -> it.data
        is Markup.Fragment.Unescaped -> it.data
        is Markup.Fragment.Affected -> when (it.effect) {
            Markup.Fragment.Effect.BOLD -> "**${compile(it.core)}**"
            Markup.Fragment.Effect.ITALIC -> "_${compile(it.core)}_"
            Markup.Fragment.Effect.UNDERLINE -> Html.tag("u", compile(it.core))
            Markup.Fragment.Effect.STRIKETHROUGH -> "~~${compile(it.core)}~~"
        }
        is Markup.Fragment.InlineCode -> "`${it.codeString}`"
        is Markup.Fragment.Link -> {
            val url = bindFileExt(it.url)
            val bookmark = it.bookmark
            "[${compile(it.core)}](${if (bookmark == "") url else "$url#$bookmark"})"
        }
        is Markup.Fragment.Span -> Html.tag("span", compile(it.core), it.attributes)
        is Markup.Fragment.Multitude -> it.joinToString("") { compile(it) }
        is Markup.Section.Paragraph -> compile(it.contents) + "\n\n"

        // THANKSTO https://stackoverflow.com/a/20885980/9115712
        is Markup.Section.Comment -> "[//]: # (${compile(it.contents)})" // TODO escape ')'

        is Markup.Section.Quotation -> {
            val by = if (it.by == null) "" else "\n>\n> — " + compile(it.by)
            "> ${compile(it.contents)}$by\n\n"
        }
        is Markup.Section.CodeBlock -> {
            val l = it.code.language
            "```${if (l is Language.HasFileExt) l.fileExt else ""}\n${it.code.string}\n```\n\n"
        }
        is Markup.Section.List -> StringBuilder().run {
            val b = if (it.numbered) "1. " else "- "
            for (item in it) {
                append(b)
                append(compile(Or.generalize(item)))
                append('\n')
            }
            append('\n')
            toString()
        }
        Markup.Section.HorizontalRule -> "***\n\n"
        is Markup.Section.Heading -> heading(it, 1)
        is Markup.Document -> compile(it, "")
        is Markup.Table -> {
            val columnWidths = getColumnWidths(it)
            var t = "|"
            for (c in it.overColumns()) {
                val f = c.titleFragment.toString(NoMarkup)
                val extraSpace = abs(columnWidths[c]!! - f.length)
                t += " ${f.spaceOut(c.cellDirection, extraSpace)} |"
            }
            t += "\n|"
            for (c in it.overColumns()) {
                var f = (columnWidths[c]!! - 2) * "-"
                when (c.cellDirection) {
                    TextDirection.LTR -> f = ":-$f"
                    TextDirection.RTL -> f += "-:"
                    TextDirection.CENTERED -> f = ":$f:"
                }
                t += " $f |"
            }
            for (k in it) {
                t += "\n|"
                for (c in it.overColumns()) {
                    val f = c[k].toString(Markdown)
                    val extraSpace = abs(columnWidths[c]!! - f.length)
                    t += " ${f.spaceOut(c.cellDirection, extraSpace)} |"
                }
            }
            t
        }
    }

    private fun getColumnWidths(it: Markup.Table): MutableMap<Markup.Table.Column, Int> {
        val columnWidths: MutableMap<Markup.Table.Column, Int> = HashMap()
        for (c in it.overColumns()) {
            columnWidths[c] = max(3, c.titleFragment.toString(Markdown).length)
        }
        for (k in it) {
            for (c in it.overColumns()) {
                columnWidths[c] = max(columnWidths[c]!!, c[k].toString(Markdown).length)
            }
        }
        return columnWidths
    }

    private fun heading(heading: Markup.Section.Heading, depth: Int): String = StringBuilder().run {
        append("#".repeat(depth))
        append(' ')
        append(compile(heading.heading))
        append("\n\n")
        for (section in heading)
            append(if (section is Markup.Section.Heading) heading(section, depth + 1) else compile(section))
        toString()
    }

    // TODO escaping
//    val escape = StringReplacer(
//        "&" to "&amp;",
//        "<" to "&lt;",
//        ">" to "&gt;",
//        "\"" to "&quot;",
//        "'" to "&#39;",
//    )
}
