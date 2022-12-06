package ir.smmh.math.symbolic

import ir.smmh.lingu.Token
import ir.smmh.math.numbers.Complex
import ir.smmh.math.numbers.Rational
import ir.smmh.math.settheory.Set
import ir.smmh.math.settheory.UniversalNumberSets

sealed interface Expression {
    val arity: Int
    operator fun get(index: Int): Expression

    /**
     * Value is unknown and depends on [Context]
     */
    class Variable(val name: String) : Expression {
        override val arity get() = 0
        override fun get(index: Int) =
            throw IndexOutOfBoundsException(index)
    }

    /**
     * Value is immutable and constant
     */
    class Constant(val set: Set, val value: Any) : Expression {
        override val arity get() = 0
        override fun get(index: Int) =
            throw IndexOutOfBoundsException(index)
    }

    class Operation(val operator: String, val values: List<Expression>) : Expression {
        constructor(operator: String, vararg values: Any): this(operator, values.map(Expression::of))
        override val arity = values.size
        override fun get(index: Int) = values[index]
    }

    companion object {

        fun of(it: Any): Expression = when (it) {
            is Expression -> it
            is String -> Variable(it)
            is Int -> of(it)
            is Long -> of(it)
            is Float -> of(it)
            is Double -> of(it)
            is Boolean -> of(it)
            is Rational -> of(it)
            is Complex -> of(it)
            is Token.Structure -> of(it)
            else -> throw Exception("unspecified type: $it")
        }

        fun of(it: Int): Constant =
            Constant(UniversalNumberSets.IntIntegers, it)

        fun of(it: Long): Constant =
            Constant(UniversalNumberSets.LongIntegers, it)

        fun of(it: Float): Constant =
            Constant(UniversalNumberSets.FloatRealNumbers, it)

        fun of(it: Double): Constant =
            Constant(UniversalNumberSets.DoubleRealNumbers, it)

        fun of(it: Boolean): Constant =
            Constant(UniversalNumberSets.Booleans, it)

        fun of(it: Rational): Constant =
            Constant(UniversalNumberSets.RationalNumbers, it)

        fun of(it: Complex): Constant =
            Constant(UniversalNumberSets.ComplexNumbers, it)

        fun of(structure: Token.Structure): Expression = when (structure) {
            is Token.Structure.Node -> {
                val list = structure.list
//                val arity = list.size - 1
                val operator = (list[0] as Token.Structure.Leaf).token.data
                Expression.Operation(operator, *list.subList(1, list.size).toTypedArray())
//                val operator = map[name]
//                if (operator == null)
//                    throw Exception("undefined operator: $name")
//                else
            }
            is Token.Structure.Leaf -> {
                val token = structure.token
                when (token.type.name) {
                    "id", "string" -> Expression.Variable(token.data)
                    "digits" -> Expression.of(token.data.toInt())
                    "digitsWithDot" -> Expression.of(token.data.toDouble())
                    else -> throw Exception("invalid token: $token")
                }
            }
        }
    }
}