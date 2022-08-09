package ir.smmh.markup

import ir.smmh.lingu.Language
import ir.smmh.lingu.TokenizationUtil.toCharSet
import ir.smmh.markup.Markup.Fragment.Effect.*
import ir.smmh.nile.or.Or

object TelegramMarkdown : Language.Markup {
    override fun compile(it: Markup.Text): String = when (it) {
        is Markup.Fragment.Atom -> escape(it.data, plain)
        is Markup.Fragment.Unescaped -> it.data
        is Markup.Fragment.Affected -> when (it.effect) {
            BOLD -> "*${this compile it.core}*"
            ITALIC -> "_${this compile it.core}_\r"
            UNDERLINE -> "__${this compile it.core}__"
            STRIKETHROUGH -> "~${this compile it.core}~"
        }
        is Markup.Fragment.InlineCode -> "`${escape(it.codeString, codeOnly)}`"
        is Markup.Fragment.Link -> {
            val url = it.url // bindFileExt(it.url)
            val bookmark = it.bookmark
            "[${this compile it.core}](" + escape(if (bookmark == "") url else "$url#$bookmark", urlOnly)
            // TODO test bookmarks
        }
        is Markup.Fragment.Span -> this compile it.core
        is Markup.Fragment.Multitude -> it.joinToString("") { this compile it }
        is Markup.Section.Paragraph -> (this compile it.contents) + "\n\n"
        is Markup.Section.Comment -> ""
        is Markup.Section.Quotation -> "“${this compile it.contents}”" + if (it.by == null) "" else ("\n— " + compile(it.by)) + "\n\n"
        is Markup.Section.CodeBlock -> "```\n${escape(it.code.string, codeOnly)}\n```\n\n"
        is Markup.Section.List -> StringBuilder().run {
            for (item in it) {
                append("• ")
                append(this@TelegramMarkdown compile Or.generalize(item))
                append('\n')
            }
            append('\n')
            toString()
            // TODO test nested lists
            // shiftRight(inner-list, 2)
        }
        Markup.Section.HorizontalRule -> "\n\n***\n\n"
        is Markup.Section.Heading -> StringBuilder().run {
            val title = this@TelegramMarkdown compile it.heading
            append(this@TelegramMarkdown compile Markup.Tools.bold(title))
            append("\n\n")
            for (section in it) {
                append(this@TelegramMarkdown compile section)
                append('\n')
            }
            append('\n')
            toString()
        }
        is Markup.Document -> StringBuilder().run {
            for (topHeading in it) {
                append(this@TelegramMarkdown compile topHeading)
                append('\n')
            }
            toString()
        }
        is Markup.Table -> it.toString()
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
        return if (count == 0) string else StringBuilder(n + count).run {
            string.asIterable().forEach {
                if (it in toEscape) append('\\')
                append(it)
            }
            toString()
        }
    }
}