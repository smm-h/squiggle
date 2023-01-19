package ir.smmh.math.settheory

import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Knowable
import kotlin.random.Random

interface Set<T : MathematicalObject> : MathematicalCollection<T>, MathematicalCollection.DisallowsDuplicates<T> {

//    infix fun superset(that: Set<T>): Knowable = isEqualTo()
//    infix fun supersetNeq(that: Set<T>): Knowable

    interface Finite<T : MathematicalObject> : Set<T>, MathematicalCollection.Finite<T> {
        override fun getPicker(random: Random): MathematicalCollection.Picker<T>? {
            val elements = overElements
            return if (elements == null) null else ListPicker(elements.toList(), random)
        }
    }

    interface Infinite<T : MathematicalObject> : Set<T>, MathematicalCollection.Infinite<T>

    interface Ordered<T : MathematicalObject> : Set<T>, MathematicalCollection.OrderMatters<T> {
        interface Finite<T : MathematicalObject> : Ordered<T>, Set.Finite<T>
        interface Infinite<T : MathematicalObject> : Ordered<T>, Set.Infinite<T>
    }
}