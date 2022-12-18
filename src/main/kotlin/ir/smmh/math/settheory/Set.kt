package ir.smmh.math.settheory

import ir.smmh.math.symbolic.Expression

interface Set {

    class SetOperationException(val expression: Expression) : Exception("set operation undefined")

    fun containsGeneric(it: Any): Boolean

    operator fun contains(it: Any): Boolean = containsGeneric(it)

    // TODO val powerSet: Specific<Set>

    interface Specific<T : Any> : Set, NonEmpty {

        fun containsSpecific(it: T): Boolean

        override fun containsGeneric(it: Any): Boolean =
            try {
                @Suppress("UNCHECKED_CAST")
                containsSpecific(it as T)
            } catch (_: ClassCastException) {
                false
            }

        override val choose: () -> T
        override fun chooseTwo(): Pair<T, T> = choose() to choose()
        override fun chooseThree(): Triple<T, T, T> = Triple(choose(), choose(), choose())

        // interface Ordered<T : Any> : Specific<T>, Set.Ordered

        interface Finite<T : Any> : Specific<T>, Set.Finite.NonEmpty {

            fun singletonNullable(): T?
            fun singleton(): T = singletonNullable() ?: throw Exception("set is not a singleton")

            override val over: Iterable<T>

            class Singleton<T : Any>(val value: T) : AbstractSet(), Finite<T> {
                override fun singletonNullable(): T = value
                override val cardinality: Int get() = 1
                override val choose: () -> T = { value }
                override fun containsSpecific(it: T): Boolean = it == value
                override val over: Iterable<T> by lazy { listOf(value) }
            }

            class Universal<T : Any>(
                override val over: List<T>,
                override val choose: () -> T,
            ) : AbstractSet(), Finite<T> {
                override fun singletonNullable(): T? = null
                override val cardinality: Int = over.size
                override fun containsSpecific(it: T): Boolean = true
            }
        }

        sealed interface Infinite<T : Any> : Specific<T>, Set.Infinite {

            interface Countable<T : Any> : Specific<T>, Set.Infinite.Countable {
                class Universal<T : Any>(
                    override val choose: () -> T,
                ) : Countable<T> {
                    override fun containsSpecific(it: T): Boolean = true
                }
            }

            interface Uncountable<T : Any> : Specific<T>, Set.Infinite.Uncountable {
                class Universal<T : Any>(
                    override val choose: () -> T,
                ) : Uncountable<T> {
                    override fun containsSpecific(it: T): Boolean = true
                }
            }
        }

        /**
         * In mathematics, a bijection, also known as a bijective function,
         * one-to-one correspondence, or invertible function, is a function
         * between the elements of two sets, where each element of one set is
         * paired with exactly one element of the other set, and each element of
         * the other set is paired with exactly one element of the first set.
         */
        abstract class InvertibleFunction<T : Any, R : Any>() : (T) -> R {
            abstract val source: Set.Specific<T>
            abstract val destination: Set.Specific<R>
            abstract fun inverse(it: R): T
        }

        class Bijected<T : Any, R : Any>(
            val set: Specific<T>,
            val bijection: InvertibleFunction<T, R>,
        ) : Specific<R> {
            override fun containsSpecific(it: R): Boolean = set.containsSpecific(bijection.inverse(it))
            override val choose: () -> R = { bijection.invoke(set.choose()) }
        }
    }

    object Universal : Infinite.Uncountable {
        override val cardinality = CardinalNumber.absoluteInfinite
        override val choose = { Unit }
        override fun containsGeneric(it: Any) = it != Universal
    }

    object Empty : Finite {
        override val cardinality: Int = 0
        override fun containsGeneric(it: Any): Boolean = false
        // override val powerSet: Specific<Set> = Specific.Finite.Singleton(this)
    }

    interface NonEmpty : Set {
        val choose: () -> Any
        fun chooseTwo(): Pair<*, *> = choose() to choose()
        fun chooseThree(): Triple<*, *, *> = Triple(choose(), choose(), choose())
    }

//    TODO interface Ordered : NonEmpty
//        interface Partially : Ordered { val partialOrder: (Any, Any) -> Boolean }
//        interface Totally : Ordered {val totalOrder: (Any) -> Int }


    interface Finite : Set {
        val cardinality: Int

        interface NonEmpty : Finite, Set.NonEmpty {
            val over: Iterable<Any>

            class Singleton(val value: Any) : AbstractSet(), NonEmpty {
                override val cardinality: Int get() = 1
                override val choose: () -> Any = { value }
                override fun containsGeneric(it: Any): Boolean = it == value
                override val over: Iterable<Any> by lazy { listOf(value) }
            }
        }
    }

    sealed interface Infinite {

        interface Countable : NonEmpty {
            val cardinality: CardinalNumber get() = CardinalNumber.alephNought
        }

        interface Uncountable : NonEmpty {
            val cardinality: CardinalNumber get() = CardinalNumber.cardinalityOfTheContinuum
        }
    }

    companion object {
        fun <T : Any> of(vararg elements: T) =
            StoredSet<T>(elements.asList())
    }
}