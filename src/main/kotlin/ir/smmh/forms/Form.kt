@file:Suppress("unused")

package ir.smmh.forms

import ir.smmh.nile.*
import ir.smmh.nile.or.Or
import ir.smmh.nile.verbs.CanClone
import org.jetbrains.annotations.Contract
import java.io.File
import java.io.IOException
import java.util.*

/**
 * A form is a plain text document with blank spaces to fill out or leave blank.
 * It is used for generating strings from patterns. Some spaces may accept more
 * than one value. Start with StringForm.empty() and chain methods to build your
 * form, and finish with generate(). The filling out methods do not mutate the
 * form, meaning you can use it more than once.
 */
interface Form : Mut.Able, CanClone<Form> {
    fun copy(title: String, mut: Mut = Mut()): Form

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
    fun getTitle(): String

    @Contract("_->this")
    fun clear(blankSpace: BlankSpace): Form

    @Contract("_, _->this")
    fun enter(blankSpace: BlankSpace, entry: String?): Form
    fun isFilledOut(): Boolean

    @Contract("_->this")
    fun append(blankSpace: BlankSpace): Form

    @Contract("_->this")
    fun prepend(blankSpace: BlankSpace): Form

    @Contract("_->this")
    fun append(form: Form): Form

    @Contract("_->this")
    fun prepend(form: Form): Form

    @Contract("_->this")
    fun append(text: String): Form

    @Contract("_->this")
    fun prepend(text: String): Form

    @Contract("_->this")
    fun append(c: Char): Form

    @Contract("_->this")
    fun prepend(c: Char): Form

    @Contract("_, _->this")
    fun enter(blankSpace: BlankSpace, entries: Sequential<String>): Form

//    @Contract("_, _->this")
//    fun enter(blankSpace: BlankSpace, vararg entries: String): Form {
//        enter(blankSpace, Sequential.of(*entries))
//        return this
//    }

    @Contract("_->this")
    fun enter(mappedEntries: Associative.MultiValue<BlankSpace, String>): Form
    fun getSequence(): Sequential<Or<String, BlankSpace>>
    interface BlankSpace {
        fun getTitle(): String
        fun acceptsCount(count: Int): Boolean {
            val min = getMinimumCount()
            val max = getMaximumCount()
            return count >= min && (max == -1 || count <= max)
        }

        fun countErrorMessage(count: Int): String {
            val min = getMinimumCount()
            val max = getMaximumCount()
            val builder = StringBuilder()
            builder
                .append("needs ")
            if (min == max) {
                builder
                    .append("exactly ")
                    .append(countToString(min))
            } else {
                builder
                    .append("at least ")
                    .append(countToString(min))
                if (max != -1) builder
                    .append(", and at most ")
                    .append(countToString(max))
            }
            builder
                .append(", got ")
                .append(countToString(count))
                .append(" instead")
            return builder.toString()
        }

        fun getMinimumCount(): Int
        fun getMaximumCount(): Int
        fun compose(values: Sequential<String>): String
        fun compose(): String {
            return compose(Sequential.empty())
        }

        fun compose(singleValue: String): String {
            return compose(SingleSequence(singleValue))
        }

        fun compose(value1: String, value2: String): String {
            return compose(DoubleSequence(value1, value2))
        }

        abstract class ZeroOrMore(title: String) : Impl(title, 0, -1)
        abstract class OneOrMore(title: String) : Impl(title, 1, -1)
        abstract class ZeroOrOne(title: String) : Impl(title, 0, 1)
        abstract class ExactlyOne(title: String) : Impl(title, 1, 1)
        abstract class Impl(title: String, minimumCount: Int, maximumCount: Int) : BlankSpace {
            private val title: String
            private val minimumCount: Int
            private val maximumCount: Int
            override fun getTitle(): String {
                return title
            }

            override fun getMinimumCount(): Int {
                return minimumCount
            }

            override fun getMaximumCount(): Int {
                return maximumCount
            }

            init {
                require(!(minimumCount < 0 || maximumCount != -1 && (minimumCount > maximumCount || maximumCount <= 0))) { "invalid range" }
                this.title = title
                this.minimumCount = minimumCount
                this.maximumCount = maximumCount
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
        private val blankSpace: BlankSpace? = null,
        extraMessage: String? = null
    ) : RuntimeException(
        makeMessage(
            blankSpace, extraMessage
        )
    ) {
        constructor(blankSpace: BlankSpace) : this(blankSpace, null)
        constructor(message: String) : this(null, message)

        fun getBlankSpace(): BlankSpace? {
            return blankSpace
        }

        companion object {
            private fun makeMessage(blankSpace: BlankSpace?, extraMessage: String?): String {
                var message = "Missing blankspace"
                if (blankSpace != null) message += ": " + blankSpace.getTitle().uppercase(Locale.ROOT)
                if (extraMessage != null) message += ", $extraMessage"
                return message
            }
        }
    }
}