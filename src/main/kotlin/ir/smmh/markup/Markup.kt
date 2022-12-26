package ir.smmh.markup

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.markup.Markup.Table.Builder
import ir.smmh.nile.Multitude
import ir.smmh.nile.Change
import ir.smmh.nile.or.FatOr
import ir.smmh.nile.or.Or
import ir.smmh.nile.table.Tabular
import ir.smmh.nile.verbs.CanAppendTo
import ir.smmh.nile.verbs.CanPrependTo

/**
 * A language-agnostic intersection of the markup capabilities of HTML and Markdown.
 * @see Html
 * @see Markdown
 * @see NoMarkup
 */
object Markup {

    private val tab = "  "

    private val dash = Markup.Tools.atom("-")

    object Tools : InlineHelpers

    sealed interface InlineHelpers {

        fun atom(text: String, escape: Boolean = true) =
            if (escape) Fragment.Atom(text) else Fragment.Unescaped(text)

        fun line(): Fragment =
            Fragment.Multitude()

        fun bold(fragment: Fragment): Fragment =
            Fragment.Affected(fragment, Fragment.Effect.BOLD)

        fun italic(fragment: Fragment): Fragment =
            Fragment.Affected(fragment, Fragment.Effect.ITALIC)

        fun underline(fragment: Fragment): Fragment =
            Fragment.Affected(fragment, Fragment.Effect.UNDERLINE)

        fun strike(fragment: Fragment): Fragment =
            Fragment.Affected(fragment, Fragment.Effect.STRIKETHROUGH)

        fun tex(fragment: Fragment): Fragment =
            Fragment.Affected(fragment, Fragment.Effect.TEX)

        fun code(codeString: String): Fragment =
            Fragment.InlineCode(codeString)

        fun link(fragment: Fragment, url: String, bookmark: String = ""): Fragment =
            Fragment.Link(fragment, url, bookmark)

        fun bold(text: String) =
            bold(atom(text))

        fun italic(text: String) =
            italic(atom(text))

        fun underline(text: String) =
            underline(atom(text))

        fun strike(text: String) =
            strike(atom(text))

        fun tex(text: String) =
            tex(atom(text))

        fun link(text: String, url: String, bookmark: String = "") =
            link(atom(text), url, bookmark)

        fun span(core: Fragment, attributes: String) =
            Fragment.Span(core, attributes)

        fun span(text: String, attributes: String) =
            span(atom(text), attributes)
    }

    class Document(val name: String? = null, block: (Document.() -> Unit)? = null) : Text(), InlineHelpers,
        Iterable<Section.Heading> {

        private val topHeadings: MutableList<Section.Heading> = ArrayList()

        override fun iterator(): Iterator<Section.Heading> = topHeadings.iterator()

        init {
            block?.invoke(this)
        }

        fun heading(heading: Fragment, block: (Section.Heading.() -> Unit)? = null) =
            Section.Heading(heading, block).also { topHeadings.add(it) }

        fun heading(heading: String, block: (Section.Heading.() -> Unit)? = null) =
            heading(Fragment.Atom(heading), block)

        override fun toString(depth: Int): String {
            val beforeEach = tab.repeat(depth)
            return topHeadings.joinToString("\n", "! $name\n") {
                beforeEach + it.toString(depth + 1)
            }
        }

        fun toCode(markupLanguage: Language.Markup) =
            markupLanguage.code(this)

        fun generate(markupLanguage: Language.Markup, metadata: String?) =
            markupLanguage.compile(this, metadata)
    }

    sealed class Text {
        abstract fun toString(depth: Int): String

        fun toString(language: Language.Markup): String = language.compile(this)

        override fun toString() = toString(1)
    }

    sealed class Section : Text() {

        fun toDocument(title: String) = Document() { heading(title) { addSection(this@Section) } }

        interface CanContainList {
            fun list(numbered: Boolean = false, block: (Section.List.() -> Unit)? = null): Section.List
        }

        class Heading(val heading: Fragment, block: (Heading.() -> Unit)? = null) : Section(), Iterable<Section>,
            CanContainList {
            private val sections: MutableList<Section> = ArrayList()

            override fun iterator(): Iterator<Section> = sections.iterator()

            override fun toString(depth: Int): String {
                val beforeEach = tab.repeat(depth)
                return sections.joinToString("\n", "h $heading\n") {
                    beforeEach + it.toString(depth + 1)
                }
            }

            constructor(heading: String, block: (Heading.() -> Unit)? = null) : this(Fragment.Atom(heading), block)

            init {
                block?.invoke(this)
            }

            fun addSection(section: Section) =
                sections.add(section)

            fun heading(heading: Fragment, block: (Section.Heading.() -> Unit)? = null) =
                Section.Heading(heading, block).also(::addSection)

            fun heading(heading: String, block: (Section.Heading.() -> Unit)? = null) =
                heading(Fragment.Atom(heading), block)

            fun paragraph(contents: String) = paragraph(Fragment.Atom(contents))
            fun paragraph(contents: Fragment) =
                Section.Paragraph(contents).also(::addSection)

            fun comment(contents: String) = comment(Fragment.Atom(contents))
            fun comment(contents: Fragment) =
                Section.Comment(contents).also(::addSection)

            fun codeBlock(contents: String, language: Language? = null) =
                codeBlock(Code(contents, language))

            fun codeBlock(code: Code) =
                Section.CodeBlock(code).also(::addSection)

            fun quotation(contents: Fragment, by: Fragment? = null, block: (Section.Quotation.() -> Unit)? = null) =
                Section.Quotation(contents, by, block).also(::addSection)

            override fun list(numbered: Boolean, block: (Section.List.() -> Unit)?) =
                Section.List(numbered, block).also(::addSection)

            fun horizontalRule() =
                Section.HorizontalRule.also(::addSection)

            fun tex(tex: String) =
                Section.TeX(tex).also(::addSection)
        }

        class Paragraph(val contents: Fragment) : Section() {
            override fun toString(depth: Int) = "p ${contents.toString(depth + 1)}"
        }

        class Comment(val contents: Fragment) : Section() {
            override fun toString(depth: Int) = "// ${contents.toString(depth + 1)}"
        }

        class CodeBlock(val code: Code) : Section() {
            override fun toString(depth: Int) = "c ${code.string}"
        }

        class Quotation(val contents: Fragment, val by: Fragment?, block: (Quotation.() -> Unit)? = null) : Section() {
            override fun toString(depth: Int) =
                "q ${contents.toString(depth + 1)}\tby: ${by?.toString(depth + 1) ?: "?"}"

            init {
                block?.invoke(this)
            }
        }

        class List(val numbered: Boolean, block: (List.() -> Unit)? = null) : Section(), Iterable<Or<Fragment, List>>,
            CanContainList {
            private val items: MutableList<Or<Fragment, List>> = ArrayList()

            override fun iterator(): Iterator<Or<Fragment, List>> = items.iterator()

            init {
                block?.invoke(this)
            }

            fun item(fragment: Fragment) =
                fragment.also { items.add(FatOr.makeThis(it)) }

            fun item(text: String) = item(Fragment.Atom(text))

            override fun toString(depth: Int): String {
                val beforeEach = tab.repeat(depth) + if (numbered) "# " else "* "
                return items.joinToString("\n", "l\n") {
                    beforeEach + Or.generalize(it).toString(depth + 1)
                }
            }

            override fun list(numbered: Boolean, block: (Section.List.() -> Unit)?) =
                Section.List(numbered, block).also { items.add(FatOr.makeThat(it)) }

            fun sort() {
                items.sortBy { NoMarkup.compile(Or.generalize(it)) }
            }
        }

        object HorizontalRule : Section() {
            override fun toString(depth: Int) = "---"
        }

        class TeX(val tex: String) : Section() {
            override fun toString(depth: Int) = tex
        }
    }

    sealed class Fragment : Text() {
        class Atom(val data: String) : Fragment() {
            override fun toString(depth: Int) = data
        }

        class Unescaped(val data: String) : Fragment() {
            override fun toString(depth: Int) = data
        }

        class Affected(val core: Fragment, val effect: Effect) : Fragment() {
            override fun toString(depth: Int) = "($effect: ${core.toString(depth + 1)})"
        }

        class InlineCode(val codeString: String) : Fragment() {
            override fun toString(depth: Int) = "(c: $codeString)"
        }

        // TODO url: URL/URI?
        class Link(val core: Fragment, val url: String, val bookmark: String) : Fragment() {
            override fun toString(depth: Int) = "(${core.toString(depth + 1)}->$url#$bookmark)"
        }

        class Span(val core: Fragment, val attributes: String) : Fragment() {
            override fun toString(depth: Int) = "(#: ${core.toString(depth + 1)})"
        }

        enum class Effect {
            BOLD, ITALIC, UNDERLINE, STRIKETHROUGH, TEX;

            override fun toString() = when (this) {
                BOLD -> "b"
                ITALIC -> "i"
                UNDERLINE -> "_"
                STRIKETHROUGH -> "~"
                TEX -> "$"
            }
        }

        class Multitude : Fragment(), ir.smmh.nile.Multitude, Iterable<Fragment>, CanAppendTo<Fragment>,
            CanPrependTo<Fragment> {
            override val changesToSize: Change = Change()
            override val size get() = list.size
            private val list: MutableList<Fragment> = ArrayList()
            override fun iterator() = list.iterator()
            override fun append(toAppend: Fragment) {
                changesToSize.beforeChange()
                if (toAppend is Multitude) list.addAll(toAppend.list)
                else list.add(toAppend)
                changesToSize.afterChange()
            }

            override fun prepend(toPrepend: Fragment) {
                changesToSize.beforeChange()
                if (toPrepend is Multitude) list.addAll(0, toPrepend.list)
                else list.add(0, toPrepend)
                changesToSize.afterChange()
            }

            override fun toString(depth: Int): String {
                return list.joinToString("") { it.toString(depth + 1) }
            }
        }

        operator fun plus(string: String) = plus(Atom(string))
        operator fun plus(other: Fragment): Fragment {
            val fragment = this
            val multitude = if (fragment is Multitude) fragment else Multitude().also { it.add(fragment) }
            multitude.append(other)
            return multitude
        }

//        companion object {
//            operator fun String.plus(fragment: Fragment): Fragment {
//                val multitude = if (fragment is Multitude) fragment else Multitude().also { it.add(fragment) }
//                multitude.prepend(Atom(this))
//                return multitude
//            }
//        }
    }

    fun <T> Iterable<T>.joinToFragment(
        separator: Fragment = Tools.atom(", "),
        prefix: Fragment = Tools.atom(""),
        postfix: Fragment = Tools.atom(""),
        limit: Int = -1,
        truncated: Fragment = Tools.atom("..."),
        transform: ((T) -> Fragment)? = null
    ): Fragment {
        val buffer = Fragment.Multitude()
        buffer.append(prefix)
        var count = 0
        for (element in this) {
            if (++count > 1) buffer.append(separator)
            if (limit < 0 || count <= limit) {
                buffer.append(transform?.invoke(element) ?: Tools.atom(element.toString()))
            } else break
        }
        if (limit >= 0 && count > limit) buffer.append(truncated)
        buffer.append(postfix)
        return buffer
    }

    /**
     * Unlike `Tabular`, this class does not allow columns of arbitrary types,
     * sorting, shuffling, filtering, data mutation, schema mutation, etc. If
     * you want to build an object of this class using a table that has those
     * qualities, try `Builder`
     * @see Tabular
     * @see Builder
     */
    class Table(
        val showIndexColumn: Boolean = false,
        val rowHyperdata: Map<Int, String> = HashMap(),
        val rowHyperdataIfNull: String = "",
    ) : Markup.Section(), Multitude, Iterable<Int> {

        inner class Column(
            val titleFragment: Markup.Fragment,
            val titleHyperdata: String? = null,
            val cellFragments: Map<Int, Markup.Fragment> = HashMap(),
            val cellHyperdata: Map<Int, String> = HashMap(),
            val cellFragmentIfNull: Markup.Fragment = dash,
            val cellHyperdataIfNull: String = "",
            val cellDirection: TextDirection = TextDirection.LTR,
            val titleDirection: TextDirection? = TextDirection.CENTERED,
        ) {
            operator fun get(key: Int): Fragment = cellFragments[key] ?: cellFragmentIfNull

            init {
                columns.add(this)
            }
        }

        /**
         * Makes it easy to build a `Markup.Table` from a `Tabular`
         * @see Tabular
         */
        class Builder {

            // table settings
            var showIndexColumn: Boolean = false
            val rowHyperdata: MutableMap<Int, String> = HashMap()
            var rowHyperdataIfNull: String = ""

            // column settings
            val titleFragment: MutableMap<Tabular.Column<*>, Fragment> = HashMap()
            val titleHyperdata: MutableMap<Tabular.Column<*>, String> = HashMap()
            val cellFragmentIfNull: MutableMap<Tabular.Column<*>, Markup.Fragment> = HashMap()
            val cellHyperdataIfNull: MutableMap<Tabular.Column<*>, String> = HashMap()
            val cellDirection: MutableMap<Tabular.Column<*>, TextDirection> = HashMap()
            val titleDirection: MutableMap<Tabular.Column<*>, TextDirection?> = HashMap()

            private val fragmentMakers: MutableMap<Tabular.Column<*>, Any> = HashMap()
            private val hyperdataMakers: MutableMap<Tabular.Column<*>, Any> = HashMap()

            fun <T> makeFragment(column: Tabular.Column<T>, function: (T) -> Fragment) {
                fragmentMakers[column] = function
            }

            fun <T> makeHyperdata(column: Tabular.Column<T>, function: (T) -> String?) {
                hyperdataMakers[column] = function
            }

            fun build(tabular: Tabular) = Table(
                showIndexColumn,
                rowHyperdata,
                rowHyperdataIfNull,
            ).apply {

                // add the pre-ordered row keys
                tabular.forEach { rows.add(it) }

                // add the index column if it necessary
                if (showIndexColumn) {
                    var k = 0
                    Column(
                        Markup.Tools.atom("#"),
                        cellFragments = tabular.associateWith { Markup.Tools.atom((k++).toString()) },
                        cellDirection = TextDirection.RTL,
                    )
                }

                // add the columns
                @Suppress("UNCHECKED_CAST")
                tabular.overColumns().forEach { c ->
                    val fragmentMaker = (fragmentMakers[c] ?: ::defaultFragmentMaker) as (Any) -> Fragment
                    val hyperdataMaker = (hyperdataMakers[c] ?: ::defaultHyperdataMaker) as (Any) -> String
                    val tf = titleFragment[c] ?: Markup.Tools.atom(c.name)
                    val th = titleHyperdata[c]
                    Column(
                        if (th == null) tf else Tools.span(tf, th),
                        cellFragments = tabular.filter { c[it] != null }.associateWith { fragmentMaker(c[it]!!) },
                        cellHyperdata = tabular.filter { c[it] != null }.associateWith { hyperdataMaker(c[it]!!) },
                    )
                }
            }

            companion object {
                private fun defaultFragmentMaker(it: Any): Fragment =
                    if (it is Fragment) it else Markup.Tools.atom(it.toString())

                @Suppress("UNUSED_PARAMETER")
                private fun defaultHyperdataMaker(it: Any): String? = null
            }
        }

        private val rows: MutableList<Int> = ArrayList()
        private val columns: MutableList<Column> = ArrayList()

        override val size get() = rows.size

        override fun toString(depth: Int): String = toString()
        override fun toString(): String = NoMarkup.compile(this)

        override fun iterator() = rows.iterator()
        fun overColumns() = Iterable { columns.iterator() }
    }
}