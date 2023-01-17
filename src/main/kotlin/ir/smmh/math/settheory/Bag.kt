package ir.smmh.math.settheory

import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject

sealed interface Bag<T : MathematicalObject> : MathematicalCollection<T> {
    val overUniqueElements: Iterable<T>? get() = null

    interface Finite<T : MathematicalObject> : Bag<T>, MathematicalCollection.Finite<T>
    interface Infinite<T : MathematicalObject> : Bag<T>, MathematicalCollection.Infinite<T> {
        override val overUniqueElements: MathematicalCollection.InfinitelyIterable<T>? get() = null
    }
}