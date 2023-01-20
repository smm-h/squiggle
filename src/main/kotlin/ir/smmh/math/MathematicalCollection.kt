package ir.smmh.math


import ir.smmh.math.MathematicalCollection.*
import ir.smmh.math.logic.Knowable
import ir.smmh.math.sequence.Sequence
import ir.smmh.math.settheory.Bag
import ir.smmh.math.settheory.Set
import ir.smmh.math.tuple.SmallTuple
import ir.smmh.math.tuple.Tuple
import kotlin.random.Random
import ir.smmh.math.MathematicalObject as M


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
interface MathematicalCollection<T : M> : M {

    // TODO val cardinality: Numbers.Cardinal
    operator fun contains(it: T): Boolean
    fun count(it: T): Int
    fun isEmpty(): Knowable
    fun isNotEmpty(): Knowable = !isEmpty()
    val overElements: Iterable<T>?
    fun getPicker(random: Random = Random): Picker<T>?

    fun containsAnyOf(vararg these: T): Boolean = containsAny(these.asList())
    fun containsAllOf(vararg these: T): Boolean = containsAll(these.asList())
    fun containsAny(them: Iterable<T>): Boolean = them.fold(false) { a, e -> a || contains(e) }
    fun containsAll(them: Iterable<T>): Boolean = them.fold(true) { a, e -> a && contains(e) }

    /**
     * [Sequence]/[Set.Ordered]
     */
    interface OrderMatters<T : M> : MathematicalCollection<T> {

        fun compare(a: T, b: T): Int

        // Measureable, Measure, measure(it: T): Int/Double/...

        interface Countable<T : M> {
            val first: T
            fun next(after: T): T
            fun nth(n: Int): T
        }
    }

    /**
     * [Set]
     */
    interface DisallowsDuplicates<T : M> : MathematicalCollection<T> {
        override fun count(it: T) = if (contains(it)) 1 else 0
    }

    /**
     * [Sequence.Finite], [Bag.Finite], [Set.Finite], [Set.Ordered.Finite]
     */
    interface Finite<T : M> : MathematicalCollection<T> {
        val cardinality: Int?
        override fun isEmpty(): Knowable = when (cardinality) {
            null -> Knowable.Unknown
            0 -> Knowable.Known.True
            else -> Knowable.Known.False
        }

        interface KnownCardinality<T : M> : Finite<T> {
            override val cardinality: Int
            override fun isEmpty(): Knowable.Known = Knowable.Known.of(cardinality == 0)
            override fun isNotEmpty(): Knowable.Known = !isEmpty()
        }

        fun singletonOrNull(): T?
        fun singleton(): T = singletonOrNull() ?: throw Exception("set is not a singleton")
    }

    /**
     * [Sequence.Infinite], [Bag.Infinite], [Set.Infinite], [Set.Ordered.Infinite]
     */
    interface Infinite<T : M> : MathematicalCollection<T> {
        override fun isEmpty() = Knowable.Known.False
        override fun isNotEmpty() = Knowable.Known.True
        override val overElements: InfinitelyIterable<T>?
    }

    /**
     * A helper object that picks randoms elements from a collection
     */
    fun interface Picker<T : M> {
        fun pick(): T

        fun pickTwo(): Tuple.Binary.Uniform<T> = SmallTuple.Uniform.Couple(pick(), pick())
        fun pickThree(): Tuple.Ternary.Uniform<T> = SmallTuple.Uniform.Triple(pick(), pick(), pick())
        //fun pickN(n: Int): Tuple.Uniform<T> = ArrayList<T>(n).apply { repeat(n) { add(pick()) } }
    }
}