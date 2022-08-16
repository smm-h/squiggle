@file:Suppress("unused")

package codesmith

import ir.smmh.nile.Mut

class FragmentedString(
    private val chunkSize: Int = 256,
    override val mut: Mut = Mut()
) : Typeable, Mut.Able {

    private val fragments: MutableList<Fragment> = ArrayList()
    private var typeable: TypeableFragment? = null
    private var typeableIndex = 0

    constructor(string: String) : this(string, (string.length / 16).coerceIn(32, 1024))

    constructor(string: String, chunkSize: Int) : this(chunkSize) {
        append(string)
    }

    private fun append(string: String) {
        if (chunkSize == 0) {
            fragments.add(RealFragment(string))
        } else {
            var p = 0
            while (p + chunkSize < string.length) {
                fragments.add(RealFragment(string.substring(p, p + chunkSize)))
                p += chunkSize
            }
            fragments.add(RealFragment(string.substring(p)))
        }
    }

    override val string: String
        get() {
            val builder = StringBuilder()
            for (fragment in fragments) builder.append(fragment.string)
            return builder.toString()
        }

    fun defragment() {
        val s = string
        fragments.clear()
        append(s)
    }

    fun defragment(from: Int, to: Int) {
        val builder = StringBuilder()

        // find out which fragments each position belongs to
        val indexFirst = indexOfFragmentContaining(from)
        val indexLast = indexOfFragmentContaining(to)

        // remove all other fragments in between, but gather their strings
        for (index in indexLast downTo indexFirst)
            builder.insert(0, fragments.removeAt(index).string)

        // insert back all the gathered string as a single fragment
        fragments.add(indexFirst, RealFragment(builder.toString()))
    }

    override fun clear() {
        mut.preMutate()
        fragments.clear()
        mut.mutate()
    }

    private fun indexOfFragmentContaining(position: Int): Int {
        var p = position
        var i = -1
        while (p > 0) p -= fragments[++i].length
        return i
    }

    private fun getFragmentStart(index: Int): Int {
        var p = 0
        var i = index
        while (i > 0) p += fragments[--i].length
        return p
    }

    fun insert(position: Int, string: String) {

        mut.preMutate()

        // find out which fragment contains this position
        var p = position
        var i = indexOfFragmentContaining(p)

        // turn absolute position into relative position
        p -= getFragmentStart(i)

        // delete the fragment found so we can break it in half
        val fragment = fragments.removeAt(i)

        // insert the left half
        if (fragment.lengthFromStart(p) != 0) fragments.add(i++, fragment.fragmentFromStart(p))

        // insert the middle
        if (chunkSize == 0) {
            fragments.add(i++, RealFragment(string))
        } else {
            var q = 0
            while (q + chunkSize < string.length) {
                fragments.add(i++, RealFragment(string.substring(q, q + chunkSize)))
                q += chunkSize
            }
            fragments.add(i++, RealFragment(string.substring(q)))
        }

        // insert the right half
        if (fragment.lengthToEnd(p) != 0) fragments.add(i, fragment.fragmentToEnd(p))

        mut.mutate()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (fragment in fragments) builder.append("[").append(fragment.string).append("]")
        return builder.toString()
    }

    override val length: Int
        get() {
            var length = 0
            for (fragment in fragments) length += fragment.length
            return length
        }

    fun delete(from: Int, to: Int) {

        mut.preMutate()

        // find out which fragments each position belongs to
        val indexFirst = indexOfFragmentContaining(from)
        val indexLast = indexOfFragmentContaining(to)

        // turn absolute positions into relative positions
        val rFrom = from - getFragmentStart(indexFirst)
        val rTo = to - getFragmentStart(indexLast)

        // if they belong to the same fragment,
        if (indexFirst == indexLast) {

            // remove that fragment so we can break it into three parts
            val fragment = fragments.removeAt(indexFirst)

            // insert back the third one-third
            if (fragment.lengthToEnd(rTo) != 0)
                fragments.add(indexFirst, fragment.fragmentToEnd(rTo))

            // insert back the first one-third
            if (fragment.lengthFromStart(rFrom) != 0)
                fragments.add(indexFirst, fragment.fragmentFromStart(rFrom))

        } else {

            // remove all fragments involved from last to first, but keep a reference to the
            // first and last one so we can break them

            // remove the last fragment
            val fragmentLast = fragments.removeAt(indexLast)

            // remove all other fragments in between
            if (indexLast > indexFirst + 1) {
                fragments.subList(indexFirst + 1, indexLast).clear()
            }

            // remove the first fragment
            val fragmentFirst = fragments.removeAt(indexFirst)

            // insert back the second half of the last fragment
            if (fragmentLast.lengthToEnd(rTo) != 0)
                fragments.add(indexFirst, fragmentLast.fragmentToEnd(rTo))

            // insert back the first half of the first fragment
            if (fragmentFirst.lengthFromStart(rFrom) != 0)
                fragments.add(indexFirst, fragmentFirst.fragmentFromStart(rFrom))
        }

        mut.mutate()
    }

    private fun isTyping(): Boolean {
        return typeable != null
    }

    private fun startTyping() {

        // find out in which fragment our caret is
        val index = indexOfFragmentContaining(caret)

        // if while typing we have moved into another fragment
        if (isTyping() && typeableIndex != index) {
            finishTyping()
        }

        // if we are not typing
        if (!isTyping()) {

            // if that fragment is not typeable, make it so
            if (fragments[index] !is TypeableFragment) fragments[index] = TypeableFragment(fragments[index].string)
            typeable = fragments[index] as TypeableFragment
            typeableIndex = index
        }
    }

    private fun finishTyping() {

        // if was typing
        if (isTyping()) {

            // turn that typeable fragment into a non-typeable one
            fragments[typeableIndex] = RealFragment(fragments[typeableIndex].string)

            // signal that we are not typing anymore
            typeable = null
        }
    }

    override fun type(character: Char) {
        mut.preMutate()
        startTyping()
        val p = caret - getFragmentStart(typeableIndex)
        typeable!!.string = typeable!!.string.substring(0, p) + character + typeable!!.string.substring(p)
        typeable!!.length++
        caret++
        mut.mutate()
    }

    override fun pressBackspace() {
        mut.preMutate()
        startTyping()
        val p = caret - getFragmentStart(typeableIndex)
        typeable!!.string = typeable!!.string.substring(0, p - 1) + typeable!!.string.substring(p)
        typeable!!.length--
        caret--
        mut.mutate()
    }

    override var caret: Int = -1
        set(value) {
            finishTyping()
            field = value
        }

    private interface Fragment {
        val length: Int
        val string: String
        fun fragmentFromStart(toIndex: Int): Fragment
        fun fragmentToEnd(fromIndex: Int): Fragment
        fun lengthFromStart(toIndex: Int): Int
        fun lengthToEnd(fromIndex: Int): Int
    }

    private abstract class AbstractFragment(final override var length: Int) : Fragment {
        init {
            if (length == 0) throw NullPointerException("String fragment length cannot be zero")
        }
    }

    private class RealFragment(string: String) : AbstractFragment(string.length) {
        val array: CharArray = string.toCharArray()
        override val string: String
            get() = String(array)

        override fun fragmentFromStart(toIndex: Int): Fragment = FakeFragment(this, 0, toIndex)
        override fun fragmentToEnd(fromIndex: Int): Fragment = FakeFragment(this, fromIndex, length)
        override fun lengthFromStart(toIndex: Int): Int = toIndex
        override fun lengthToEnd(fromIndex: Int): Int = length - fromIndex
    }

    private class FakeFragment(val parent: RealFragment, val offset: Int, end: Int) :
        AbstractFragment(end - offset) {
        override val string: String
            get() = String(parent.array, offset, length)

        override fun fragmentFromStart(toIndex: Int): Fragment = FakeFragment(parent, offset, toIndex)
        override fun fragmentToEnd(fromIndex: Int): Fragment = FakeFragment(parent, offset + fromIndex, offset + length)
        override fun lengthFromStart(toIndex: Int): Int = toIndex - offset
        override fun lengthToEnd(fromIndex: Int): Int = length - fromIndex
    }

    private class TypeableFragment(override var string: String) : AbstractFragment(string.length) {
        override fun fragmentFromStart(toIndex: Int): Fragment = RealFragment(string.substring(0, toIndex))
        override fun fragmentToEnd(fromIndex: Int): Fragment = RealFragment(string.substring(fromIndex, length))
        override fun lengthFromStart(toIndex: Int): Int = toIndex
        override fun lengthToEnd(fromIndex: Int): Int = length - fromIndex
    }
}