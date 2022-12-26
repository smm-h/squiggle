package ir.smmh.forms

import ir.smmh.forms.Form.BlankSpace
import ir.smmh.forms.Form.IncompleteFormException
import ir.smmh.nile.Associative
import ir.smmh.nile.Dirty
import ir.smmh.nile.Sequential
import ir.smmh.nile.ListSequential
import ir.smmh.nile.or.FatOr
import ir.smmh.nile.or.Or
import ir.smmh.util.FileUtil.writeTo
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class FormImpl private constructor(
    override val title: String,
    private val sequence: Sequential.Mutable.CanChangeSize<Or<String, BlankSpace>> = ListSequential(),
    private val associative: Associative.MultiValue.Mutable<BlankSpace, String> = Associative.MultiValue.Mutable.empty(),
) : Form {

    private val string: String by Dirty(AtomicBoolean().also{
        sequence.changesToValues
    }) {
        StringBuilder().apply {
            val n = sequence.size
            for (i in 0 until n) {
                val thisOrThat = sequence.getAtIndex(i)
                if (thisOrThat.isThis) {
                    append(thisOrThat.getThis())
                } else {
                    val blankSpace = thisOrThat.getThat()
                    val values = associative.getAtPlace(blankSpace)
                    val count = values.size
                    if (blankSpace.acceptsCount(count)) append(blankSpace.compose(values))
                    else throw IncompleteFormException(blankSpace, blankSpace.countErrorMessage(count))
                }
            }
        }.toString()
    }

    override fun clone(deepIfPossible: Boolean) =
        copy("clone of $title")

    override fun specificThis() = this

    override fun append(blankSpace: BlankSpace) = sequence.append(make(blankSpace))
    override fun prepend(blankSpace: BlankSpace) = sequence.prepend(make(blankSpace))
    override fun append(other: Form) = sequence.appendAll((other as FormImpl).sequence.overValues)
    override fun prepend(other: Form) = sequence.prependAll((other as FormImpl).sequence.overValues)
    override fun append(text: String) = sequence.append(make(text))
    override fun prepend(text: String) = sequence.prepend(make(text))
    override fun append(c: Char) = sequence.append(make(c.toString()))
    override fun prepend(c: Char) = sequence.prepend(make(c.toString()))

    override fun copy(title: String): Form =
        FormImpl(title, sequence.clone(false), associative.clone(false))

    override fun generateToFile(destination: File, overwrite: Boolean) {
        if (overwrite || !destination.exists()) {
            generate() writeTo destination
        }
    }

    override fun enter(blankSpace: BlankSpace, entry: String) =
        associative.addAtPlace(blankSpace, entry)

    override fun enter(blankSpace: BlankSpace, entries: Sequential<String>) =
        associative.addAllAtPlace(blankSpace, entries.overValues)

    override fun enter(mappedEntries: Associative.MultiValue<BlankSpace, String>) =
        associative.addAllFrom(mappedEntries)

    override fun clear(blankSpace: BlankSpace) =
        associative.clearAtPlace(blankSpace)

    override fun generate(): String = string

    override fun toString(): String = string

    companion object {
        private fun make(string: String): Or<String, BlankSpace> =
            FatOr.makeThis(string)

        private fun make(blankSpace: BlankSpace): Or<String, BlankSpace> =
            FatOr.makeThat(blankSpace)
    }
}