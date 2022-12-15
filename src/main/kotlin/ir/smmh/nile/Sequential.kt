@file:Suppress("unused")

package ir.smmh.nile


import ir.smmh.nile.verbs.*
import java.util.*
import java.util.Collections.max


interface Sequential<T> : Iterable<T>, ReverseIterable<T>, CanClone<Sequential<T>>, CanGetAtIndex<T>,
    CanContainValue<T> {

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

    fun filterOutOfPlace(toTest: (T) -> Boolean): Sequential<T> = Mutable.VariableSize.of<T>(ArrayList(count(toTest)))
        .also { for (element in this) if (toTest(element)) it.append(element) }

    fun <R> applyOutOfPlace(toApply: (T) -> R): Sequential<R> = Mutable.VariableSize.of<R>(ArrayList(size))
        .also { for (element in this) it.append(toApply(element)) }

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
        Mutable.VariableSize.of(ArrayList<Int?>(count(toFind)))
            .also { for (i in start until end) if (getAtIndex(i) == toFind) it.append(i) }

    override fun iterator(): Iterator<T> = ObverseIterator(this)
    override fun inReverse(): Iterable<T> = Iterable { ReverseIterator(this) }

    /**
     * @param index Unsigned integer
     * @return Data stored at index
     * @throws IndexOutOfBoundsException If integer is negative or equal or more than size
     */
    override fun getAtIndex(index: Int): T

    interface Mutable<T> : Sequential<T>, CanSwapAtIndices<T>, CanClone.Mutable<Sequential<T>>, Mut.Able {

        override fun clone(deepIfPossible: Boolean): Mutable<T>
        override fun clone(deepIfPossible: Boolean, mut: Mut): Mutable<T>

        /**
         * Do not call this directly because it does not call preMutate/mutate
         */
        fun fillWithPermutations(permutations: CanAppendTo<in Sequential<T>>, first: Int, last: Int) {
            if (first == last) {
                permutations.append(clone(false))
            } else {
                for (i in first..last) {
                    swap(first, i)
                    fillWithPermutations(permutations, first + 1, last)
                    swap(first, i)
                }
            }
        }

        fun getPermutations(): Sequential<Sequential<T>> {
            val permutations: VariableSize<Sequential<T>> = SequentialImpl()
            fillWithPermutations(permutations, 0, size - 1)
            return permutations
        }

        fun replaceData(toReplace: (T) -> T) {
            if (isNotEmpty()) {
                mut.preMutate()
                for (i in 0 until size) setAtIndex(i, toReplace(getAtIndex(i)))
                mut.mutate()
            }
        }

        fun mutateData(toApply: (T) -> Unit) {
            if (isNotEmpty()) {
                mut.preMutate()
                for (element in this) toApply(element)
                mut.mutate()
            }
        }

        override fun iterator(): Iterator<T> = ObverseIterator.Mutable(this)
        override fun inReverse(): Iterable<T> = Iterable { ReverseIterator.Mutable(this) }

        interface VariableSize<T> : Mutable<T>, CanPrependTo<T>, CanAppendTo<T>, CanRemoveAt, CanClear {

            override fun clone(deepIfPossible: Boolean): VariableSize<T>
            override fun clone(deepIfPossible: Boolean, mut: Mut): VariableSize<T>

            fun filterInPlace(toTest: (T) -> Boolean) {
                if (isNotEmpty()) {
                    var mutated = false
                    for (i in 0 until size) {
                        val element: T = getAtIndex(i)
                        if (!toTest(element)) {
                            if (!mutated) {
                                mut.preMutate()
                                mutated = true
                            }
                            removeIndexFrom(i)
                        }
                    }
                    if (mutated) {
                        mut.mutate()
                    }
                }
            }

            companion object {

                fun <T> ofArguments(vararg arguments: T, mut: Mut = Mut()): VariableSize<T> =
                    of(arguments, mut)

                fun <T> of(list: List<T>, mut: Mut = Mut()): VariableSize<T> =
                    SequentialImpl(list, mut)

                fun <T> of(array: Array<out T>, mut: Mut = Mut()): VariableSize<T> =
                    of(ArrayList<T>(array.size).also { it.addAll(listOf(*array)) }, mut)
            }
        }

        companion object {

            fun <T> ofArguments(vararg arguments: T): Mutable<T> =
                of(arguments.asList().toMutableList())

            fun of(string: String): Mutable<Char> =
                of(string.toCharArray())

            @Suppress("DuplicatedCode")
            fun <T> of(list: MutableList<T>, mut: Mut = Mut()): Mutable<T> =
                object : AbstractMutableSequential<T>(mut) {
                    override fun getAtIndex(index: Int): T {
                        validateIndex(index)
                        return list[index]
                    }

                    override val size by list::size

                    override fun setAtIndex(index: Int, toSet: T) {
                        validateIndex(index)
                        mut.preMutate()
                        list[index] = toSet
                        mut.mutate()
                    }
                }

            @Suppress("DuplicatedCode")
            fun <T> of(array: Array<T>, mut: Mut = Mut()): Mutable<T> =
                object : AbstractMutableSequential<T>(mut) {
                    override fun getAtIndex(index: Int): T {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size by array::size

                    override fun setAtIndex(index: Int, toSet: T) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = toSet
                        mut.mutate()
                    }
                }

            fun of(array: IntArray, mut: Mut = Mut()): Mutable<Int> =
                object : AbstractMutableSequential<Int>(mut) {
                    override fun getAtIndex(index: Int): Int {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int by array::size

                    override fun setAtIndex(index: Int, toSet: Int) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = toSet
                        mut.mutate()
                    }
                }

            fun of(array: FloatArray, mut: Mut = Mut()): Mutable<Float> =
                object : AbstractMutableSequential<Float>(mut) {
                    override fun getAtIndex(index: Int): Float {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int by array::size

                    override fun setAtIndex(index: Int, toSet: Float) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = toSet
                        mut.mutate()
                    }
                }

            fun of(array: LongArray, mut: Mut = Mut()): Mutable<Long> =
                object : AbstractMutableSequential<Long>(mut) {
                    override fun getAtIndex(index: Int): Long {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int by array::size

                    override fun setAtIndex(index: Int, toSet: Long) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = toSet
                        mut.mutate()
                    }
                }

            fun of(array: DoubleArray, mut: Mut = Mut()): Mutable<Double> =
                object : AbstractMutableSequential<Double>(mut) {
                    override fun getAtIndex(index: Int): Double {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int by array::size

                    override fun setAtIndex(index: Int, toSet: Double) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = toSet
                        mut.mutate()
                    }
                }

            fun of(array: ByteArray, mut: Mut = Mut()): Mutable<Byte> =
                object : AbstractMutableSequential<Byte>(mut) {
                    override fun getAtIndex(index: Int): Byte {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int by array::size

                    override fun setAtIndex(index: Int, toSet: Byte) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = toSet
                        mut.mutate()
                    }
                }

            fun of(array: CharArray, mut: Mut = Mut()): Mutable<Char> =
                object : AbstractMutableSequential<Char>(mut) {
                    override fun getAtIndex(index: Int): Char {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int by array::size

                    override fun setAtIndex(index: Int, toSet: Char) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = toSet
                        mut.mutate()
                    }
                }

            fun of(array: BooleanArray, mut: Mut = Mut()): Mutable<Boolean> =
                object : AbstractMutableSequential<Boolean>(mut) {
                    override fun getAtIndex(index: Int): Boolean {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int by array::size

                    override fun setAtIndex(index: Int, toSet: Boolean) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = toSet
                        mut.mutate()
                    }
                }
        }
    }

//    interface View<T> : Sequential<T> { // ir.smmh.nile.View<Sequential<T>>,
////        fun addExpirationHandler(handler: (Sequential<T>) -> Unit) {
////            val core: Sequential<T> = core
////            if (core is Mutable<*>) {
////                val coreOnPreMutate = core.mut.onPreMutate
////                if (coreOnPreMutate is Listeners) {
////                    coreOnPreMutate.addDisposable {
////                        handler(clone(false))
////                    }
////                }
////            }
////        }
//
//        //        @Throws(ir.smmh.nile.View.CoreExpiredException::class)
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
//        View<T> { // , ir.smmh.nile.View.Injected<Sequential<T>> {
////        override val injected = ir.smmh.nile.View.Impl(sequential, onExpire)
//    }

    abstract class AbstractMutableSequential<T> protected constructor(override var mut: Mut) :
        AbstractSequential<T>(), Mutable<T>, Mut.Able {

        override fun clone(deepIfPossible: Boolean) = clone(deepIfPossible, Mut())
        override fun clone(deepIfPossible: Boolean, mut: Mut): Mutable<T> {
            // TODO deep clone
            return SequentialImpl(asList(), mut)
        }
    }

    abstract class AbstractSequential<T> : Sequential<T> {

        override fun specificThis(): Sequential<T> = this
        override fun clone(deepIfPossible: Boolean): Sequential<T> {
            // TODO deep clone
            return SequentialImpl(asList())
        }

        override fun toString() = this.joinToString(", ", "[", "]")
        override fun hashCode() = size.hashCode() xor
                (getNullableAtFirstIndex().hashCode()) xor
                (getNullableAtLastIndex().hashCode())

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
            class VariableSize<S : Sequential.Mutable.VariableSize<T>, T>(sequential: S) :
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
            class VariableSize<S : Sequential.Mutable.VariableSize<T>, T>(sequential: S) :
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

        fun <T> of(map: Map<Int, T>, maxCount: Int? = null): Sequential<T?> = SequentialImpl<T?>().apply {
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