package ir.smmh.math.symbolic

import ir.smmh.math.numbers.Complex
import ir.smmh.math.numbers.Rational
import ir.smmh.math.settheory.Set
import ir.smmh.math.settheory.UniversalNumberSets

sealed interface Expression : TeXable {
    val arity: Int
    operator fun get(index: Int): Expression

    sealed interface Constant : Expression {
        override val arity get() = 0
        val set: Set
        val value: Any
    }

    sealed interface Operation : Expression {
        val operator: Operator
    }

    sealed interface Variable : Expression {
        override val arity get() = 0
        val set: Set
        val name: String
    }

    /**
     * Value is unknown and depends on [Context]
     */
    private class VariableNullary(
        override val set: Set,
        override val name: String,
        override val tex: String = name,
    ) : Variable {
        override fun get(index: Int) =
            throw IndexOutOfBoundsException(index)
    }

    /**
     * Value is immutable and constant
     */
    private class ConstantNullary(
        override val set: Set,
        override val value: Any,
        override val tex: String = TeXable.texOf(value),
    ) : Constant {
        override fun get(index: Int) =
            throw IndexOutOfBoundsException(index)
    }

    private class Unary(
        override val operator: Operator.Unary,
        val value: Expression,
    ) : Operation {
        override val arity = 1
        override fun get(index: Int) = if (index == 0) value else
            throw IndexOutOfBoundsException(index)

        override val tex: String by lazy {
            operator.render(this[0].tex)
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

        override val tex: String by lazy {
            operator.render(this[0].tex, this[1].tex)
        }
    }

    private class Multiary(
        override val operator: Operator.Multiary,
        val values: List<Expression>,
    ) : Operation {
        override val arity = values.size
        override fun get(index: Int) = values[index]

        override val tex: String by lazy {
            operator.render((0 until arity).map { i -> this[i].tex })
        }
    }

    companion object {

        fun variable(set: Set, name: String): Expression =
            VariableNullary(set, name)

        fun variable(set: Set, name: String, tex: String): Expression =
            VariableNullary(set, name, tex)

        fun of(set: Set, it: Any): Expression =
            ConstantNullary(set, it)

        fun of(set: Set, it: Any, tex: String): Expression =
            ConstantNullary(set, it, tex)

        fun of(it: Any): Expression = when (it) {
            is Expression -> it
            // is String -> variable()
            is Int -> of(it)
            is Long -> of(it)
            is Float -> of(it)
            is Double -> of(it)
            is Boolean -> of(it)
            is Rational -> of(it)
            is Complex -> of(it)
            else -> throw Exception("unspecified type: $it")
        }

        fun of(it: Int): Expression = ConstantNullary(UniversalNumberSets.IntIntegers, it)
        fun of(it: Long): Expression = ConstantNullary(UniversalNumberSets.LongIntegers, it)
        fun of(it: Float): Expression = ConstantNullary(UniversalNumberSets.FloatRealNumbers, it)
        fun of(it: Double): Expression = ConstantNullary(UniversalNumberSets.DoubleRealNumbers, it)
        fun of(it: Boolean): Expression = ConstantNullary(UniversalNumberSets.Booleans, it)
        fun of(it: Rational): Expression = ConstantNullary(UniversalNumberSets.RationalNumbers, it)
        fun of(it: Complex): Expression = ConstantNullary(UniversalNumberSets.ComplexNumbers, it)

        fun combine(operator: Operator.Unary, a: Any): Expression =
            Unary(operator, Expression.of(a))

        fun combine(operator: Operator.Binary, a: Any, b: Any): Expression =
            Binary(operator, Expression.of(a), Expression.of(b))

        fun combine(operator: Operator.Multiary, vararg arguments: Any): Expression =
            Multiary(operator, arguments.map(Expression::of))
    }
}