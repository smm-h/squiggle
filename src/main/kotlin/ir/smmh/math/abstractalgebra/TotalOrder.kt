package ir.smmh.math.abstractalgebra

import ir.smmh.math.abstractalgebra.ComparisonResult.*
import ir.smmh.math.logic.Knowable.Known
import ir.smmh.math.logic.Knowable.Known.True
import ir.smmh.math.MathematicalObject as M

/**
 * A [TotalOrder] is a [PartialOrder] where every pair of elements have to be
 * comparable.
 *
 * [Wikipedia](https://en.wikipedia.org/wiki/Total_order)
 */
interface TotalOrder<T : M> : PartialOrder<T> {
    override fun compare(a: T, b: T) = super.compare(a, b) as ComparisonResult.Comparable
    override fun isLessThan(a: T, b: T) = Known.of(compare(a, b) == LessThan)
    override fun isLessThanOrEqualTo(a: T, b: T) = Known.of(compare(a, b) != GreaterThan)
    override fun isGreaterThan(a: T, b: T) = Known.of(compare(a, b) == GreaterThan)
    override fun isGreaterThanOrEqualTo(a: T, b: T) = Known.of(compare(a, b) != LessThan)
    override fun areEqual(a: T, b: T) = Known.of(compare(a, b) == EqualTo)
    override fun areComparable(a: T, b: T) = True
}