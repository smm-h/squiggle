package ir.smmh.math.settheory

import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject

interface Set<T : MathematicalObject> : MathematicalCollection<T>, MathematicalCollection.DisallowsDuplicates<T> {
    interface Finite<T : MathematicalObject> : Set<T>, MathematicalCollection.Finite<T>
    interface Infinite<T : MathematicalObject> : Set<T>, MathematicalCollection.Infinite<T>

    interface Ordered<T : MathematicalObject> : Set<T>, MathematicalCollection.OrderMatters<T> {
        interface Finite<T : MathematicalObject> : Ordered<T>, Set.Finite<T>
        interface Infinite<T : MathematicalObject> : Ordered<T>, Set.Infinite<T>
    }
}