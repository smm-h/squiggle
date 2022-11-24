package ir.smmh.math.symbolic

import ir.smmh.math.numbers.Complex
import ir.smmh.math.numbers.Rational
import ir.smmh.math.settheory.Set
import ir.smmh.math.settheory.Sets
import ir.smmh.math.symbolic.Operator.Binary.Infix.Companion.Equal

sealed interface Expression : TeXable {
    val arity: Int
    operator fun get(index: Int): Expression

    sealed interface Constant<T> : Expression {
        val set: Set<T>
        val value: T
    }

    sealed interface Operation : Expression {
        val operator: Operator
    }

    private class Nullary<T>(
        override val set: Set<T>,
        override val value: T,
    ) : Constant<T> {
        override val arity = 0
        override fun get(index: Int) =
            throw IndexOutOfBoundsException(index)

        override val render: String by lazy {
            val it = value
            if (it is TeXable) it.render else it.toString()
        }
    }

    private class Unary(
        override val operator: Operator.Unary,
        val value: Expression,
    ) : Operation {
        override val arity = 1
        override fun get(index: Int) = if (index == 0) value else
            throw IndexOutOfBoundsException(index)

        override val render: String by lazy {
            operator.render(this[0].render)
        }
    }

    private class Binary(
        override val operator: Operator.Binary,
        val lhs: Expression,
        val rhs: Expression,
    ) : Operation {
        override val arity = 2
        override fun get(index: Int) = if (index == 0) lhs else if (index == 1) rhs else
            throw IndexOutOfBoundsException(index)

        override val render: String by lazy {
            operator.render(this[0].render, this[1].render)
        }
    }

    private class Ternary(
        override val operator: Operator.Ternary,
        val a: Expression,
        val b: Expression,
        val c: Expression,
    ) : Operation {
        override val arity = 3
        override fun get(index: Int) = if (index == 0) a else if (index == 1) b else if (index == 2) c else
            throw IndexOutOfBoundsException(index)

        override val render: String by lazy {
            operator.render(this[0].render, this[1].render, this[2].render)
        }
    }

    private class Multiary(
        override val operator: Operator.Multiary,
        vararg val values: Expression,
    ) : Operation {
        override val arity = values.size
        override fun get(index: Int) = values[index]

        override val render: String by lazy {
            operator.render((0 until arity).map { i -> this[i].render })
        }
    }

    companion object {

        fun of(it: Any?): Expression = Nullary(Sets.U, it)

        fun of(it: Int): Expression = Nullary(Sets.Integer32, it)
        fun of(it: Long): Expression = Nullary(Sets.Integer64, it)
        fun of(it: Float): Expression = Nullary(Sets.RealFP, it)
        fun of(it: Double): Expression = Nullary(Sets.RealDP, it)
        fun of(it: Boolean): Expression = Nullary(Sets.Boolean, it)
        fun of(it: Rational): Expression = Nullary(Sets.RationalNumbers, it)
        fun of(it: Complex): Expression = Nullary(Sets.ComplexNumbers, it)

        fun <T> of(set: Set<T>, it: T): Expression =
            Nullary(set, it)

        fun combine(operator: Operator.Unary, argument: Expression): Expression =
            Unary(operator, argument)

        fun combine(operator: Operator.Binary, lhs: Expression, rhs: Expression): Expression =
            Binary(operator, lhs, rhs)

        fun combine(operator: Operator.Ternary, a: Expression, b: Expression, c: Expression): Expression =
            Ternary(operator, a, b, c)

        fun combine(operator: Operator.Multiary, vararg arguments: Expression): Expression =
            Multiary(operator, *arguments)
    }

    fun interface Calculator<T> {
        fun calculate(expression: Expression): T
        fun express(expression: Expression): Expression = Equal(expression, of(calculate(expression)))
        class CalculationException(message: String) : Exception(message)
    }
}