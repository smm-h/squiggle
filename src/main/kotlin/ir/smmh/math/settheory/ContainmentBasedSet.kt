package ir.smmh.math.settheory

import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject
import kotlin.random.Random

abstract class ContainmentBasedSet<T : MathematicalObject>(
    override val debugText: String,
    private val containment: (T) -> Boolean,
) : Set<T> {

    override fun contains(it: T): Boolean = containment(it)

    class Finite<T : MathematicalObject>(
        debugText: String,
        override val cardinality: Int,
        containment: (T) -> Boolean,
    ) : ContainmentBasedSet<T>(debugText, containment), Set.Finite<T> {
        override val overElements: Iterable<T>? get() = null
        override fun singletonOrNull(): T? = null
        override fun isNonReferentiallyEqualTo(that: MathematicalObject) = null
        override fun getPicker(random: Random): MathematicalCollection.Picker<T>? = null
    }

    class Infinite<T : MathematicalObject>(
        debugText: String,
        containment: (T) -> Boolean,
    ) : ContainmentBasedSet<T>(debugText, containment), Set.Infinite<T> {
        override val overElements: MathematicalCollection.InfinitelyIterable<T>? get() = null
        override fun isNonReferentiallyEqualTo(that: MathematicalObject) = null
        override fun getPicker(random: Random): MathematicalCollection.Picker<T>? = null
    }
}