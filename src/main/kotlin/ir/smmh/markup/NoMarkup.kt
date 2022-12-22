package ir.smmh.markup

import ir.smmh.lingu.Language
import ir.smmh.nile.or.Or
import ir.smmh.util.StringUtil.indent
import kotlin.math.abs
import kotlin.math.max

//import ir.smmh.markup.times

/**
 * Remove markup from a given Markup.Text object and return it to plain text
 */
object NoMarkup : Language.Markup {
    override fun compile(it: Markup.Text): String = when (it) {
        is Markup.Fragment.Atom -> it.data
        is Markup.Fragment.Unescaped -> it.data
        is Markup.Fragment.Affected -> compile(it.core)
        is Markup.Fragment.InlineCode -> it.codeString
        is Markup.Fragment.Link -> compile(it.core)
        is Markup.Fragment.Span -> compile(it.core)
        is Markup.Fragment.Multitude -> it.joinToString("", transform = ::compile)
        is Markup.Section.Paragraph -> (compile(it.contents)) + "\n\n"
        is Markup.Section.Comment -> ""
        is Markup.Section.Quotation -> (compile(it.contents)) + if (it.by == null) "" else ("\n\t- " + compile(it.by)) + "\n\n"
        is Markup.Section.CodeBlock -> indent(it.code.string) + "\n\n"
        is Markup.Section.List -> StringBuilder().apply {
            for (item in it) {
                append(" * ")
                append(compile(Or.generalize(item)))
                append('\n')
            }
            append('\n')
            // TODO test nested lists
        }.toString()
        Markup.Section.HorizontalRule -> "\n\n***\n\n"
        is Markup.Section.Heading -> StringBuilder().apply {
            val title = compile(it.heading)
            append(title)
            append('\n')
            append("-".repeat(title.length))
            append('\n')
            for (section in it) {
                append('\n')
                append(compile(section))
            }
        }.toString()
        is Markup.Document -> StringBuilder().apply {
            val title = it.name ?: "Untitled"
            append(title)
            append('\n')
            append("=".repeat(title.length))
            append('\n')
            for (topHeading in it) {
                append('\n')
                append(compile(topHeading))
            }
        }.toString()
        is Markup.Table -> {
            val columnWidths: MutableMap<Markup.Table.Column, Int> = getColumnWidths(it)
            var t = it.overColumns().joinToString("┬", "┌", "┐") { (columnWidths[it]!! + 2) * "─" } + "\n│"
            for (c in it.overColumns()) {
                val f = c.titleFragment.toString(NoMarkup)
                val extraSpace = abs(columnWidths[c]!! - f.length)
                t += " ${f.spaceOut(c.titleDirection ?: c.cellDirection, extraSpace)} │"
            }
            t += it.overColumns().joinToString("┼", "\n├", "┤\n") { (columnWidths[it]!! + 2) * "─" }
            for (k in it) {
                t += "│"
                for (c in it.overColumns()) {
                    val f = c[k].toString(NoMarkup)
                    val extraSpace = abs(columnWidths[c]!! - f.length)
                    t += " ${f.spaceOut(c.cellDirection, extraSpace)} │"
                }
                t += "\n"
            }
            t += it.overColumns().joinToString("┴", "└", "┘") { (columnWidths[it]!! + 2) * "─" }
            t
        }
        is Markup.Section.TeX -> indent(it.tex) + "\n\n"
    }

    private fun getColumnWidths(it: Markup.Table): MutableMap<Markup.Table.Column, Int> {
        val columnWidths: MutableMap<Markup.Table.Column, Int> = HashMap()
        for (c in it.overColumns()) {
            columnWidths[c] = max(3, c.titleFragment.toString(NoMarkup).length)
        }
        for (k in it) {
            for (c in it.overColumns()) {
                columnWidths[c] = max(columnWidths[c]!!, c[k].toString(NoMarkup).length)
            }
        }
        return columnWidths
    }

    override fun compile(document: Markup.Document, metadata: String?): String =
        compile(document)
//      metadata + "\n----------\n" + of(document)
}