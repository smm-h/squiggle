package ir.smmh.math.settheory

import ir.smmh.math.MathematicalObject
import ir.smmh.math.MathematicalCollection

sealed interface Set<T : MathematicalObject> : MathematicalCollection<T>, MathematicalCollection.DisallowsDuplicates<T> {
    interface Finite<T : MathematicalObject> : Set<T>, MathematicalCollection.Finite<T>
    interface Infinite<T : MathematicalObject> : Set<T>, MathematicalCollection.Infinite<T>

    sealed interface Ordered<T : MathematicalObject> : Set<T>, MathematicalCollection.OrderMatters<T> {
        interface Finite<T : MathematicalObject> : Ordered<T>, Set.Finite<T>
        interface Infinite<T : MathematicalObject> : Ordered<T>, Set.Infinite<T>
    }
}