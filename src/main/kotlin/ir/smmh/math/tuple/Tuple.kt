package ir.smmh.math.tuple

import ir.smmh.math.InfinitelyIterable
import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical
import ir.smmh.nile.verbs.CanClear

sealed interface Tuple : MathematicalObject {
    operator fun get(index: Int): MathematicalObject

    val overParts: Iterable<MathematicalObject>

    override val debugText: String
        get() = overParts.joinToString(", ", "(", ")", limit = if (this is Infinitary) 10 else -1) { it.debugText }

    interface Finitary : Tuple {
        val length: Int
        override val overParts: Iterable<MathematicalObject>
            get() = Iterable<MathematicalObject> {
                var i = 0
                object : Iterator<MathematicalObject> {
                    override fun hasNext() = i < length
                    override fun next() = get(i++)
                }
            }

        override fun isNonReferentiallyEqualTo(that: MathematicalObject): Knowable {
            if (that is Tuple.Finitary && length == that.length) {
                for (i in 0 until length) if (this[i] != that[i]) return Logical.False
                return Logical.True
            }
            return Logical.False
        }
    }

    interface Infinitary : Tuple {
        override val overParts: InfinitelyIterable<MathematicalObject>
            get() = InfinitelyIterable<MathematicalObject> {
                var i = 0
                object : InfinitelyIterable.Iterator<MathematicalObject> {
                    override fun next() = get(i++)
                }
            }
    }

    interface Uniform<T : MathematicalObject> : Tuple {
        override fun get(index: Int): T
        interface Finitary<T : MathematicalObject> : Tuple.Finitary, Uniform<T> {
            override val overParts: Iterable<T>
                get() = Iterable<T> {
                    var i = 0
                    object : Iterator<T> {
                        override fun hasNext() = i < length
                        override fun next() = get(i++)
                    }
                }
        }

        interface Infinitary<T : MathematicalObject> : Tuple.Infinitary, Uniform<T> {
            override val overParts: InfinitelyIterable<T>
                get() = InfinitelyIterable<T> {
                    var i = 0
                    object : InfinitelyIterable.Iterator<T> {
                        override fun next() = get(i++)
                    }
                }
        }
    }

    interface Nullary : Finitary {
        override val length: Int get() = 0
        override fun get(index: Int) = throw TupleIndexOutOfBoundsException(index)
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
                else throw TupleIndexOutOfBoundsException(index)
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
            else throw TupleIndexOutOfBoundsException(index)

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
                else throw TupleIndexOutOfBoundsException(index)
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
            else throw TupleIndexOutOfBoundsException(index)

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
                else throw TupleIndexOutOfBoundsException(index)
        }
    }

    interface Factory<T : Tuple> : CanClear {
        fun create(vararg values: MathematicalObject): T
        fun destroy(it: T)
    }
}