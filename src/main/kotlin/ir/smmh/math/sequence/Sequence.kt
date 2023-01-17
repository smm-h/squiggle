package ir.smmh.math.sequence

import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject

sealed interface Sequence<T : MathematicalObject> : MathematicalCollection.OrderMatters<T> {
    interface Finite<T : MathematicalObject> : Sequence<T>, MathematicalCollection.Finite<T>
    interface Infinite<T : MathematicalObject> : Sequence<T>, MathematicalCollection.Infinite<T>
}