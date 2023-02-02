package ir.smmh.math.logic

import ir.smmh.math.MathematicalCollection
import ir.smmh.math.ordertheory.TotalOrder
import ir.smmh.math.settheory.ContainmentBasedSet
import ir.smmh.math.settheory.Set
import ir.smmh.math.tuple.Tuple
import kotlin.random.Random
import ir.smmh.math.MathematicalObject as M

/**
 * A [BooleanDomain] is a [Set] consisting of exactly two elements that denote
 * [falsehood] and [truth].
 *
 * @see Logical.Domain
 * @see AbstractTwoElementBooleanAlgebra
 * [Wikipedia](https://en.wikipedia.org/wiki/Boolean_domain)
 */
class BooleanDomain<T : M>(val falsehood: T, val truth: T) : M.Abstract(), Set.TotallyOrdered.Finite<T> {

    val elementList = listOf(falsehood, truth)

    override val overElements: Iterable<T> by ::elementList
    override val debugText: String = "BooleanDomain"
    override val cardinality: Int get() = 2
    override fun contains(it: T) = Logical.True
    override fun singletonOrNull() = null

    override fun isNonReferentiallyEqualTo(that: M) =
        Logical.of(that is Set.Finite<*> && that.cardinality == 2 && setOf(that.overElements) == elementList.toSet())

    override val order = object : TotalOrder.Strict<T> {
        override val debugText: String = "BooleanDomain.order"
        override fun get(a: T, b: T): Logical = Logical.of(a == falsehood && b == truth)
        override val holds: Set<out Tuple.Binary.Uniform<T>> =
            ContainmentBasedSet.Finite("BooleanDomain.order.holds", 1) { get(it.first, it.second) }
    }

    override fun getPicker(random: Random) = MathematicalCollection.Picker<T> {
        elementList[random.nextInt(1)]
    }
}