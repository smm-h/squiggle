package ir.smmh.math.relation

import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Knowable
import ir.smmh.math.settheory.Set
import ir.smmh.math.tuple.Tuple
import kotlin.random.Random

class PredicateInfiniteClosedBinaryRelation<T : MathematicalObject>(
    override val domain: Set.Infinite<T>,
    override val debugText: String = "Anonymous PredicateInfiniteClosedBinaryRelation",
    private val holdsOrNot: (T, T) -> Boolean,
) : Relation.Binary.Closed.Infinite<T, Tuple.Binary.Uniform<T>> {
    override val holds = object : Set.Infinite<Tuple.Binary.Uniform<T>> {
        override val overElements = null
        override val debugText: String get() = "${this@PredicateInfiniteClosedBinaryRelation.debugText}.holds"
        override fun contains(it: Tuple.Binary.Uniform<T>) = holdsOrNot(it.first, it.second)
        override fun getPicker(random: Random): MathematicalCollection.Picker<Tuple.Binary.Uniform<T>>? =
            domain.getPicker()?.let { MathematicalCollection.Picker<Tuple.Binary.Uniform<T>>(it::pickTwo) }

        override fun isNonReferentiallyEqualTo(that: MathematicalObject) =
            if (that is PredicateInfiniteClosedBinaryRelation<*> && that.holdsOrNot == holdsOrNot)
                Knowable.Known.True else Knowable.Unknown
    }
}