package ir.smmh.math.settheory

interface Set {

    operator fun contains(it: Any): Boolean

    // TODO val powerSet: Specific<Set>

    interface Specific<T : Any> : Set, NonEmpty {

        fun containsSpecific(it: T): Boolean

        override fun contains(it: Any): Boolean =
            try {
                @Suppress("UNCHECKED_CAST")
                containsSpecific(it as T)
            } catch (_: ClassCastException) {
                false
            }

        override val choose: () -> T
        override fun chooseTwo(): Pair<T, T> = choose() to choose()
        override fun chooseThree(): Triple<T, T, T> = Triple(choose(), choose(), choose())

        interface Ordered<T : Any> : Specific<T>, Set.Ordered

        interface Finite<T : Any> : Specific<T>, Set.Finite {
            class Singleton<T : Any>(val value: T) : Finite<T> {
                override val cardinality: Int get() = 1
                override val choose: () -> T = { value }
                override fun containsSpecific(it: T): Boolean = it == value
            }

            class Universal<T : Any>(
                override val cardinality: Int,
                override val choose: () -> T,
            ) : Finite<T> {
                override fun containsSpecific(it: T): Boolean = true
            }
        }

        interface CountablyInfinite<T : Any> : Specific<T>, Set.CountablyInfinite {
            class Universal<T : Any>(
                override val choose: () -> T,
            ) : CountablyInfinite<T> {
                override fun containsSpecific(it: T): Boolean = true
            }
        }

        interface Uncountable<T : Any> : Specific<T>, Set.Uncountable {
            class Universal<T : Any>(
                override val choose: () -> T,
            ) : Uncountable<T> {
                override fun containsSpecific(it: T): Boolean = true
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

    object Universal : CountablyInfinite {
        override val cardinality = CardinalNumber.AbsoluteInfinite
        override val choose = { Unit }
        override fun contains(it: Any) = it != Universal
    }

    object Empty : Finite {
        override val cardinality: Int = 0
        override fun contains(it: Any): Boolean = false
        // override val powerSet: Specific<Set> = Specific.Finite.Singleton(this)
    }

    interface NonEmpty : Set {
        val choose: () -> Any
        fun chooseTwo(): Pair<*, *> = choose() to choose()
        fun chooseThree(): Triple<*, *, *> = Triple(choose(), choose(), choose())
    }

    interface Ordered : NonEmpty {

        // TODO partial/total
        val partialOrder: (Any, Any) -> Boolean
    }

    interface Finite : Set {
        val cardinality: Int

        interface NonEmpty : Finite, Set.NonEmpty {
            class Singleton(val value: Any) : NonEmpty {
                override val cardinality: Int get() = 1
                override val choose: () -> Any = { value }
                override fun contains(it: Any): Boolean = it == value
            }
        }
    }

    interface CountablyInfinite : NonEmpty {
        val cardinality: CardinalNumber get() = CardinalNumber.AlephNought
    }

    interface Uncountable : NonEmpty {
        val cardinality: CardinalNumber get() = CardinalNumber.CardinalityOfTheContinuum
    }
}