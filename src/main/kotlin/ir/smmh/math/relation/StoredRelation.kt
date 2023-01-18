package ir.smmh.math.relation

import ir.smmh.math.MathematicalObject
import ir.smmh.math.settheory.Set
import ir.smmh.math.settheory.StoredSet
import ir.smmh.math.tuple.SmallTuple
import ir.smmh.math.tuple.Tuple
import kotlin.random.Random

class StoredRelation<T : MathematicalObject> private constructor(
    override val domain: Set.Finite<T>,
    override val holds: StoredSet<Tuple.Binary.Uniform<T>>,
) : Relation.Binary.Closed.Finite<T, Tuple.Binary.Uniform<T>> {

    override fun get(a: T, b: T) = holds.contains(SmallTuple.Uniform.Couple(a, b))

    companion object {
        fun <T : MathematicalObject, TupleType : Tuple.Binary.Uniform<T>> Relation.Binary.Closed.Finite<T, TupleType>.toStoredRelation() =
            StoredRelation.of(holds.overElements!!)

        fun <T : MathematicalObject> empty() =
            of(emptyList<Tuple.Binary.Uniform<T>>())

        fun <T : MathematicalObject> of(vararg pairs: Tuple.Binary.Uniform<T>) =
            StoredRelation.of(pairs.asList())

        fun <T : MathematicalObject> of(pairs: Iterable<Tuple.Binary.Uniform<T>>) =
            StoredRelation(StoredSet(HashSet<T>().apply {
                for (pair in pairs) {
                    add(pair.first)
                    add(pair.second)
                }
            }), StoredSet(pairs))
    }
}