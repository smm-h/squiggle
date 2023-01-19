package ir.smmh.math


import ir.smmh.math.MathematicalCollection.*
import ir.smmh.math.sequence.Sequence
import ir.smmh.math.settheory.Bag
import ir.smmh.math.settheory.Set
import ir.smmh.math.tuple.SmallTuple
import ir.smmh.math.tuple.Tuple
import kotlin.random.Random


/**
 * A [MathematicalCollection] is a collection of [MathematicalObject]s of type
 * [T].
 *
 * - The order of its elements do not matter unless [OrderMatters]
 * - Duplicate elements are allowed unless it [DisallowsDuplicates]
 * - The collection is either [Finite] or [Infinite]
 *
 * While you can mix and mash these properties to define your own collection
 * type, it is strongly recommended that you use one of the already defined
 * types of [Sequence], [Set], [Set.Ordered], and [Bag].
 */
interface MathematicalCollection<T : MathematicalObject> : MathematicalObject {

    // TODO val cardinality: Numbers.Cardinal
    operator fun contains(it: T): Boolean
    fun count(it: T): Int
    fun isEmpty(): Boolean
    fun isNotEmpty() = !isEmpty()
    val overElements: Iterable<T>?
    fun getPicker(random: Random = Random): Picker<T>?

    fun containsAnyOf(vararg these: T): Boolean = containsAny(these.asList())
    fun containsAllOf(vararg these: T): Boolean = containsAll(these.asList())
    fun containsAny(them: Iterable<T>): Boolean = them.fold(false) { a, e -> a || contains(e) }
    fun containsAll(them: Iterable<T>): Boolean = them.fold(true) { a, e -> a && contains(e) }

    /**
     * [Sequence]/[Set.Ordered]
     */
    interface OrderMatters<T : MathematicalObject> : MathematicalCollection<T> {

        fun compare(a: T, b: T): Int

        // Measureable, Measure, measure(it: T): Int/Double/...

        interface Countable<T : MathematicalObject> {
            val first: T
            fun next(after: T): T
            fun nth(n: Int): T
        }
    }

    /**
     * [Set]
     */
    interface DisallowsDuplicates<T : MathematicalObject> : MathematicalCollection<T> {
        override fun count(it: T) = if (contains(it)) 1 else 0
    }

    /**
     * [Sequence.Finite], [Bag.Finite], [Set.Finite], [Set.Ordered.Finite]
     */
    interface Finite<T : MathematicalObject> : MathematicalCollection<T> {
        val cardinality: Int
        override fun isEmpty() = cardinality == 0
        override fun isNotEmpty() = cardinality > 0

        fun singletonOrNull(): T?
        fun singleton(): T = singletonOrNull() ?: throw Exception("set is not a singleton")
    }

    /**
     * [Sequence.Infinite], [Bag.Infinite], [Set.Infinite], [Set.Ordered.Infinite]
     */
    interface Infinite<T : MathematicalObject> : MathematicalCollection<T> {
        override fun isEmpty() = false
        override fun isNotEmpty() = true
        override val overElements: InfinitelyIterable<T>?
    }

    fun interface InfinitelyIterable<T> : Iterable<T> {
        override fun iterator(): Iterator<T>

        fun interface Iterator<T> : kotlin.collections.Iterator<T> {
            override fun hasNext() = true
        }
    }

    /**
     * A helper object that picks randoms elements from a collection
     */
    fun interface Picker<T : MathematicalObject> {
        fun pick(): T

        fun pickTwo(): Tuple.Binary.Uniform<T> = SmallTuple.Uniform.Couple(pick(), pick())
        fun pickThree(): Tuple.Ternary.Uniform<T> = SmallTuple.Uniform.Triple(pick(), pick(), pick())
        //fun pickN(n: Int): Tuple.Uniform<T> = ArrayList<T>(n).apply { repeat(n) { add(pick()) } }
    }
}