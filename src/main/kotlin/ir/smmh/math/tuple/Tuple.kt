package ir.smmh.math.tuple

import ir.smmh.math.InfinitelyIterable
import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical
import ir.smmh.nile.verbs.CanClear
import ir.smmh.math.MathematicalObject as M

sealed interface Tuple : M {
    operator fun get(index: Int): M

    val overParts: Iterable<M>

    override val debugText: String
        get() = overParts.joinToString(", ", "(", ")", limit = if (this is Infinitary) 10 else -1) { it.debugText }

    interface Finitary : Tuple {
        val length: Int
        override val overParts: Iterable<M>
            get() = Iterable<M> {
                var i = 0
                object : Iterator<M> {
                    override fun hasNext() = i < length
                    override fun next() = get(i++)
                }
            }

        override fun isNonReferentiallyEqualTo(that: M): Knowable {
            if (that is Tuple.Finitary && length == that.length) {
                for (i in 0 until length) if (this[i] != that[i]) return Logical.False
                return Logical.True
            }
            return Logical.False
        }
    }

    interface Infinitary : Tuple {
        override val overParts: InfinitelyIterable<M>
            get() = InfinitelyIterable<M> {
                var i = 0
                object : InfinitelyIterable.Iterator<M> {
                    override fun next() = get(i++)
                }
            }
    }

    interface Uniform<T : M> : Tuple {
        override fun get(index: Int): T
        interface Finitary<T : M> : Tuple.Finitary, Uniform<T> {
            override val overParts: Iterable<T>
                get() = Iterable<T> {
                    var i = 0
                    object : Iterator<T> {
                        override fun hasNext() = i < length
                        override fun next() = get(i++)
                    }
                }
        }

        interface Infinitary<T : M> : Tuple.Infinitary, Uniform<T> {
            override val overParts: InfinitelyIterable<T>
                get() = InfinitelyIterable<T> {
                    var i = 0
                    object : InfinitelyIterable.Iterator<T> {
                        override fun next() = get(i++)
                    }
                }
        }
    }

    object Nullary : AbstractFinitaryTuple() {
        override val debugText = "EmptyTuple"
        override val tex = "()"
        override val length = 0
        override fun get(index: Int) = throw TupleIndexOutOfBoundsException(index)
    }

    interface Unary : Finitary {
        override val length: Int get() = 1
        val singleton: M
        operator fun component1(): M = singleton

        interface Specific<T : M> : Unary, Uniform.Finitary<T> {
            override val singleton: T
            override fun component1(): T = singleton
            override fun get(index: Int): T =
                if (index == 0) singleton
                else throw TupleIndexOutOfBoundsException(index)
        }
    }

    interface Binary : Finitary {
        override val length: Int get() = 2
        val first: M
        val second: M
        operator fun component1(): M = first
        operator fun component2(): M = second
        override fun get(index: Int): M =
            if (index == 0) first
            else if (index == 1) second
            else throw TupleIndexOutOfBoundsException(index)

        interface Specific<T1 : M, T2 : M> : Tuple.Binary {
            override val first: T1
            override val second: T2
            override fun component1(): T1 = first
            override fun component2(): T2 = second
        }

        interface Uniform<T : M> : Tuple.Uniform.Finitary<T>, Specific<T, T> {
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
        val first: M
        val second: M
        val third: M
        operator fun component1(): M = first
        operator fun component2(): M = second
        operator fun component3(): M = third
        override fun get(index: Int): M =
            if (index == 0) first
            else if (index == 1) second
            else if (index == 2) third
            else throw TupleIndexOutOfBoundsException(index)

        interface Specific<T1 : M, T2 : M, T3 : M> : Tuple.Ternary {
            override val first: T1
            override val second: T2
            override val third: T3
            override fun component1(): T1 = first
            override fun component2(): T2 = second
            override fun component3(): T3 = third
        }

        interface Uniform<T : M> : Tuple.Uniform.Finitary<T>, Specific<T, T, T> {
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
        fun create(vararg values: M): T
        fun destroy(it: T)
    }
}