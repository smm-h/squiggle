package ir.smmh.math.relation

import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Knowable.Known.False
import ir.smmh.math.logic.Knowable.Known.True
import ir.smmh.math.logic.Knowable.Unknown
import ir.smmh.math.relation.ComparisonResult.Comparable
import ir.smmh.math.relation.ComparisonResult.Comparable.*
import ir.smmh.math.relation.ComparisonResult.Incomparable
import ir.smmh.math.relation.Relation.Binary.Homogeneous.*
import ir.smmh.math.tuple.Tuple
import ir.smmh.math.MathematicalObject as M

/**
 * A [PartialOrder] is a homogeneous binary relation
 * ([Relation.Binary.Homogeneous]) where if it holds for two elements from its
 * [domain], it implies one precedes the other. It can [compare] and return a
 * [ComparisonResult] for every two elements from its [domain].
 * The word "partial" implies that not every pair of elements have to be
 * [Comparable].
 *
 * [Wikipedia](https://en.wikipedia.org/wiki/Partially_ordered_set)
 */
sealed interface PartialOrder<T : M> : Reflexive<T>, Antisymmetric<T>, Transitive<T> {

    fun compare(a: T, b: T): ComparisonResult

    interface NonStrict<T : M> : PartialOrder<T> {
        override fun compare(a: T, b: T): ComparisonResult {
            val ab = get(a, b)
            val ba = get(b, a)
            return if (ab && ba) EqualTo
            else if (ab) LessThan
            else if (ba) GreaterThan
            else Incomparable
        }
    }

    interface Strict<T : M> : PartialOrder<T> {
        override fun compare(a: T, b: T): ComparisonResult {
            val ab = get(a, b)
            val ba = get(b, a)
            return if (ab && ba) EqualTo
            else if (ab) LessThan
            else if (ba) GreaterThan
            else Incomparable
        }
    }

    fun isLessThan(a: T, b: T): Knowable = when (compare(a, b)) {
        LessThan -> True
        Incomparable -> Unknown
        else -> False
    }

    fun isLessThanOrEqualTo(a: T, b: T): Knowable = when (compare(a, b)) {
        GreaterThan -> False
        Incomparable -> Unknown
        else -> True
    }

    fun isGreaterThan(a: T, b: T): Knowable = when (compare(a, b)) {
        GreaterThan -> True
        Incomparable -> Unknown
        else -> False
    }

    fun isGreaterThanOrEqualTo(a: T, b: T): Knowable = when (compare(a, b)) {
        LessThan -> False
        Incomparable -> Unknown
        else -> True
    }

    fun areEqual(a: T, b: T): Knowable = when (compare(a, b)) {
        EqualTo -> True
        Incomparable -> Unknown
        else -> False
    }

    fun areComparable(a: T, b: T): Knowable = when (compare(a, b)) {
        Incomparable -> False
        else -> True
    }
}