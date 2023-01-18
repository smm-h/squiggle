package ir.smmh.math.tuple

import ir.smmh.math.MathematicalObject
import ir.smmh.nile.verbs.CanClear

sealed interface Tuple : MathematicalObject {
    val length: Int
    operator fun get(index: Int): MathematicalObject

    interface Finitary : Tuple
    interface Infinitary : Tuple

    interface Nullary : Finitary {
        override val length: Int get() = 0
        override fun get(index: Int) = throw IndexOutOfBoundsException()
    }

    interface Unary : Finitary {
        override val length: Int get() = 1

        interface Specific<T : MathematicalObject> : Unary, Uniform<T> {
            override fun get(index: Int): T
        }
    }

    interface Binary : Finitary {
        override val length: Int get() = 2

        interface Specific<T1 : MathematicalObject, T2 : MathematicalObject> : Tuple.Binary
        interface Uniform<T : MathematicalObject> : Tuple.Uniform<T>, Specific<T, T>
    }

    interface Ternary : Finitary {
        override val length: Int get() = 3

        interface Specific<T1 : MathematicalObject, T2 : MathematicalObject, T3 : MathematicalObject> : Tuple.Ternary
        interface Uniform<T : MathematicalObject> : Tuple.Uniform<T>, Specific<T, T, T>
    }

    interface Uniform<T : MathematicalObject> : Tuple {
        override fun get(index: Int): T
    }

    interface Factory<T : Tuple> : CanClear {
        fun create(vararg values: MathematicalObject): T
        fun destroy(it: T)
    }
}