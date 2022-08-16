@file:Suppress("unused")

package ir.smmh.nile


import ir.smmh.nile.verbs.*
import java.util.*
import java.util.Collections.max


interface Sequential<T> : Iterable<T>, ReverseIterable<T>, CanClone<Sequential<T>>, CanGetAtIndex<T>,
    CanContainValue<T> {

    fun toBooleanArray(toBoolean: (T) -> Boolean): BooleanArray {
        val array = BooleanArray(size)
        for (i in array.indices) {
            array[i] = toBoolean(getAtIndex(i))
        }
        return array
    }

    fun toLongArray(toLong: (T) -> Long): LongArray {
        val array = LongArray(size)
        for (i in array.indices) {
            array[i] = toLong(getAtIndex(i))
        }
        return array
    }

    @Suppress("UNCHECKED_CAST")
    fun toArray(): Array<T> {
        val array = arrayOfNulls<Any>(size) as Array<T>
        for (i in array.indices) {
            array[i] = getAtIndex(i)
        }
        return array
    }

    fun toFloatArray(toFloat: (T) -> Float): FloatArray {
        val array = FloatArray(size)
        for (i in array.indices) {
            array[i] = toFloat(getAtIndex(i))
        }
        return array
    }

    fun toDoubleArray(toDouble: (T) -> Double): DoubleArray {
        val array = DoubleArray(size)
        for (i in array.indices) {
            array[i] = toDouble(getAtIndex(i))
        }
        return array
    }

    fun toCharArray(toChar: (T) -> Char): CharArray {
        val array = CharArray(size)
        for (i in array.indices) {
            array[i] = toChar(getAtIndex(i))
        }
        return array
    }

    fun toIntArray(toInt: (T) -> Int): IntArray {
        val array = IntArray(size)
        for (i in array.indices) {
            array[i] = toInt(getAtIndex(i))
        }
        return array
    }

    fun filterOutOfPlace(toTest: (T) -> Boolean): Sequential<T> {
        val filtered: Mutable.VariableSize<T> = Mutable.VariableSize.of(ArrayList(count(toTest)))
        for (element in this) {
            if (toTest(element)) {
                filtered.append(element)
            }
        }
        return filtered
    }

    fun <R> applyOutOfPlace(toApply: (T) -> R): Sequential<R> {
        val applied: Mutable.VariableSize<R> = Mutable.VariableSize.of(
            ArrayList(
                size
            )
        )
        for (element in this) {
            applied.append(toApply(element))
        }
        return applied
    }

    fun asList(): List<T> {
        return object : AbstractList<T>() {
            override val size: Int get() = this@Sequential.size

            override fun get(index: Int): T? {
                return getAtIndex(index)
            }
        }
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }

    override fun containsValue(toCheck: T): Boolean {
        return findFirst(toCheck) != -1
    }

    override fun doesNotContainValue(toCheck: T): Boolean {
        return findFirst(toCheck) == -1
    }

    fun count(toTest: (T) -> Boolean, start: Int = 0, end: Int = size): Int {
        var count = 0
        for (i in start until end) {
            if (toTest(getAtIndex(i))) {
                count++
            }
        }
        return count
    }

    fun count(toCount: T, start: Int = 0, end: Int = size): Int {
        var count = 0
        for (i in start until end) {
            if (getAtIndex(i) == toCount) {
                count++
            }
        }
        return count
    }

    fun findFirst(toFind: T, start: Int = 0, end: Int = size): Int {
        for (i in start until end) {
            if (getAtIndex(i) == toFind) {
                return i
            }
        }
        return -1
    }

    fun findLast(toFind: T, start: Int = 0, end: Int = size): Int {
        for (i in start until end) {
            if (getAtIndex(i) == toFind) {
                return i
            }
        }
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

    fun findAll(toFind: T, start: Int = 0, end: Int = size): Sequential<Int?> {
        val all = Mutable.VariableSize.of(ArrayList<Int?>(count(toFind)))
        for (i in start until end) {
            if (getAtIndex(i) == toFind) {
                all.append(i)
            }
        }
        return all
    }

    override fun iterator(): Iterator<T> {
        return ObverseIterator(this)
    }

    override fun inReverse(): Iterable<T> {
        return Iterable { ReverseIterator(this) }
    }

    /**
     * @param index Unsigned integer
     * @return Data stored at index
     * @throws IndexOutOfBoundsException If integer is negative or equal or more than size
     */
    override fun getAtIndex(index: Int): T
    interface Mutable<T> : Sequential<T>, CanSwapAtIndices<T>, CanClone.Mutable<Sequential<T>>,
        Mut.Able {
        override fun clone(deepIfPossible: Boolean): Mutable<T>
        override fun clone(deepIfPossible: Boolean, mut: Mut): Mutable<T>

        /**
         * Do not call this directly because it does not call preMutate/mutate
         */
        fun fillWithPermutations(permutations: CanAppendTo<in Sequential<T>?>, first: Int, last: Int) {
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

        fun getPermutations(): Sequential<Sequential<T>?>? {
            // TODO null check
            val permutations: VariableSize<Sequential<T>?> = SequentialImpl()
            fillWithPermutations(permutations, 0, size - 1)
            return permutations
        }

        fun replaceData(toReplace: (T) -> T) {
            if (isNotEmpty()) {
                mut.preMutate()
                for (i in 0 until size) {
                    setAtIndex(i, toReplace(getAtIndex(i)))
                }
                mut.mutate()
            }
        }

        fun mutateData(toApply: (T) -> Unit) {
            if (isNotEmpty()) {
                mut.preMutate()
                for (element in this) {
                    toApply(element)
                }
                mut.mutate()
            }
        }

        override fun iterator(): Iterator<T> {
            return ObverseIterator.Mutable(this)
        }

        override fun inReverse(): Iterable<T> {
            return Iterable { ReverseIterator.Mutable(this) }
        }

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
                fun <T> ofArguments(
                    vararg arguments: T,
                    mut: Mut = Mut()
                ): VariableSize<T> {
                    return of(arguments, mut)
                }

                fun <T> of(
                    list: List<T>,
                    mut: Mut = Mut()
                ): VariableSize<T> {
                    return SequentialImpl(list, mut)
                }

                fun <T> of(
                    array: Array<out T>,
                    mut: Mut = Mut()
                ): VariableSize<T> {
                    val list: MutableList<T> = ArrayList(array.size)
                    list.addAll(listOf(*array))
                    return of(list, mut)
                }
            }
        }

        companion object {
            fun <T> ofArguments(vararg arguments: T): Mutable<T> {
                return of(arguments.asList().toMutableList())
            }

            fun of(string: String): Mutable<Char> {
                return of(string.toCharArray())
            }

            @Suppress("DuplicatedCode")
            fun <T> of(list: MutableList<T>, mut: Mut = Mut()): Mutable<T> {
                return object : AbstractMutableSequential<T>(mut) {
                    override fun getAtIndex(index: Int): T {
                        validateIndex(index)
                        return list[index]
                    }

                    override val size get() = list.size

                    override fun setAtIndex(index: Int, toSet: T) {
                        validateIndex(index)
                        mut.preMutate()
                        list[index] = toSet
                        mut.mutate()
                    }
                }
            }

            fun of(array: IntArray, mut: Mut = Mut()): Mutable<Int> {
                return object : AbstractMutableSequential<Int>(mut) {
                    override fun getAtIndex(index: Int): Int {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int get() = array.size

                    override fun setAtIndex(index: Int, toSet: Int) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = Objects.requireNonNull(toSet)
                        mut.mutate()
                    }
                }
            }

            fun of(array: FloatArray, mut: Mut = Mut()): Mutable<Float> {
                return object : AbstractMutableSequential<Float>(mut) {
                    override fun getAtIndex(index: Int): Float {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int get() = array.size

                    override fun setAtIndex(index: Int, toSet: Float) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = Objects.requireNonNull(toSet)
                        mut.mutate()
                    }
                }
            }

            fun of(array: LongArray, mut: Mut = Mut()): Mutable<Long> {
                return object : AbstractMutableSequential<Long>(mut) {
                    override fun getAtIndex(index: Int): Long {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int get() = array.size

                    override fun setAtIndex(index: Int, toSet: Long) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = Objects.requireNonNull(toSet)
                        mut.mutate()
                    }
                }
            }

            fun of(array: DoubleArray, mut: Mut = Mut()): Mutable<Double> {
                return object : AbstractMutableSequential<Double>(mut) {
                    override fun getAtIndex(index: Int): Double {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int get() = array.size

                    override fun setAtIndex(index: Int, toSet: Double) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = Objects.requireNonNull(toSet)
                        mut.mutate()
                    }
                }
            }

            fun of(array: ByteArray, mut: Mut = Mut()): Mutable<Byte> {
                return object : AbstractMutableSequential<Byte>(mut) {
                    override fun getAtIndex(index: Int): Byte {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int get() = array.size

                    override fun setAtIndex(index: Int, toSet: Byte) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = Objects.requireNonNull(toSet)
                        mut.mutate()
                    }
                }
            }

            fun of(array: CharArray, mut: Mut = Mut()): Mutable<Char> {
                return object : AbstractMutableSequential<Char>(mut) {
                    override fun getAtIndex(index: Int): Char {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int get() = array.size

                    override fun setAtIndex(index: Int, toSet: Char) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = Objects.requireNonNull(toSet)
                        mut.mutate()
                    }
                }
            }

            fun of(
                array: BooleanArray,
                mut: Mut = Mut()
            ): Mutable<Boolean> {
                return object : AbstractMutableSequential<Boolean>(mut) {
                    override fun getAtIndex(index: Int): Boolean {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size: Int get() = array.size

                    override fun setAtIndex(index: Int, toSet: Boolean) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = Objects.requireNonNull(toSet)
                        mut.mutate()
                    }
                }
            }

            @Suppress("DuplicatedCode")
            fun <T> of(array: Array<T>, mut: Mut = Mut()): Mutable<T> {
                return object : AbstractMutableSequential<T>(mut) {
                    override fun getAtIndex(index: Int): T {
                        validateIndex(index)
                        return array[index]
                    }

                    override val size get() = array.size

                    override fun setAtIndex(index: Int, toSet: T) {
                        validateIndex(index)
                        mut.preMutate()
                        array[index] = toSet
                        mut.mutate()
                    }
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
        AbstractSequential<T>(), Mutable<T>,
        Mut.Able {

        override fun clone(deepIfPossible: Boolean) = clone(deepIfPossible, Mut())
        override fun clone(deepIfPossible: Boolean, mut: Mut): Mutable<T> {
            // TODO deep clone
            return SequentialImpl(asList(), mut)
        }
    }

    abstract class AbstractSequential<T> : Sequential<T> {
        override fun specificThis(): Sequential<T> {
            return this
        }

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
                    for (i in 0 until size) {
                        if (getAtIndex(i) != other.getAtIndex(i)) {
                            return false
                        }
                    }
                    return true
                }
            }
            return false
        }
    }

    private open class ObverseIterator<S : Sequential<T>, T>(val sequential: S) : Iterator<T> {
        protected var i = 0
        override fun hasNext(): Boolean {
            return i < sequential.size
        }

        override fun next(): T {
            return sequential.getAtIndex(i++)
        }

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
        override fun hasNext(): Boolean {
            return index > 0
        }

        override fun next(): T {
            return sequential.getAtIndex(--index)
        }

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
        fun <T> ofArguments(vararg arguments: T): Sequential<T> {
            return of(arguments)
        }

        fun <T> of(list: List<T>): Sequential<T> {
            return object : AbstractSequential<T>() {
                override fun getAtIndex(index: Int): T {
                    return list[index]
                }

                override val size: Int get() = list.size
            }
        }

        fun <T> of(map: Map<Int, T>, maxCount: Int? = null): Sequential<T?> {
            val n: Int = maxCount ?: max(map.keys)
            SequentialImpl<T?>().apply {
                for (key in 0..n) append(map[key])
                return this
            }
        }

        fun of(array: IntArray): Sequential<Int> {
            return object : AbstractSequential<Int>() {
                override fun getAtIndex(index: Int): Int {
                    return array[index]
                }

                override val size: Int get() = array.size
            }
        }

        fun of(array: FloatArray): Sequential<Float> {
            return object : AbstractSequential<Float>() {
                override fun getAtIndex(index: Int): Float {
                    return array[index]
                }

                override val size: Int get() = array.size
            }
        }

        fun of(array: LongArray): Sequential<Long> {
            return object : AbstractSequential<Long>() {
                override fun getAtIndex(index: Int): Long {
                    return array[index]
                }

                override val size: Int get() = array.size
            }
        }

        fun of(array: DoubleArray): Sequential<Double> {
            return object : AbstractSequential<Double>() {
                override fun getAtIndex(index: Int): Double {
                    return array[index]
                }

                override val size: Int get() = array.size
            }
        }

        fun of(array: ByteArray): Sequential<Byte> {
            return object : AbstractSequential<Byte>() {
                override fun getAtIndex(index: Int): Byte {
                    return array[index]
                }

                override val size: Int get() = array.size
            }
        }

        fun of(array: CharArray): Sequential<Char> {
            return object : AbstractSequential<Char>() {
                override fun getAtIndex(index: Int): Char {
                    return array[index]
                }

                override val size: Int get() = array.size
            }
        }

        fun of(array: BooleanArray): Sequential<Boolean> {
            return object : AbstractSequential<Boolean>() {
                override fun getAtIndex(index: Int): Boolean {
                    return array[index]
                }

                override val size: Int get() = array.size
            }
        }

        fun <T> of(array: Array<out T>): Sequential<T> {
            return object : AbstractSequential<T>() {
                override fun getAtIndex(index: Int): T {
                    return array[index]
                }

                override val size: Int get() = array.size
            }
        }

        fun <T> empty(): Sequential<T> {
            return object : AbstractSequential<T>() {
                override fun getAtIndex(index: Int): T {
                    throw IndexOutOfBoundsException()
                }

                override val size = 0
            }
        }

        fun of(string: String): Sequential<Char> {
            return object : AbstractSequential<Char>() {
                override fun getAtIndex(index: Int): Char {
                    return string[index]
                }

                override val size: Int get() = string.length
            }
        }
    }
}