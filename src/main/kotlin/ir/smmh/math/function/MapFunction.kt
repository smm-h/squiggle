package ir.smmh.math.function

import ir.smmh.math.settheory.Set
import ir.smmh.math.MathematicalObject as M

class MapFunction<I : M, O : M> private constructor(
    override val domain: Set.Finite<I>,
    override val codomain: Set.Finite<O>,
    private val map: Map<I, O>,
) : Function.Univariate.Finite<I, O> {
    override fun invoke(a: I): O = map[a]!!

    class Closed<T : M> private constructor(
        override val domainAndCodomains: Set.Finite<T>,
        private val map: Map<T, T>,
    ) : Function.Univariate.Closed.Finite<T> {
        override fun invoke(a: T): T? = map[a]

        companion object {
            fun <T : M> of(domain: Set.Finite<T>, map: Map<T, T>) =
                Closed(domain, map)
        }
    }

    companion object {
        fun <I : M, O : M> of(domain: Set.Finite<I>, codomain: Set.Finite<O>, map: Map<I, O>) =
            MapFunction(domain, codomain, map)

        fun <T : M> of(domain: Set.Finite<T>, map: Map<T, T>) =
            Closed.of(domain, map)
    }
}