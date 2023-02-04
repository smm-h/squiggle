package ir.smmh.math.settheory

import ir.smmh.math.InfinitelyIterable
import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject as M

sealed interface Bag<T : M> : MathematicalCollection<T> {
    val overUniqueElements: Iterable<T>?

    interface Finite<T : M> : Bag<T>, MathematicalCollection.Finite<T>
    interface Infinite<T : M> : Bag<T>, MathematicalCollection.Infinite<T> {
        override val overUniqueElements: InfinitelyIterable<T>?
    }
}