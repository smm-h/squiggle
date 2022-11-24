package ir.smmh.math.symbolic

import ir.smmh.math.numbers.Rational
import ir.smmh.math.symbolic.Expression.Calculator.CalculationException

object RealCalculator : Expression.Calculator<Rational> {
    override fun calculate(expression: Expression): Rational {
        return if (expression is Expression.Operation) {
            val operator = expression.operator
            when (operator) {
                is Operator.Unary -> {
                    val a = calculate(expression[0])
                    return when (operator) {
                        // Operator.Unary.Prefix.PlusMinus
                        Operator.Unary.Prefix.Plus -> a
                        Operator.Unary.Prefix.Minus -> a.negate()
                        // unaryPostfixExclamation -> MathUtil.factorial(toInt(a)).toDouble()
                        Operator.Unary.Prefix.Sin -> Rational.of(Math.sin(a.approximate()))
                        Operator.Unary.Prefix.Cos -> Rational.of(Math.cos(a.approximate()))
                        Operator.Unary.Prefix.Tan -> Rational.of(Math.tan(a.approximate()))
                        else -> throw CalculationException("undefined operation")
                    }
                }
                is Operator.Binary -> {
                    val a = calculate(expression[0])
                    val b = calculate(expression[1])
                    when (operator) {
                        // Operator.Binary.Infix.PlusMinus
                        Operator.Binary.Infix.Plus -> a.add(b)
                        Operator.Binary.Infix.Minus -> a.subtract(b)
                        Operator.Binary.Infix.Cross, Operator.Binary.Infix.Invisible -> a.multiply(b)
                        Operator.Binary.Infix.OverInline, Operator.Binary.Infix.Over -> a.divide(b)
                        // Operator.Binary.Infix.Mod -> {}
                        else -> throw CalculationException("undefined operation")
                    }
                }
                else -> throw CalculationException("undefined operation")
            }
        } else if (expression is Expression.Constant<*>) {
            // val set = expression.set
            val value = expression.value
            return when (value) {
                is Int -> Rational.of(value)
                is Number -> Rational.of(value.toDouble())
                is Rational -> value
                else -> throw CalculationException("not a real number")
            }
        } else {
            throw CalculationException("invalid expression")
        }
    }

    private fun toInt(it: Rational): Int {
        val d = it.approximate()
        val i = d.toInt()
        if (i.toDouble() == d) return i else throw CalculationException("integer expected")
    }
}