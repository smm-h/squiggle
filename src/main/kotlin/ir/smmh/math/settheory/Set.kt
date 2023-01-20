package ir.smmh.math.settheory

import ir.smmh.math.MathematicalCollection
import kotlin.random.Random
import ir.smmh.math.MathematicalObject as M

interface Set<T : M> : MathematicalCollection<T>, MathematicalCollection.DisallowsDuplicates<T> {

//    infix fun superset(that: Set<T>): Knowable = (this isEqualTo that) or (this supersetNeq that)
//    infix fun supersetNeq(that: Set<T>): Knowable

    interface Finite<T : M> : Set<T>, MathematicalCollection.Finite<T> {
        override fun getPicker(random: Random): MathematicalCollection.Picker<T>? {
            val elements = overElements
            return if (elements == null) null else ListPicker(elements.toList(), random)
        }

        interface KnownCardinality<T : M> : Finite<T>, MathematicalCollection.Finite.KnownCardinality<T>
    }

    interface Infinite<T : M> : Set<T>, MathematicalCollection.Infinite<T>

    interface Ordered<T : M> : Set<T>, MathematicalCollection.OrderMatters<T> {
        interface Finite<T : M> : Ordered<T>, Set.Finite<T>
        interface Infinite<T : M> : Ordered<T>, Set.Infinite<T>
    }
}