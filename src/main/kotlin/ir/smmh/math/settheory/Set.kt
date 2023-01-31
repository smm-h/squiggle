package ir.smmh.math.settheory

import ir.smmh.math.MathematicalCollection
import ir.smmh.math.ordertheory.PartialOrder
import ir.smmh.math.ordertheory.TotalOrder
import kotlin.random.Random
import ir.smmh.math.MathematicalObject as M

interface Set<T : M> : MathematicalCollection<T>, MathematicalCollection.DisallowsDuplicates<T> {

//    infix fun superset(that: Set<T>): Knowable = (this isEqualTo that) or (this supersetNeq that)
//    infix fun supersetNeq(that: Set<T>): Knowable

    /**
     * A [PartiallyOrdered] set is a [Set] equipped with a [PartialOrder].
     *
     * [Wikipedia](https://en.wikipedia.org/wiki/Partially_ordered_set)
     */
    interface PartiallyOrdered<T : M> : Set<T>, MathematicalCollection.OrderMatters<T> {
        val order: PartialOrder<T>

        interface Finite<T : M> : PartiallyOrdered<T>, Set.Finite<T>
        interface Infinite<T : M> : PartiallyOrdered<T>, Set.Infinite<T>
    }

    /**
     * A [TotallyOrdered] set is a [Set] equipped with a [TotalOrder].
     *
     * [Wikipedia](https://en.wikipedia.org/wiki/Totally_ordered_set)
     */
    interface TotallyOrdered<T : M> : PartiallyOrdered<T> {
        override val order: TotalOrder<T>

        interface Finite<T : M> : TotallyOrdered<T>, PartiallyOrdered.Finite<T>
        interface Infinite<T : M> : TotallyOrdered<T>, PartiallyOrdered.Infinite<T>
    }

    interface Finite<T : M> : Set<T>, MathematicalCollection.Finite<T> {
        override fun getPicker(random: Random): MathematicalCollection.Picker<T>? {
            val elements = overElements
            return if (elements == null) null else ListPicker(elements.toList(), random)
        }
    }

    interface Infinite<T : M> : Set<T>, MathematicalCollection.Infinite<T>
}