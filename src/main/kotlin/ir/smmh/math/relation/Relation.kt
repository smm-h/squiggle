package ir.smmh.math.relation

import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Knowable
import ir.smmh.math.settheory.Set
import ir.smmh.math.tuple.Tuple
import ir.smmh.nile.verbs.CanChangeValues

interface Relation<TupleType : Tuple> : MathematicalObject {

    val holds: Set<TupleType>

    override val debugText: String get() = "Relation:${holds.debugText}"
    override fun isNonReferentiallyEqualTo(that: MathematicalObject): Knowable =
        if (that is Relation<*> && that.holds == holds) Knowable.Known.True else Knowable.Known.False

    interface Finitary<TupleType : Tuple.Finitary> : Relation<TupleType>
    interface Infinitary<TupleType : Tuple.Infinitary> : Relation<TupleType>

    interface Closed<T : MathematicalObject, TupleType : Tuple.Uniform<T>> : Relation<TupleType> {
        val domain: Set<T>

        interface Finite<T : MathematicalObject, TupleType : Tuple.Uniform<T>> : Closed<T, TupleType> {
            override val holds: Set.Finite<TupleType>
        }

        interface Infinite<T : MathematicalObject, TupleType : Tuple.Uniform<T>> : Closed<T, TupleType> {
            override val holds: Set.Infinite<TupleType>
        }
    }

    interface Binary<T1 : MathematicalObject, T2 : MathematicalObject, TupleType : Tuple.Binary.Specific<T1, T2>> :
        Finitary<TupleType> {
        val domain: Set<T1>
        val codomain: Set<T2>

        interface Mutable<T1 : MathematicalObject, T2 : MathematicalObject, TupleType : Tuple.Binary.Specific<T1, T2>> :
            Binary<T1, T2, TupleType>, CanChangeValues {
            operator fun set(a: T1, b: T2, holds: Boolean)
            fun hold(a: T1, b: T2) = set(a, b, true)
            fun unhold(a: T1, b: T2) = set(a, b, false)
        }

        interface Finite<T1 : MathematicalObject, T2 : MathematicalObject, TupleType : Tuple.Binary.Specific<T1, T2>> :
            Binary<T1, T2, TupleType> {
            override val holds: Set.Finite<TupleType>
            override val domain: Set.Finite<T1>
            override val codomain: Set.Finite<T2>
        }

        interface Infinite<T1 : MathematicalObject, T2 : MathematicalObject, TupleType : Tuple.Binary.Specific<T1, T2>> :
            Binary<T1, T2, TupleType> {
            override val holds: Set.Infinite<TupleType>
            override val domain: Set.Infinite<T1>
            override val codomain: Set.Infinite<T2>
        }

        interface Closed<T : MathematicalObject, TupleType : Tuple.Binary.Uniform<T>> :
            Relation.Closed<T, TupleType>, Binary<T, T, TupleType> {
            override val domain: Set<T>
            override val codomain: Set<T> get() = domain

            interface Mutable<T : MathematicalObject, TupleType : Tuple.Binary.Uniform<T>> :
                Closed<T, TupleType>,
                Binary.Mutable<T, T, TupleType>

            interface Finite<T : MathematicalObject, TupleType : Tuple.Binary.Uniform<T>> :
                Closed<T, TupleType>, Relation.Closed.Finite<T, TupleType> {
                override val holds: Set.Finite<TupleType>
                override val domain: Set.Finite<T>
                override val codomain: Set.Finite<T> get() = domain
            }

            interface Infinite<T : MathematicalObject, TupleType : Tuple.Binary.Uniform<T>> :
                Closed<T, TupleType>, Relation.Closed.Infinite<T, TupleType> {
                override val domain: Set.Infinite<T>
                override val codomain: Set.Infinite<T> get() = domain
            }
        }
    }
}