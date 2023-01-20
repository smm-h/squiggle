package ir.smmh.math.relation

import ir.smmh.math.logic.Knowable.Known
import ir.smmh.math.logic.Knowable.Known.True
import ir.smmh.math.relation.ComparisonResult.Comparable
import ir.smmh.math.relation.ComparisonResult.Comparable.*
import ir.smmh.math.tuple.Tuple
import ir.smmh.math.MathematicalObject as M

/**
 * A [TotalOrder] is a [PartialOrder] where every two elements are [Comparable].
 *
 * [Wikipedia](https://en.wikipedia.org/wiki/Total_order)
 */
sealed interface TotalOrder<T : M> : PartialOrder<T> {
    override fun compare(a: T, b: T): ComparisonResult.Comparable

    interface NonStrict<T : M> : TotalOrder<T>, PartialOrder.NonStrict<T> {
        override fun compare(a: T, b: T): Comparable = super.compare(a, b) as Comparable
    }

    interface Strict<T : M> : TotalOrder<T>, PartialOrder.Strict<T> {
        override fun compare(a: T, b: T): Comparable = super.compare(a, b) as Comparable
    }

    override fun isLessThan(a: T, b: T) = Known.of(compare(a, b) == LessThan)
    override fun isLessThanOrEqualTo(a: T, b: T) = Known.of(compare(a, b) != GreaterThan)
    override fun isGreaterThan(a: T, b: T) = Known.of(compare(a, b) == GreaterThan)
    override fun isGreaterThanOrEqualTo(a: T, b: T) = Known.of(compare(a, b) != LessThan)
    override fun areEqual(a: T, b: T) = Known.of(compare(a, b) == EqualTo)
    override fun areComparable(a: T, b: T) = True
}