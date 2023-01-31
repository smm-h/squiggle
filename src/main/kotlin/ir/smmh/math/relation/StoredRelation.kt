package ir.smmh.math.relation

import ir.smmh.math.MathematicalObject
import ir.smmh.math.settheory.StoredSet
import ir.smmh.math.tuple.SmallTuple
import ir.smmh.math.tuple.Tuple

class StoredRelation<T : MathematicalObject> private constructor(override val holds: StoredSet<Tuple.Binary.Uniform<T>>) :
    Relation.Binary.Homogeneous.Finite<T> {

    override fun get(a: T, b: T) = holds.contains(SmallTuple.Uniform.Couple(a, b))

    companion object {
        fun <T : MathematicalObject> Relation.Binary.Homogeneous.Finite<T>.toStoredRelation() =
            StoredRelation.of(holds.overElements!!)

        fun <T : MathematicalObject> empty() =
            of(emptyList<Tuple.Binary.Uniform<T>>())

        fun <T : MathematicalObject> of(vararg pairs: Tuple.Binary.Uniform<T>) =
            StoredRelation.of(pairs.asList())

        fun <T : MathematicalObject> of(pairs: Iterable<Tuple.Binary.Uniform<T>>) =
            StoredRelation(StoredSet(pairs))
    }
}