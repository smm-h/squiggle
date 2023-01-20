package ir.smmh.math.relation

import ir.smmh.math.logic.Knowable
import ir.smmh.math.settheory.Set
import ir.smmh.math.tuple.Tuple
import ir.smmh.nile.verbs.CanChangeValues
import ir.smmh.math.MathematicalObject as M

interface Relation<TT : Tuple> : M {

    val holds: Set<TT>

    override val debugText: String get() = "Relation:${holds.debugText}"
    override fun isNonReferentiallyEqualTo(that: M): Knowable =
        if (that is Relation<*> && that.holds == holds) Knowable.Known.True else Knowable.Known.False

    interface Finitary<TT : Tuple.Finitary> : Relation<TT>
    interface Infinitary<TT : Tuple.Infinitary> : Relation<TT>

    /**
     * [Wikipedia](https://en.wikipedia.org/wiki/Homogeneous_relation)
     */
    interface Homogeneous<T : M, TT : Tuple.Uniform<T>> : Relation<TT> {
        val domain: Set<T>

        interface Finite<T : M, TT : Tuple.Uniform<T>> : Homogeneous<T, TT> {
            override val holds: Set.Finite<TT>
        }

        interface Infinite<T : M, TT : Tuple.Uniform<T>> : Homogeneous<T, TT> {
            override val holds: Set.Infinite<TT>
        }
    }

    interface Binary<T1 : M, T2 : M, TT : Tuple.Binary.Specific<T1, T2>> : Finitary<TT> {
        val domain: Set<T1>
        val codomain: Set<T2>

        operator fun get(a: T1, b: T2): Boolean

        interface Mutable<T1 : M, T2 : M, TT : Tuple.Binary.Specific<T1, T2>> : Binary<T1, T2, TT>, CanChangeValues {
            operator fun set(a: T1, b: T2, holds: Boolean)
            fun hold(a: T1, b: T2) = set(a, b, true)
            fun unhold(a: T1, b: T2) = set(a, b, false)
        }

        interface Finite<T1 : M, T2 : M, TT : Tuple.Binary.Specific<T1, T2>> : Binary<T1, T2, TT> {
            override val holds: Set.Finite<TT>
            override val domain: Set.Finite<T1>
            override val codomain: Set.Finite<T2>
        }

        interface Infinite<T1 : M, T2 : M, TT : Tuple.Binary.Specific<T1, T2>> : Binary<T1, T2, TT> {
            override val holds: Set.Infinite<TT>
            override val domain: Set.Infinite<T1>
            override val codomain: Set.Infinite<T2>
        }

        interface Homogeneous<T : M, TT : Tuple.Binary.Uniform<T>> : Relation.Homogeneous<T, TT>, Binary<T, T, TT> {
            override val domain: Set<T>
            override val codomain: Set<T> get() = domain

            interface Reflexive<T : M, TT : Tuple.Binary.Uniform<T>> : Homogeneous<T, TT>
            interface Irreflexive<T : M, TT : Tuple.Binary.Uniform<T>> : Homogeneous<T, TT>
            interface Symmetric<T : M, TT : Tuple.Binary.Uniform<T>> : Homogeneous<T, TT>
            interface Antisymmetric<T : M, TT : Tuple.Binary.Uniform<T>> : Homogeneous<T, TT>
            interface Asymmetric<T : M, TT : Tuple.Binary.Uniform<T>> : Antisymmetric<T, TT>, Irreflexive<T, TT>
            interface Transitive<T : M, TT : Tuple.Binary.Uniform<T>> : Homogeneous<T, TT>
            interface Connected<T : M, TT : Tuple.Binary.Uniform<T>> : Homogeneous<T, TT>
            interface StronglyConnected<T : M, TT : Tuple.Binary.Uniform<T>> : Connected<T, TT>, Reflexive<T, TT>
            interface Dense<T : M, TT : Tuple.Binary.Uniform<T>> : Homogeneous<T, TT>

            interface Mutable<T : M, TT : Tuple.Binary.Uniform<T>> : Homogeneous<T, TT>, Binary.Mutable<T, T, TT>

            interface Finite<T : M, TT : Tuple.Binary.Uniform<T>> :
                Homogeneous<T, TT>, Relation.Homogeneous.Finite<T, TT> {
                override val holds: Set.Finite<TT>
                override val domain: Set.Finite<T>
                override val codomain: Set.Finite<T> get() = domain
            }

            interface Infinite<T : M, TT : Tuple.Binary.Uniform<T>> :
                Homogeneous<T, TT>, Relation.Homogeneous.Infinite<T, TT> {
                override val domain: Set.Infinite<T>
                override val codomain: Set.Infinite<T> get() = domain
            }
        }
    }
}