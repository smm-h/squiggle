package ir.smmh.markup

import ir.smmh.lingu.Language
import ir.smmh.lingu.TokenizationUtil.toCharSet
import ir.smmh.markup.Markup.Fragment.Effect.*
import ir.smmh.nile.or.Or

object TelegramMarkdown : Language.Markup {
    override fun compile(it: Markup.Text): String = when (it) {
        is Markup.Fragment.Atom -> escape(it.data, plain)
        is Markup.Fragment.Unescaped -> it.data
        is Markup.Fragment.Affected -> {
            val core = compile(it.core)
            when (it.effect) {
                BOLD -> "*$core*"
                ITALIC -> "_${core}_\r"
                UNDERLINE -> "__${core}__"
                STRIKETHROUGH -> "~$core~"
                TEX -> "$$core$"
            }
        }
        is Markup.Fragment.InlineCode -> "`${escape(it.codeString, codeOnly)}`"
        is Markup.Fragment.Link -> {
            val url = it.url // bindFileExt(it.url)
            val bookmark = it.bookmark
            "[${compile(it.core)}](" + escape(if (bookmark == "") url else "$url#$bookmark", urlOnly)
            // TODO test bookmarks
        }
        is Markup.Fragment.Span -> compile(it.core)
        is Markup.Fragment.Multitude -> it.joinToString("", transform = ::compile)
        is Markup.Section.Paragraph -> compile(it.contents) + "\n\n"
        is Markup.Section.Comment -> ""
        is Markup.Section.Quotation -> "“${compile(it.contents)}”" + if (it.by == null) "" else ("\n— " + compile(it.by)) + "\n\n"
        is Markup.Section.CodeBlock -> "```\n${escape(it.code.string, codeOnly)}\n```\n\n"
        is Markup.Section.List -> StringBuilder().apply {
            for (item in it) {
                append("• ")
                append(compile(Or.generalize(item)))
                append('\n')
            }
            append('\n')
            // TODO test nested lists
            // shiftRight(inner-list, 2)
        }.toString()
        Markup.Section.HorizontalRule -> "\n\n***\n\n"
        is Markup.Section.Heading -> StringBuilder().apply {
            val title = compile(it.heading)
            append(compile(Markup.Tools.bold(title)))
            append("\n\n")
            for (section in it) {
                append(compile(section))
                append('\n')
            }
            append('\n')
        }.toString()
        is Markup.Document -> StringBuilder().apply {
            for (topHeading in it) {
                append(compile(topHeading))
                append('\n')
            }
        }.toString()
        is Markup.Table -> it.toString()
        is Markup.Section.TeX -> "$$\n${it.tex}\n$$\n\n"
    }

    override fun compile(document: Markup.Document, metadata: String?): String {
        return compile(document) // ignores metadata
    }

    private val plain: Set<Char> = "\\_*[]()~`>#+-=|{}.!".toCharSet()
    private val urlOnly: Set<Char> = "\\)".toCharSet()
    private val codeOnly: Set<Char> = "\\`".toCharSet()

    fun escape(string: String, toEscape: Set<Char>): String {
        val n = string.length
        var count = 0
        string.asIterable().forEach { if (it in toEscape) count++ }
        return if (count == 0) string else StringBuilder(n + count).apply {
            string.asIterable().forEach {
                if (it in toEscape) append('\\')
                append(it)
            }
        }.toString()
    }
}