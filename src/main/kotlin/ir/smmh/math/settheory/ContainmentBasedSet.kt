package ir.smmh.math.settheory

import ir.smmh.math.InfinitelyIterable
import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Knowable
import kotlin.random.Random

sealed class ContainmentBasedSet<T : MathematicalObject>(
    override val debugText: String,
    private val containment: (T) -> Boolean,
) : Set<T> {

    override fun contains(it: T): Boolean = containment(it)

    override fun isNonReferentiallyEqualTo(that: MathematicalObject) = Knowable.Unknown
    override fun getPicker(random: Random): MathematicalCollection.Picker<T>? = null

    sealed class Finite<T : MathematicalObject>(
        debugText: String,
        containment: (T) -> Boolean,
    ) : ContainmentBasedSet<T>(debugText, containment), Set.Finite<T> {
        override val overElements: Iterable<T>? get() = null
        override fun singletonOrNull(): T? = null

        override fun getPicker(random: Random): MathematicalCollection.Picker<T>? = null

        class KnownCardinality<T : MathematicalObject>(
            debugText: String,
            override val cardinality: Int,
            containment: (T) -> Boolean,
        ) : ContainmentBasedSet.Finite<T>(debugText, containment), Set.Finite.KnownCardinality<T>

        class UnknownCardinality<T : MathematicalObject>(
            debugText: String,
            containment: (T) -> Boolean,
        ) : ContainmentBasedSet.Finite<T>(debugText, containment), Set.Finite<T> {
            override val cardinality = null
        }
    }

    class Infinite<T : MathematicalObject>(
        debugText: String,
        containment: (T) -> Boolean,
    ) : ContainmentBasedSet<T>(debugText, containment), Set.Infinite<T> {
        override val overElements: InfinitelyIterable<T>? get() = null
    }
}