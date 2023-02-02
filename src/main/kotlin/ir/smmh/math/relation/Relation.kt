package ir.smmh.math.relation

import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical
import ir.smmh.math.settheory.Set
import ir.smmh.math.tuple.Tuple
import ir.smmh.nile.verbs.CanChangeValues
import ir.smmh.math.MathematicalObject as M

interface Relation : M {
    val holds: Set<out Tuple>

    override val debugText: String get() = "Relation:${holds.debugText}"
    override fun isNonReferentiallyEqualTo(that: M): Knowable =
        if (that is Relation && that.holds == holds) Logical.True else Logical.False

    interface Finitary : Relation {
        override val holds: Set<out Tuple.Finitary>
    }

    interface Infinitary : Relation {
        override val holds: Set<out Tuple.Infinitary>
    }

    /**
     * [Wikipedia](https://en.wikipedia.org/wiki/Homogeneous_relation)
     */
    interface Homogeneous<T : M> : Relation {
        interface Finite<T : M> : Homogeneous<T> {
            override val holds: Set.Finite<out Tuple.Uniform<T>>
        }

        interface Infinite<T : M> : Homogeneous<T> {
            override val holds: Set.Infinite<out Tuple.Uniform<T>>
        }
    }

    interface Binary<T1 : M, T2 : M> : Finitary {
        override val holds: Set<out Tuple.Binary.Specific<T1, T2>>

        operator fun get(a: T1, b: T2): Logical

        val reciprocal: Binary<T2, T1>
            get() = PredicateRelation.Heterogeneous<T2, T1> { a, b -> get(b, a) }

        interface Mutable<T1 : M, T2 : M> : Binary<T1, T2>, CanChangeValues {
            operator fun set(a: T1, b: T2, holds: Boolean)
            fun hold(a: T1, b: T2) = set(a, b, true)
            fun unhold(a: T1, b: T2) = set(a, b, false)
        }

        interface Finite<T1 : M, T2 : M> : Binary<T1, T2> {
            override val holds: Set.Finite<out Tuple.Binary.Specific<T1, T2>>
        }

        interface Infinite<T1 : M, T2 : M> : Binary<T1, T2> {
            override val holds: Set.Infinite<out Tuple.Binary.Specific<T1, T2>>
        }

        interface Homogeneous<T : M> : Relation.Homogeneous<T>, Binary<T, T> {
            override val holds: Set<out Tuple.Binary.Uniform<T>>

            override val reciprocal: Homogeneous<T>
                get() = PredicateRelation.Homogeneous<T> { a, b -> get(b, a) }

            interface Reflexive<T : M> : Homogeneous<T>
            interface Irreflexive<T : M> : Homogeneous<T> // TODO iff Asymmetric, Transitive
            interface Symmetric<T : M> : Homogeneous<T>
            interface Antisymmetric<T : M> : Homogeneous<T>
            interface Asymmetric<T : M> : Antisymmetric<T>, Irreflexive<T>
            interface Transitive<T : M> : Homogeneous<T>
            interface Connected<T : M> : Homogeneous<T>
            interface StronglyConnected<T : M> : Connected<T>, Reflexive<T>
            interface Dense<T : M> : Homogeneous<T>

            interface Mutable<T : M> : Homogeneous<T>, Binary.Mutable<T, T>

            interface Finite<T : M> : Homogeneous<T>, Relation.Homogeneous.Finite<T> {
                override val holds: Set.Finite<out Tuple.Binary.Uniform<T>>
            }

            interface Infinite<T : M> : Homogeneous<T>, Relation.Homogeneous.Infinite<T> {
                override val holds: Set.Infinite<out Tuple.Binary.Uniform<T>>
            }
        }
    }
}