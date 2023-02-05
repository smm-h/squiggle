package ir.smmh.math.function

import ir.smmh.math.function.Function.Univariate
import ir.smmh.math.symbolic.Calculable.Operation
import ir.smmh.math.symbolic.Context
import ir.smmh.math.MathematicalObject as M

/**
 * [FunctionComposition] is a binary [Operation] (`∘`) that takes two
 * [Univariate] functions and produces another: it takes [f]: [X] -> [Y] and
 * [g]: [Y] -> [Z] and produces `g∘f`: [X] -> [Z] such that `g∘f(x) = g(f(x))`.
 *
 * [Wikipedia](https://en.wikipedia.org/wiki/Function_composition)
 */
class FunctionComposition<X : M, Y : M, Z : M> private constructor(val f: Univariate<X, Y>, val g: Univariate<Y, Z>) :
    Operation("univariate_function_composition", listOf(f, g).map(::ContextIndependent)) {
    override fun calculate(context: Context) =
        Function.of<X, Z>(f.domain, g.codomain) {
            val m = f.invoke(it)
            if (m != null) g.invoke(m) else null
        }

    /**
     * [Closed] function composition is a [FunctionComposition] where [f] and
     * [g] are both [Closed] univariate functions of type [T].
     */
    class Closed<T : M> private constructor(val f: Univariate.Closed<T>, val g: Univariate.Closed<T>) :
        Operation("closed_univariate_function_composition", listOf(f, g).map(::ContextIndependent)) {
        override fun calculate(context: Context) =
            Function.of<T>(f.domainAndCodomains) {
                val m = f.invoke(it)
                if (m != null) g.invoke(m) else null
            }

        companion object {
            fun <T : M> of(f: Univariate.Closed<T>, g: Univariate.Closed<T>) =
                Closed(f, g)
        }
    }

    companion object {
        fun <X : M, Y : M, Z : M> of(f: Univariate<X, Y>, g: Univariate<Y, Z>) =
            FunctionComposition(f, g)

        fun <T : M> of(f: Univariate.Closed<T>, g: Univariate.Closed<T>) =
            Closed.of(f, g)
    }
}