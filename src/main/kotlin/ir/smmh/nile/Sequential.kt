@file:Suppress("unused")

package ir.smmh.nile


import ir.smmh.nile.verbs.*
import java.util.*
import java.util.Collections.max


interface Sequential<T> :
    CanIterateOverValues<T>,
    CanIterateOverValuesInReverse<T>,
    CanGetAtIndex<T>,
    CanContainValue<T>,
    CanClone<Sequential<T>> {

    val head: T get() = getAtIndex(0)
    val tail: Sequential<T>
        get() {
            val source = this
            return object : AbstractSequential<T>() {
                override fun getAtIndex(index: Int): T = source.getAtIndex(index + 1)
                override val size: Int get() = source.size - 1
            }
        }

    @Suppress("UNCHECKED_CAST")
    fun toArray(): Array<T> = arrayOfNulls<Any>(size)
        .also { for (i in it.indices) it[i] = getAtIndex(i) } as Array<T>

    fun toIntArray(toInt: (T) -> Int) = IntArray(size)
        .also { for (i in it.indices) it[i] = toInt(getAtIndex(i)) }

    fun toLongArray(toLong: (T) -> Long) = LongArray(size)
        .also { for (i in it.indices) it[i] = toLong(getAtIndex(i)) }

    fun toFloatArray(toFloat: (T) -> Float) = FloatArray(size)
        .also { for (i in it.indices) it[i] = toFloat(getAtIndex(i)) }

    fun toDoubleArray(toDouble: (T) -> Double) = DoubleArray(size)
        .also { for (i in it.indices) it[i] = toDouble(getAtIndex(i)) }

    fun toBooleanArray(toBoolean: (T) -> Boolean) = BooleanArray(size)
        .also { for (i in it.indices) it[i] = toBoolean(getAtIndex(i)) }

    fun toCharArray(toChar: (T) -> Char) = CharArray(size)
        .also { for (i in it.indices) it[i] = toChar(getAtIndex(i)) }

    fun filterOutOfPlace(toTest: (T) -> Boolean): Sequential<T> = Mutable.CanChangeSize.of<T>(ArrayList(count(toTest)))
        .also { for (value in overValues) if (toTest(value)) it.append(value) }

    fun <R> applyOutOfPlace(toApply: (T) -> R): Sequential<R> = Mutable.CanChangeSize.of<R>(ArrayList(size))
        .also { for (value in overValues) it.append(toApply(value)) }

    fun asList(): List<T> = object : AbstractList<T>() {
        override val size by this@Sequential::size
        override fun get(index: Int): T? = getAtIndex(index)
    }

    override fun isEmpty() = size == 0
    override fun containsValue(toCheck: T) = findFirst(toCheck) != -1
    override fun doesNotContainValue(toCheck: T) = findFirst(toCheck) == -1

    fun count(toTest: (T) -> Boolean, start: Int = 0, end: Int = size): Int {
        var count = 0
        for (i in start until end) if (toTest(getAtIndex(i))) count++
        return count
    }

    fun count(toCount: T, start: Int = 0, end: Int = size): Int {
        var count = 0
        for (i in start until end) if (getAtIndex(i) == toCount) count++
        return count
    }

    fun findFirst(toFind: T, start: Int = 0, end: Int = size): Int {
        for (i in start until end) if (getAtIndex(i) == toFind) return i
        return -1
    }

    fun findLast(toFind: T, start: Int = 0, end: Int = size): Int {
        for (i in start until end) if (getAtIndex(i) == toFind) return i
        return -1
    }

    fun findNth(toFind: T, nth: Int, start: Int = 0, end: Int = size): Int {
        var n = nth
        if (n == 0) return findFirst(toFind, start, end)
        if (n == -1) return findLast(toFind, start, end)
        val all = findAll(toFind, start, end)
        if (n < 0) n += end - start
        return if (n < 0 || n >= all.size) -1 else all.getAtIndex(n)!!
    }

    fun findAll(toFind: T, start: Int = 0, end: Int = size): Sequential<Int?> =
        Mutable.CanChangeSize.of(ArrayList<Int?>(count(toFind)))
            .also { for (i in start until end) if (getAtIndex(i) == toFind) it.append(i) }

    override val overValues: Iterable<T>
        get() = Iterable { ObverseIterator(this) }
    override val overValuesInReverse: Iterable<T>
        get() = Iterable { ReverseIterator(this) }

    /**
     * @param index Unsigned integer
     * @return Data stored at index
     * @throws IndexOutOfBoundsException If integer is negative or equal or more than size
     */
    override fun getAtIndex(index: Int): T

    interface Mutable<T> : Sequential<T>, CanSwapAtIndices<T> {

        override fun clone(deepIfPossible: Boolean): Mutable<T>
        fun clone(deepIfPossible: Boolean, changesToValues: Change): Mutable<T>

        fun replaceData(toReplace: (T) -> T) {
            if (isNotEmpty()) {
                changesToValues.beforeChange()
                for (i in 0 until size) setAtIndex(i, toReplace(getAtIndex(i)))
                changesToValues.afterChange()
            }
        }

        fun mutateData(toApply: (T) -> Unit) {
            if (isNotEmpty()) {
                changesToValues.beforeChange()
                for (value in overValues) toApply(value)
                changesToValues.afterChange()
            }
        }

        override val overValues: Iterable<T>
            get() = Iterable { ObverseIterator.Mutable(this) }
        override val overValuesInReverse: Iterable<T>
            get() = Iterable { ReverseIterator.Mutable(this) }

        interface CanChangeSize<T> : Mutable<T>, CanPrependTo<T>, CanAppendTo<T>, CanRemoveAt, CanClear {

            override fun clone(deepIfPossible: Boolean): CanChangeSize<T>
            override fun clone(deepIfPossible: Boolean, changesToValues: Change): CanChangeSize<T>

            fun filterInPlace(toTest: (T) -> Boolean) {
                if (isNotEmpty()) {
                    var mutated = false
                    for (i in 0 until size) {
                        val value: T = getAtIndex(i)
                        if (!toTest(value)) {
                            if (!mutated) {
                                changesToSize.beforeChange()
                                mutated = true
                            }
                            removeIndexFrom(i)
                        }
                    }
                    if (mutated) {
                        changesToSize.afterChange()
                    }
                }
            }

            companion object {

                fun <T> ofArguments(vararg arguments: T, change: Change = Change()): CanChangeSize<T> =
                    of(arguments, change)

                fun <T> of(list: List<T>, change: Change = Change()): CanChangeSize<T> =
                    ListSequential(list, change)

                fun <T> of(array: Array<out T>, change: Change = Change()): CanChangeSize<T> =
                    of(ArrayList<T>(array.size).also { it.addAll(listOf(*array)) }, change)
            }
        }

        companion object {

            fun <T> ofArguments(vararg arguments: T): Mutable<T> =
                of(arguments.asList().toMutableList())

            fun of(string: String): Mutable<Char> =
                of(string.toCharArray())

            @Suppress("DuplicatedCode")
            fun <T> of(list: MutableList<T>, change: Change = Change()): Mutable<T> =
                object : AbstractMutableSequential<T>(change) {
                    override fun getAtIndex(index: Int): T {
                        validateIndex(index)
                        return list[index]
                    }

                    override val size by list::size

                    override fun setAtIndex(index: Int, toSet: T) {
                        validateIndex(index)
                        change.beforeChange()
                        list[index] = toSet
                        change.afterChange()
                    }
                }

            @Suppress("DuplicatedCode")
            fun <T> of(array: Array<T>, changesToSize: Change = Change()): Mutable<T> =
                object : AbstractMutableSequential<T>(changesToSize) {
                    override fun getAtIndex(index: Int): T {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size by array::size

                    override fun setAtIndex(index: Int, toSet: T) {
                        validateIndex(index)
                        changesToValues.beforeChange()
                        array[index] = toSet
                        changesToValues.afterChange()
                    }
                }

            fun of(array: IntArray, change: Change = Change()): Mutable<Int> =
                object : AbstractMutableSequential<Int>(change) {
                    override fun getAtIndex(index: Int): Int {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int by array::size

                    override fun setAtIndex(index: Int, toSet: Int) {
                        validateIndex(index)
                        change.beforeChange()
                        array[index] = toSet
                        change.afterChange()
                    }
                }

            fun of(array: FloatArray, change: Change = Change()): Mutable<Float> =
                object : AbstractMutableSequential<Float>(change) {
                    override fun getAtIndex(index: Int): Float {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int by array::size

                    override fun setAtIndex(index: Int, toSet: Float) {
                        validateIndex(index)
                        change.beforeChange()
                        array[index] = toSet
                        change.afterChange()
                    }
                }

            fun of(array: LongArray, change: Change = Change()): Mutable<Long> =
                object : AbstractMutableSequential<Long>(change) {
                    override fun getAtIndex(index: Int): Long {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int by array::size

                    override fun setAtIndex(index: Int, toSet: Long) {
                        validateIndex(index)
                        change.beforeChange()
                        array[index] = toSet
                        change.afterChange()
                    }
                }

            fun of(array: DoubleArray, change: Change = Change()): Mutable<Double> =
                object : AbstractMutableSequential<Double>(change) {
                    override fun getAtIndex(index: Int): Double {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int by array::size

                    override fun setAtIndex(index: Int, toSet: Double) {
                        validateIndex(index)
                        change.beforeChange()
                        array[index] = toSet
                        change.afterChange()
                    }
                }

            fun of(array: ByteArray, change: Change = Change()): Mutable<Byte> =
                object : AbstractMutableSequential<Byte>(change) {
                    override fun getAtIndex(index: Int): Byte {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int by array::size

                    override fun setAtIndex(index: Int, toSet: Byte) {
                        validateIndex(index)
                        change.beforeChange()
                        array[index] = toSet
                        change.afterChange()
                    }
                }

            fun of(array: CharArray, change: Change = Change()): Mutable<Char> =
                object : AbstractMutableSequential<Char>(change) {
                    override fun getAtIndex(index: Int): Char {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int by array::size

                    override fun setAtIndex(index: Int, toSet: Char) {
                        validateIndex(index)
                        change.beforeChange()
                        array[index] = toSet
                        change.afterChange()
                    }
                }

            fun of(array: BooleanArray, change: Change = Change()): Mutable<Boolean> =
                object : AbstractMutableSequential<Boolean>(change) {
                    override fun getAtIndex(index: Int): Boolean {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int by array::size

                    override fun setAtIndex(index: Int, toSet: Boolean) {
                        validateIndex(index)
                        change.beforeChange()
                        array[index] = toSet
                        change.afterChange()
                    }
                }
        }
    }

//    interface View<T> : Sequential<T> { // View<Sequential<T>>,
////        fun addExpirationHandler(handler: (Sequential<T>) -> Unit) {
////            val core: Sequential<T> = core
////            if (core is Mutable<*>) {
////                val coreOnPreMutate = core.change.onPreMutate
////                if (coreOnPreMutate is Listeners) {
////                    coreOnPreMutate.addDisposable {
////                        handler(clone(false))
////                    }
////                }
////            }
////        }
//
//        //        @Throws(View.CoreExpiredException::class)
//        override fun getAtIndex(index: Int): T {
//            return core.getAtIndex(transformIndex(index))
//        }
//
//        fun transformIndex(index: Int): Int
//        interface Referential<T> : View<T> {
//            fun getIndices(): IntArray
//            override val size: Int get() = getIndices().size
//
//            override fun transformIndex(index: Int): Int {
//                return getIndices()[index]
//            }
//
//            fun interface ReferenceComputer<T> {
//                fun computeReference(sequential: Sequential<T>): IntArray
//            }
//        }
//
//        sealed class Reference<T> constructor(
//            sequential: Sequential<T>,
//            computer: Referential.ReferenceComputer<T>,
//            onExpire: (() -> Unit)?,
//        ) :
//            AbstractView<T>(sequential, onExpire), Referential<T> {
//            private var indices: IntArray
//            override fun getIndices(): IntArray {
//                return indices
//            }
//
//            companion object {
//                val EMPTY_INDICES = IntArray(0)
//            }
//
//            init {
//                indices = computer.computeReference(sequential)
//                this.onExpire.add { indices = EMPTY_INDICES }
//            }
//        }
//
//        class AllButOne<T>(sequential: Sequential<T>, onExpire: (() -> Unit)?, private val except: Int) :
//            AbstractView<T>(sequential, onExpire) {
//            override val size: Int get() = if (expired) 0 else ((core.size - 1)).coerceAtLeast(0)
//
//            override fun transformIndex(index: Int): Int {
//                return if (index >= except) index + 1 else index
//            }
//        }
//
//        class Conditional<T> internal constructor(
//            sequential: Sequential<T>,
//            condition: (T) -> Boolean,
//            onExpire: (() -> Unit)?
//        ) :
//            Reference<T>(sequential, Referential.ReferenceComputer { seq: Sequential<T> ->
//                var index = 0
//                val total = seq.size
//                var foundIndex = 0
//                val foundTotal: Int = seq.count(condition)
//                val reference = IntArray(foundTotal)
//                while (foundIndex < foundTotal && index < total) {
//                    if (condition(seq.getAtIndex(index))) {
//                        reference[foundIndex++] = index
//                    }
//                    index++
//                }
//                reference
//            }, onExpire)
//
//
//        /**
//         * A sequential ranged view on another sequential object
//         *
//         * @param start Inclusive starting index
//         * @param end   Non-inclusive ending index
//         */
//        class Ranged<T>(
//            sequential: Sequential<T>,
//            onExpire: (() -> Unit)?,
//            private val start: Int,
//            private val end: Int = sequential.size
//        ) :
//            AbstractView<T>(sequential, onExpire) {
//
//            override val size: Int get() = end - start
//
//            override fun transformIndex(index: Int): Int {
//                return index + start
//            }
//        }
//
//        class Reversed<T>(sequential: Sequential<T>, onExpire: (() -> Unit)?) : AbstractView<T>(sequential, onExpire) {
//            override val size: Int get() = if (expired) 0 else core.size
//
//            override fun transformIndex(index: Int): Int {
//                return size - index - 1
//            }
//        }
//
//        companion object {
//            fun <T> allButOne(sequential: Sequential<T>, except: Int, onExpire: (() -> Unit)? = null): View<T> =
//                AllButOne(sequential, onExpire, except)
//
//            fun <T> ranged(sequential: Sequential<T>, start: Int, onExpire: (() -> Unit)? = null): View<T> =
//                Ranged(sequential, onExpire, start)
//
//            fun <T> ranged(sequential: Sequential<T>, start: Int, end: Int, onExpire: (() -> Unit)? = null): View<T> =
//                Ranged(sequential, onExpire, start, end)
//
//            fun <T> reversed(sequential: Sequential<T>, onExpire: (() -> Unit)? = null): View<T> =
//                Reversed(sequential, onExpire)
//        }
//    }
//
//    /**
//     * A read-only partial view on another sequential.
//     *
//     * @param <T> Type of data
//     * @see View.Ranged
//     * @see View.Reversed
//     * @see View.Referential
//     * @see View.Conditional
//     */
//    abstract class AbstractView<T> protected constructor(sequential: Sequential<T>, onExpire: (() -> Unit)?) :
//        AbstractSequential<T>(),
//        View<T> { // View.Injected<Sequential<T>> {
////        override val injected = View.Impl(sequential, onExpire)
//    }

    abstract class AbstractMutableSequential<T> protected constructor(override val changesToValues: Change) :
        AbstractSequential<T>(), Mutable<T> {

        override fun clone(deepIfPossible: Boolean) = clone(deepIfPossible, Change())
        override fun clone(deepIfPossible: Boolean, changesToValues: Change): Mutable<T> {
            // TODO deep clone
            return ListSequential(asList(), changesToValues)
        }
    }

    abstract class AbstractSequential<T> : Sequential<T> {

        override fun specificThis(): Sequential<T> = this
        override fun clone(deepIfPossible: Boolean): Sequential<T> {
            // TODO deep clone
            return ListSequential(asList())
        }

        override fun toString() = overValues.joinToString(", ", "[", "]")
        override fun hashCode() = when (size) {
            0 -> 0
            1 -> getAtFirstIndex().hashCode()
            else -> size.hashCode() xor
                    (getAtFirstIndex().hashCode()) xor
                    (getAtLastIndex().hashCode())
        }

        override fun equals(other: Any?): Boolean {
            if (this === other)
                return true
            if (other is Sequential<*>) {
                if (size == other.size) {
                    for (i in 0 until size) if (getAtIndex(i) != other.getAtIndex(i)) return false
                    return true
                }
            }
            return false
        }
    }

    private open class ObverseIterator<S : Sequential<T>, T>(val sequential: S) : Iterator<T> {
        protected var i = 0
        override fun hasNext() = i < sequential.size
        override fun next(): T = sequential.getAtIndex(i++)
        internal open class Mutable<S : Sequential.Mutable<T>, T>(sequential: S) : ObverseIterator<S, T>(sequential) {
            class VariableSize<S : Sequential.Mutable.CanChangeSize<T>, T>(sequential: S) :
                Mutable<S, T>(sequential), MutableIterator<T> {
                override fun remove() {
                    sequential.removeIndexFrom(i)
                }
            }
        }
    }

    private open class ReverseIterator<S : Sequential<T>, T>(protected val sequential: S) : Iterator<T> {
        protected var index: Int = sequential.size
        override fun hasNext() = index > 0
        override fun next(): T = sequential.getAtIndex(--index)
        internal open class Mutable<S : Sequential.Mutable<T>, T>(sequential: S) : ReverseIterator<S, T>(sequential) {
            class VariableSize<S : Sequential.Mutable.CanChangeSize<T>, T>(sequential: S) :
                Mutable<S, T>(sequential), MutableIterator<T> {
                override fun remove() {
                    sequential.removeIndexFrom(index)
                }
            }
        }
    }

    companion object {
        fun <T> ofArguments(vararg arguments: T): Sequential<T> =
            of(arguments)

        fun <T> of(list: List<T>): Sequential<T> = object : AbstractSequential<T>() {
            override fun getAtIndex(index: Int): T = list[index]
            override val size by list::size
        }

        fun <T> of(map: Map<Int, T>, maxCount: Int? = null): Sequential<T?> = ListSequential<T?>().apply {
            for (key in 0..(maxCount ?: max(map.keys))) append(map[key])
        }

        fun of(array: IntArray): Sequential<Int> = object : AbstractSequential<Int>() {
            override fun getAtIndex(index: Int): Int = array[index]
            override val size: Int by array::size
        }

        fun of(array: FloatArray): Sequential<Float> = object : AbstractSequential<Float>() {
            override fun getAtIndex(index: Int): Float = array[index]
            override val size: Int by array::size
        }

        fun of(array: LongArray): Sequential<Long> = object : AbstractSequential<Long>() {
            override fun getAtIndex(index: Int): Long = array[index]
            override val size: Int by array::size
        }

        fun of(array: DoubleArray): Sequential<Double> = object : AbstractSequential<Double>() {
            override fun getAtIndex(index: Int): Double = array[index]
            override val size: Int by array::size
        }

        fun of(array: ByteArray): Sequential<Byte> = object : AbstractSequential<Byte>() {
            override fun getAtIndex(index: Int): Byte = array[index]
            override val size: Int by array::size
        }

        fun of(array: CharArray): Sequential<Char> = object : AbstractSequential<Char>() {
            override fun getAtIndex(index: Int): Char = array[index]
            override val size: Int by array::size
        }

        fun of(array: BooleanArray): Sequential<Boolean> = object : AbstractSequential<Boolean>() {
            override fun getAtIndex(index: Int): Boolean = array[index]
            override val size: Int by array::size
        }

        fun <T> of(array: Array<out T>): Sequential<T> = object : AbstractSequential<T>() {
            override fun getAtIndex(index: Int): T = array[index]
            override val size: Int by array::size
        }

        fun <T> empty(): Sequential<T> = object : AbstractSequential<T>() {
            override fun getAtIndex(index: Int): T = throw IndexOutOfBoundsException()
            override val size = 0
        }

        fun of(string: String): Sequential<Char> = object : AbstractSequential<Char>() {
            override fun getAtIndex(index: Int): Char = string[index]
            override val size by string::length
        }
    }
}