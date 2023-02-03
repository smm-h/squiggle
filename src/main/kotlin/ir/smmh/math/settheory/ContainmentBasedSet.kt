package ir.smmh.math.settheory

import ir.smmh.math.InfinitelyIterable
import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical
import kotlin.random.Random

sealed class ContainmentBasedSet<T : MathematicalObject>(
    override val debugText: String,
    override val tex: String,
    private val containment: (T) -> Logical,
) : Set<T> {

    override fun contains(it: T): Logical = containment(it)
    override fun isNonReferentiallyEqualTo(that: MathematicalObject) = Knowable.Unknown
    override fun getPicker(random: Random): MathematicalCollection.Picker<T>? = null
    override val overElements: Iterable<T>? get() = null

    class Finite<T : MathematicalObject>(
        debugText: String,
        tex: String,
        override val cardinality: Int,
        containment: (T) -> Logical,
    ) : ContainmentBasedSet<T>(debugText, tex, containment), Set.Finite<T> {
        override fun singletonOrNull(): T? = null
        override fun getPicker(random: Random): MathematicalCollection.Picker<T>? = null
    }

    class UnknownCardinality<T : MathematicalObject>(
        debugText: String,
        tex: String,
        containment: (T) -> Logical,
    ) : ContainmentBasedSet<T>(debugText, tex, containment), Set<T>, MathematicalCollection.UnknownCardinality<T>

    class Infinite<T : MathematicalObject>(
        debugText: String,
        tex: String,
        containment: (T) -> Logical,
    ) : ContainmentBasedSet<T>(debugText, tex, containment), Set.Infinite<T> {
        override val overElements: InfinitelyIterable<T>? get() = null
    }
}