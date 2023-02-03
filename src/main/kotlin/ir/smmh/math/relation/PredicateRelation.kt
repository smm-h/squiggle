package ir.smmh.math.relation

import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical
import ir.smmh.math.settheory.Set
import ir.smmh.math.tuple.Tuple
import kotlin.random.Random
import ir.smmh.math.MathematicalObject as M


sealed class PredicateRelation<T1 : M, T2 : M> : Relation.Binary<T1, T2> {

    class Heterogeneous<T1 : M, T2 : M>(
        override val tex: String,
        val predicate: (T1, T2) -> Logical,
    ) : PredicateRelation<T1, T2>() {
        override fun get(a: T1, b: T2) = predicate(a, b)
        override val holds = object : Set<Tuple.Binary.Specific<T1, T2>> {
            override val debugText: String get() = "PredicateRelation.Heterogeneous.holds"
            override val tex by this@Heterogeneous::tex
            override val overElements = null
            override fun contains(it: Tuple.Binary.Specific<T1, T2>) = get(it.first, it.second)
            override fun isEmpty() = Knowable.Unknown
            override fun getPicker(random: Random) = null
            override fun isNonReferentiallyEqualTo(that: M) = Knowable.Unknown
        }
    }

    class Homogeneous<T : M>(
        override val tex: String,
        val predicate: (T, T) -> Logical,
    ) : PredicateRelation<T, T>(), Relation.Binary.Homogeneous<T> {
        override fun get(a: T, b: T) = predicate(a, b)
        override val holds = object : Set<Tuple.Binary.Uniform<T>> {
            override val debugText: String get() = "PredicateRelation.Homogeneous.holds"
            override val tex by this@Homogeneous::tex
            override val overElements = null
            override fun contains(it: Tuple.Binary.Uniform<T>) = get(it.first, it.second)
            override fun isEmpty() = Knowable.Unknown
            override fun getPicker(random: Random) = null
            override fun isNonReferentiallyEqualTo(that: M) = Knowable.Unknown
        }
    }
}