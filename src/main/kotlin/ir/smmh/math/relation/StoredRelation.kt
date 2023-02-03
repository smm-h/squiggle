package ir.smmh.math.relation

import ir.smmh.math.settheory.StoredSet
import ir.smmh.math.tuple.SmallTuple
import ir.smmh.math.tuple.Tuple
import ir.smmh.math.MathematicalObject as M

class StoredRelation<T : M> private constructor(
    override val holds: StoredSet<Tuple.Binary.Uniform<T>>,
) : Relation.Binary.Homogeneous.Finite<T> {

    override val tex by holds::tex

    override fun get(a: T, b: T) = holds.contains(SmallTuple.Uniform.Couple(a, b))

    companion object {
        fun <T : M> Relation.Binary.Homogeneous.Finite<T>.toStoredRelation() =
            StoredRelation.of(holds.overElements!!)

        fun <T : M> empty() =
            of(emptyList<Tuple.Binary.Uniform<T>>())

        fun <T : M> of(vararg pairs: Tuple.Binary.Uniform<T>) =
            StoredRelation.of(pairs.asList())

        fun <T : M> of(pairs: Iterable<Tuple.Binary.Uniform<T>>) =
            StoredRelation(StoredSet(pairs))
    }
}