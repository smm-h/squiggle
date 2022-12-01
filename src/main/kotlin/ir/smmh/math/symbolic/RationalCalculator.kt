package ir.smmh.math.symbolic

import ir.smmh.math.numbers.Rational
import ir.smmh.math.settheory.Set
import ir.smmh.math.settheory.UniversalNumberSets
import ir.smmh.math.symbolic.Calculator.Exception
import java.lang.Math.pow
import java.util.concurrent.atomic.AtomicReference

object RationalCalculator : Calculator<Rational> {

    override val set: Set.Specific<Rational> = UniversalNumberSets.RationalNumbers

    override fun calculate(expression: Expression, context: Context<Rational>): Rational {
        return if (expression is Expression.Operation) {
            val operator = expression.operator
            when (operator) {
                is Operator.Unary -> {
                    val a = calculate(expression[0], context)
                    return when (operator) {
                        // Operator.Unary.Prefix.PlusMinus
                        Operator.Unary.Prefix.Plus -> a
                        Operator.Unary.Prefix.Minus -> a.negate()
                        // unaryPostfixExclamation -> MathUtil.factorial(toInt(a)).toDouble()
                        Operator.Unary.Prefix.Sin -> Rational.of(Math.sin(a.approximate()))
                        Operator.Unary.Prefix.Cos -> Rational.of(Math.cos(a.approximate()))
                        Operator.Unary.Prefix.Tan -> Rational.of(Math.tan(a.approximate()))
                        Operator.Unary.Prefix.Ln -> Rational.of(Math.log(a.approximate()))
                        Operator.Unary.Prefix.Root -> Rational.of(Math.sqrt(a.approximate()))
                        else -> throw Exception("undefined operation")
                    }
                }
                is Operator.Binary -> {
                    val a = calculate(expression[0], context)
                    val b = calculate(expression[1], context)
                    when (operator) {
                        // Operator.Binary.Infix.PlusMinus
                        Operator.Binary.Infix.Plus -> a + b
                        Operator.Binary.Infix.Minus -> a - b
                        Operator.Binary.Infix.Cross, Operator.Binary.Infix.Invisible -> a * b
                        Operator.Binary.Infix.OverInline, Operator.Binary.Infix.Over -> a / b
                        Operator.Binary.Infix.Superscript -> a.power(b.approximate().toInt())
                        Operator.Binary.Sin -> Rational.of(pow(Math.sin(a.approximate()), b.approximate()))
                        Operator.Binary.Cos -> Rational.of(pow(Math.cos(a.approximate()), b.approximate()))
                        Operator.Binary.Tan -> Rational.of(pow(Math.tan(a.approximate()), b.approximate()))
                        Operator.Binary.Log -> Rational.of(Math.log(a.approximate()) / Math.log(b.approximate()))
                        Operator.Binary.Root -> Rational.of(Math.pow(a.approximate(), 1 / b.approximate()))
                        // Operator.Binary.Infix.Mod -> {}
                        else -> throw Exception("undefined operation")
                    }
                }
                Operator.Multiary.Sum -> {
                    val v: String = (expression[0] as Expression.Variable).name
                    val a: Int = RationalCalculator.calculate(expression[1], context).approximate().toInt()
                    val b: Int = RationalCalculator.calculate(expression[2], context).approximate().toInt()
                    Rational.Mutable.of(Rational.ZERO) { s ->
                        val reference = AtomicReference<Rational>(null)
                        val c: Context<Rational> = Context.of(v, reference, context)
                        for (i in a..b) {
                            reference.set(Rational.of(i))
                            s += RationalCalculator.calculate(expression[3], c)
                        }
                    }
                }
                Operator.Multiary.Prod -> {
                    val v: String = (expression[0] as Expression.Variable).name
                    val a: Int = RationalCalculator.calculate(expression[1], context).approximate().toInt()
                    val b: Int = RationalCalculator.calculate(expression[2], context).approximate().toInt()
                    Rational.Mutable.of(Rational.ONE) { s ->
                        val reference = AtomicReference<Rational>(null)
                        val c: Context<Rational> = Context.of(v, reference, context)
                        for (i in a..b) {
                            reference.set(Rational.of(i))
                            s *= RationalCalculator.calculate(expression[3], c)
                        }
                    }
                }
                else -> throw Exception("undefined operation")
            }
        } else if (expression is Expression.Constant) {
            // val set = expression.set
            val value = expression.value
            return when (value) {
                is Int -> Rational.of(value)
                is Number -> Rational.of(value.toDouble())
                is Rational -> value
                else -> throw Exception("not a rational number: $value")
            }
        } else if (expression is Expression.Variable) {
            context[expression.name]
        } else {
            throw Exception("invalid expression")
        }
    }

    private fun toInt(it: Rational): Int {
        val d = it.approximate()
        val i = d.toInt()
        if (i.toDouble() == d) return i else throw Exception("integer expected")
    }
}