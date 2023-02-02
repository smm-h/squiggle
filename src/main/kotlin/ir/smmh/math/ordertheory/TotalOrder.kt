package ir.smmh.math.ordertheory

import ir.smmh.math.logic.Logical
import ir.smmh.math.logic.Logical.True
import ir.smmh.math.ordertheory.ComparisonResult.Comparable
import ir.smmh.math.ordertheory.ComparisonResult.Comparable.*
import ir.smmh.math.MathematicalObject as M

/**
 * A [TotalOrder] is a [PartialOrder] where every two elements are [Comparable].
 *
 * [Wikipedia](https://en.wikipedia.org/wiki/Total_order)
 */
sealed interface TotalOrder<T : M> : PartialOrder<T> {
    override fun compare(a: T, b: T): Comparable

    interface NonStrict<T : M> : TotalOrder<T>, PartialOrder.NonStrict<T> {
        override fun compare(a: T, b: T): Comparable = super.compare(a, b) as Comparable
    }

    interface Strict<T : M> : TotalOrder<T>, PartialOrder.Strict<T> {
        override fun compare(a: T, b: T): Comparable {
            val ab = get(a, b) == Logical.False
            val ba = get(b, a) == Logical.False
            return if (ab && ba) EqualTo
            else if (ab) LessThan
            else if (ba) GreaterThan
            else throw StrictOrderViolationException(a, b)
        }
    }

    override fun isLessThan(a: T, b: T) = Logical.of(compare(a, b) == LessThan)
    override fun isLessThanOrEqualTo(a: T, b: T) = Logical.of(compare(a, b) != GreaterThan)
    override fun isGreaterThan(a: T, b: T) = Logical.of(compare(a, b) == GreaterThan)
    override fun isGreaterThanOrEqualTo(a: T, b: T) = Logical.of(compare(a, b) != LessThan)
    override fun areEqual(a: T, b: T) = Logical.of(compare(a, b) == EqualTo)
    override fun areComparable(a: T, b: T) = True
}