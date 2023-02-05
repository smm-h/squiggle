package ir.smmh.math.function

import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Knowable
import ir.smmh.math.settheory.Set
import ir.smmh.math.tuple.Tuple
import ir.smmh.util.UnboundConstantList
import ir.smmh.math.MathematicalObject as M

/**
 * A [Function] is a [MathematicalObject] that when [invoke]d, takes a number of
 * objects and outputs exactly one object of type [O].
 */
interface Function<O : M> : M {

    operator fun invoke(t: Tuple): O?

    val domains: List<Set<*>>
    val codomain: Set<O>

    interface Finite<O : M> :
        Function<O> {
        override val domains: List<Set.Finite<*>>
        override val codomain: Set.Finite<O>
    }

    interface Infinite<O : M> :
        Function<O> {
        override val domains: List<Set.Infinite<*>>
        override val codomain: Set.Infinite<O>
    }

    override val debugText: String
        get() = "Function[${domains.joinToString(" ") { it.debugText }}->${codomain.debugText}]"

    override val tex: String
        get() = "{${domains.joinToString("\\times") { "{${it.tex}}" }}\\rightarrow{${codomain.tex}}}"

    override fun isNonReferentiallyEqualTo(that: M): Knowable =
        Knowable.Unknown

    /**
     * A [Uniform] function is a [Function] whose input objects are all of the
     * same type [I].
     */
    interface Uniform<I : M, O : M> :
        Function<O>

    /**
     * A [Closed] function is a [Function] whose input and output objects are
     * all of the same type [T].
     */
    interface Closed<T : M> :
        Uniform<T, T> {
        val domainAndCodomains: Set<T>
        override val codomain: Set<T>
            get() = domainAndCodomains
        override val domains: List<Set<T>>
            get() = UnboundConstantList(domainAndCodomains)

        interface Finite<T : M> :
            Closed<T>,
            Function.Finite<T> {
            override val domains: List<Set.Finite<T>>
            override val codomain: Set.Finite<T>
        }

        interface Infinite<T : M> :
            Closed<T>,
            Function.Infinite<T> {
            override val domains: List<Set.Infinite<T>>
            override val codomain: Set.Infinite<T>
        }
    }

    /**
     * A [Univariate] function is a [Function] that takes exactly one object of
     * type [I] as its input.
     */
    interface Univariate<I : M, O : M> :
        Function<O> {

        val domain: Set<I>

        override val domains: List<Set<I>>
            get() = listOf(domain)

        fun image(): Pair<List<I>, List<O?>>? =
            domain.overElements?.toList()?.let { it to image(it) }

        fun image(input: List<I>): List<O?> =
            input.map(::invoke)

        interface Finite<I : M, O : M> :
            Function.Univariate<I, O>,
            Function.Finite<O> {
            override val domain: Set.Finite<I>
            override val domains: List<Set.Finite<I>>
                get() = listOf(domain)
            override val codomain: Set.Finite<O>
        }

        interface Infinite<I : M, O : M> :
            Function.Univariate<I, O>,
            Function.Infinite<O> {
            override val domain: Set.Infinite<I>
            override val domains: List<Set.Infinite<I>>
                get() = listOf(domain)
            override val codomain: Set.Infinite<O>
        }

        @Suppress("UNCHECKED_CAST")
        override fun invoke(t: Tuple): O? =
            invoke((t as Tuple.Unary).singleton as I)

        operator fun invoke(a: I): O?

        /**
         * A [Closed] univariate function is a [Univariate] function whose
         * input and output objects are both of type [T].
         */
        interface Closed<T : M> :
            Function.Univariate<T, T>,
            Function.Closed<T> {

            override val domain: Set<T>
                get() = domainAndCodomains
            override val domains: List<Set<T>>
                get() = listOf(domainAndCodomains)
            override val codomain: Set<T>
                get() = domainAndCodomains

            interface Finite<T : M> :
                Univariate.Closed<T>,
                Univariate.Finite<T, T> {
                override val domainAndCodomains: Set.Finite<T>
                override val domain: Set.Finite<T>
                    get() = domainAndCodomains
                override val domains: List<Set.Finite<T>>
                    get() = listOf(domainAndCodomains)
                override val codomain: Set.Finite<T>
                    get() = domainAndCodomains
            }

            interface Infinite<T : M> :
                Univariate.Closed<T>,
                Univariate.Infinite<T, T> {
                override val domainAndCodomains: Set.Infinite<T>
                override val domain: Set.Infinite<T>
                    get() = domainAndCodomains
                override val domains: List<Set.Infinite<T>>
                    get() = listOf(domainAndCodomains)
                override val codomain: Set.Infinite<T>
                    get() = domainAndCodomains
            }
        }
    }

    /**
     * A [Multivariate] function is a [Function] that takes at least two, but
     * still finitely many, objects as its input.
     */
    interface Multivariate<O : M> :
        Function<O> {

        /**
         * A [Uniform] multivariate function is a [Multivariate] function whose
         * input objects are all of the same type [I].
         */
        interface Uniform<I : M, O : M> :
            Function.Multivariate<O>,
            Function.Uniform<I, O>

        /**
         * A [Closed] multivariate function is a [Multivariate] function whose
         * input and output objects are all of the same type [T].
         */
        interface Closed<T : M> :
            Function.Uniform<T, T>,
            Function.Closed<T>
    }

    /**
     * A [Bivariate] function is a [Function] that takes exactly two objects, of
     * types [I1] and [I2], as as its input.
     */
    interface Bivariate<I1 : M, I2 : M, O : M> :
        Function.Multivariate<O> {

        @Suppress("UNCHECKED_CAST")
        override fun invoke(t: Tuple): O? =
            invoke((t as Tuple.Binary).first as I1, t.second as I2)

        operator fun invoke(a: I1, b: I2): O?

        /**
         * A [Uniform] bivariate function is a [Bivariate] function whose input
         * objects are all of the same type [I].
         */
        interface Uniform<I : M, O : M> :
            Function.Bivariate<I, I, O>,
            Multivariate.Uniform<I, O>

        /**
         * A [Closed] bivariate function is a [Bivariate] function whose
         * both input objects and output object are of the same type [T].
         */
        interface Closed<T : M> :
            Bivariate.Uniform<T, T>,
            Multivariate.Closed<T>
    }

    private class FiniteNonClosedUnivariateFunctionImpl<I : M, O : M>(
        override val domain: Set.Finite<I>,
        override val codomain: Set.Finite<O>,
        val body: (I) -> O?,
    ) : Function.Univariate.Finite<I, O> {
        override fun invoke(a: I): O? = body(a)
    }

    private class FiniteClosedUnivariateFunctionImpl<T : M>(
        override val domainAndCodomains: Set.Finite<T>,
        val body: (T) -> T?,
    ) : Function.Univariate.Closed.Finite<T> {
        override fun invoke(a: T): T? = body(a)
    }

    private class NonFiniteNonClosedUnivariateFunctionImpl<I : M, O : M>(
        override val domain: Set<I>,
        override val codomain: Set<O>,
        val body: (I) -> O?,
    ) : Function.Univariate<I, O> {
        override fun invoke(a: I): O? = body(a)
    }

    private class NonFiniteClosedUnivariateFunctionImpl<T : M>(
        override val domainAndCodomains: Set<T>,
        val body: (T) -> T?,
    ) : Function.Univariate.Closed<T> {
        override fun invoke(a: T): T? = body(a)
    }

    companion object {
        fun <I : M, O : M> of(
            domain: Set<I>,
            codomain: Set<O>,
            body: (I) -> O?,
        ): Function.Univariate<I, O> =
            if (domain !is Set.Finite<I> || codomain !is Set.Finite<O>)
                NonFiniteNonClosedUnivariateFunctionImpl(domain, codomain, body)
            else
                FiniteNonClosedUnivariateFunctionImpl(domain, codomain, body)

        fun <T : M> of(
            domain: Set<T>,
            body: (T) -> T?,
        ): Function.Univariate.Closed<T> =
            if (domain !is Set.Finite<T>)
                NonFiniteClosedUnivariateFunctionImpl(domain, body)
            else
                FiniteClosedUnivariateFunctionImpl(domain, body)
    }
}