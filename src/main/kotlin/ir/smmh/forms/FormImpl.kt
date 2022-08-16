package ir.smmh.forms

import ir.smmh.forms.Form.BlankSpace
import ir.smmh.forms.Form.IncompleteFormException
import ir.smmh.nile.Associative
import ir.smmh.nile.Mut
import ir.smmh.nile.Sequential
import ir.smmh.nile.SequentialImpl
import ir.smmh.nile.or.FatOr
import ir.smmh.nile.or.Or
import ir.smmh.util.FileUtil.writeTo
import java.io.File
import java.io.IOException

class FormImpl private constructor(
    private val title: String,
    private val sequence: Sequential.Mutable.VariableSize<Or<String, BlankSpace>>,
    private val associative: Associative.MultiValue.Mutable<BlankSpace, String>,
    override val mut: Mut = sequence.mut
) : Form {
    @Transient
    private var string: String? = null

    @Transient
    private var isFilledOut = false

    constructor(title: String) : this(
        title,
        SequentialImpl<Or<String, BlankSpace>>(),
        Associative.MultiValue.Mutable.empty<BlankSpace, String>(),
        Mut()
    )

    override fun clone(deepIfPossible: Boolean): Form {
        return copy("clone of $title")
    }

    override fun specificThis(): Form {
        return this
    }

    override fun append(blankSpace: BlankSpace): Form {
        sequence.append(make(blankSpace))
        return this
    }

    override fun prepend(blankSpace: BlankSpace): Form {
        sequence.prepend(make(blankSpace))
        return this
    }

    override fun append(form: Form): Form {
        sequence.appendAll(form.getSequence())
        return this
    }

    override fun prepend(form: Form): Form {
        sequence.prependAll(form.getSequence())
        return this
    }

    override fun append(text: String): Form {
        sequence.append(make(text))
        return this
    }

    override fun prepend(text: String): Form {
        sequence.prepend(make(text))
        return this
    }

    override fun append(c: Char): Form {
        sequence.append(make(c.toString()))
        return this
    }

    override fun prepend(c: Char): Form {
        sequence.prepend(make(c.toString()))
        return this
    }

    override fun copy(title: String, mut: Mut): Form =
        FormImpl(title, sequence.clone(false), associative.clone(false))

    @Throws(IOException::class)
    override fun generateToFile(destination: File, overwrite: Boolean) {
        if (overwrite || !destination.exists()) {
            generate() writeTo destination
        }
    }

    override fun enter(blankSpace: BlankSpace, entry: String?): Form {
        if (entry != null) associative.addAtPlace(blankSpace, entry)
        return this
    }

    override fun enter(blankSpace: BlankSpace, entries: Sequential<String>): Form {
        associative.addAllAtPlace(blankSpace, entries)
        return this
    }

    override fun getSequence(): Sequential<Or<String, BlankSpace>> {
        return sequence
    }

    override fun enter(mappedEntries: Associative.MultiValue<BlankSpace, String>): Form {
        associative.addAllFrom(mappedEntries)
        return this
    }

    override fun generate(): String {
        if (isFilledOut()) return string!!
        throw IncompleteFormException("the form is not filled out")
    }

    override fun getTitle(): String {
        return title
    }

    override fun clear(blankSpace: BlankSpace): Form {
        associative.clearAtPlace(blankSpace)
        return this
    }

    override fun isFilledOut(): Boolean {
        mut.clean()
        return isFilledOut
    }

    override fun toString(): String {
        return if (isFilledOut()) string!! else "INCOMPLETE FORM"
    }

    companion object {
        private fun make(string: String): Or<String, BlankSpace> {
            return FatOr.makeThis(string)
        }

        private fun make(blankSpace: BlankSpace): Or<String, BlankSpace> {
            return FatOr.makeThat(blankSpace)
        }
    }

    init {
        mut.onClean.add {
            isFilledOut = false
            val builder = StringBuilder()
            val n = sequence.size
            for (i in 0 until n) {
                val thisOrThat = sequence.getAtIndex(i)
                if (thisOrThat.isThis) {
                    builder.append(thisOrThat.getThis())
                } else {
                    val blankSpace = thisOrThat.getThat()
                    val values = associative.getAtPlace(blankSpace)
                    val count = values.size
                    if (blankSpace.acceptsCount(count)) builder.append(blankSpace.compose(values)) else throw IncompleteFormException(
                        blankSpace,
                        blankSpace.countErrorMessage(count)
                    )
                }
            }
            string = builder.toString()
            isFilledOut = true
        }
    }
}