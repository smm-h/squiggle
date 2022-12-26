@file:Suppress("unused")

package ir.smmh.forms

import ir.smmh.nile.*
import ir.smmh.nile.or.Or
import ir.smmh.nile.verbs.CanClone
import java.io.File
import java.io.IOException
import java.util.*

/**
 * A form is a plain text document with blank spaces to fill out or leave blank.
 * It is used for generating strings from patterns. Some spaces may accept more
 * than one value. Start with StringForm.empty() and chain methods to build your
 * form, and finish with generate(). The filling out methods do not mutate the
 * form, meaning you can use them more than once.
 */
interface Form : CanClone<Form> {
    fun copy(title: String): Form

    /**
     * This method helps you use a pre-made string form, fill it, and write its
     * contents to a file.
     *
     * @throws IncompleteFormException If filling out the form fails
     * @throws IOException             If writing to file fails
     */
    @Throws(IOException::class)
    fun generateToFile(destination: File, overwrite: Boolean)

    /**
     * @throws IncompleteFormException If filling out the form fails
     */
    fun generate(): String

    val title: String

    fun clear(blankSpace: BlankSpace)
    fun enter(blankSpace: BlankSpace, entry: String)
    fun append(blankSpace: BlankSpace)
    fun prepend(blankSpace: BlankSpace)
    fun append(other: Form)
    fun prepend(other: Form)
    fun append(text: String)
    fun prepend(text: String)
    fun append(c: Char)
    fun prepend(c: Char)
    fun enter(blankSpace: BlankSpace, entries: Sequential<String>)

//    fun enter(blankSpace: BlankSpace, vararg entries: String) =
//        enter(blankSpace, Sequential.of(*entries))

    fun enter(mappedEntries: Associative.MultiValue<BlankSpace, String>)

    interface BlankSpace {

        val title: String
        val minimumCount: Int
        val maximumCount: Int

        fun acceptsCount(count: Int): Boolean {
            val min = minimumCount
            val max = maximumCount
            return count >= min && (max == -1 || count <= max)
        }

        fun countErrorMessage(count: Int) = StringBuilder().apply {
            val min = minimumCount
            val max = maximumCount
            append("needs ")
            if (min == max) {
                append("exactly ")
                append(countToString(min))
            } else {
                append("at least ")
                append(countToString(min))
                if (max != -1)
                    append(", and at most ")
                append(countToString(max))
            }
            append(", got ")
            append(countToString(count))
            append(" instead")
        }.toString()

        fun compose(values: Sequential<String>): String
        fun compose(): String {
            return compose(Sequential.empty())
        }

        fun compose(singleValue: String): String =
            compose(SingleSequence(singleValue))

        fun compose(value1: String, value2: String): String =
            compose(DoubleSequence(value1, value2))

        abstract class ZeroOrMore(title: String) : Impl(title, 0, -1)
        abstract class OneOrMore(title: String) : Impl(title, 1, -1)
        abstract class ZeroOrOne(title: String) : Impl(title, 0, 1)
        abstract class ExactlyOne(title: String) : Impl(title, 1, 1)
        abstract class Impl(
            override val title: String,
            override val minimumCount: Int,
            override val maximumCount: Int,
        ) : BlankSpace {
            init {
                require(!(minimumCount < 0 || maximumCount != -1 && (minimumCount > maximumCount || maximumCount <= 0))) { "invalid range" }
            }
        }

        companion object {
            fun itself(title: String): BlankSpace {
                return object : ExactlyOne(title) {
                    override fun compose(values: Sequential<String>) = values.singleton
                }
            }

            fun countToString(count: Int): String {
                return if (count == 1) "1 entry" else "$count entries"
            }
        }
    }

    class IncompleteFormException constructor(
        val blankSpace: BlankSpace? = null,
        extraMessage: String? = null
    ) : RuntimeException(makeMessage(blankSpace, extraMessage)) {
        constructor(blankSpace: BlankSpace) : this(blankSpace, null)
        constructor(message: String) : this(null, message)

        companion object {
            private fun makeMessage(blankSpace: BlankSpace?, extraMessage: String?): String {
                var message = "Missing blankspace"
                if (blankSpace != null) message += ": " + blankSpace.title.uppercase(Locale.ROOT)
                if (extraMessage != null) message += ", $extraMessage"
                return message
            }
        }
    }
}