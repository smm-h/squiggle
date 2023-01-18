package ir.smmh.math.tuple

import ir.smmh.math.MathematicalObject
import ir.smmh.nile.verbs.CanClear

sealed interface Tuple : MathematicalObject {
    val length: Int
    operator fun get(index: Int): MathematicalObject

    interface Finitary : Tuple {
        override fun isNonReferentiallyEqualTo(that: MathematicalObject): Boolean? {
            if (that is Tuple && length == that.length) {
                for (i in 0 until length) if (this[i] != that[i]) return false
                return true
            }
            return false
        }
    }

    interface Infinitary : Tuple

    interface Nullary : Finitary {
        override val length: Int get() = 0
        override fun get(index: Int) = throw IndexOutOfBoundsException()
    }

    interface Unary : Finitary {
        override val length: Int get() = 1
        val singleton: MathematicalObject
        operator fun component1(): MathematicalObject = singleton

        interface Specific<T : MathematicalObject> : Unary, Uniform<T> {
            override val singleton: T
            override fun component1(): T = singleton
            override fun get(index: Int): T =
                if (index == 0) singleton
                else throw IndexOutOfBoundsException()
        }
    }

    interface Binary : Finitary {
        override val length: Int get() = 2
        val first: MathematicalObject
        val second: MathematicalObject
        operator fun component1(): MathematicalObject = first
        operator fun component2(): MathematicalObject = second
        override fun get(index: Int): MathematicalObject =
            if (index == 0) first
            else if (index == 1) second
            else throw IndexOutOfBoundsException()

        interface Specific<T1 : MathematicalObject, T2 : MathematicalObject> : Tuple.Binary {
            override val first: T1
            override val second: T2
            override fun component1(): T1 = first
            override fun component2(): T2 = second
        }

        interface Uniform<T : MathematicalObject> : Tuple.Uniform<T>, Specific<T, T> {
            override val first: T
            override val second: T
            override fun component1(): T = first
            override fun component2(): T = second
            override fun get(index: Int): T =
                if (index == 0) first
                else if (index == 1) second
                else throw IndexOutOfBoundsException()
        }
    }

    interface Ternary : Finitary {
        override val length: Int get() = 3
        val first: MathematicalObject
        val second: MathematicalObject
        val third: MathematicalObject
        operator fun component1(): MathematicalObject = first
        operator fun component2(): MathematicalObject = second
        operator fun component3(): MathematicalObject = third
        override fun get(index: Int): MathematicalObject =
            if (index == 0) first
            else if (index == 1) second
            else if (index == 2) third
            else throw IndexOutOfBoundsException()

        interface Specific<T1 : MathematicalObject, T2 : MathematicalObject, T3 : MathematicalObject> : Tuple.Ternary {
            override val first: T1
            override val second: T2
            override val third: T3
            override fun component1(): T1 = first
            override fun component2(): T2 = second
            override fun component3(): T3 = third
        }

        interface Uniform<T : MathematicalObject> : Tuple.Uniform<T>, Specific<T, T, T> {
            override val first: T
            override val second: T
            override val third: T
            override fun component1(): T = first
            override fun component2(): T = second
            override fun component3(): T = third
            override fun get(index: Int): T =
                if (index == 0) first
                else if (index == 1) second
                else if (index == 2) third
                else throw IndexOutOfBoundsException()
        }
    }

    interface Uniform<T : MathematicalObject> : Tuple {
        override fun get(index: Int): T
    }

    interface Factory<T : Tuple> : CanClear {
        fun create(vararg values: MathematicalObject): T
        fun destroy(it: T)
    }
}