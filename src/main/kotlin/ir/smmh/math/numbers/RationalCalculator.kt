package ir.smmh.math.numbers

import ir.smmh.math.settheory.Set
import ir.smmh.math.settheory.UniversalNumberSets
import ir.smmh.math.symbolic.Calculator
import ir.smmh.math.symbolic.Context
import ir.smmh.math.symbolic.Expression
import ir.smmh.math.symbolic.Operator
import java.lang.Math.pow
import java.util.concurrent.atomic.AtomicReference

class RationalCalculator<F : Any>(
    override val fallback: Calculator<F, *>,
    override val downgrade: (Rational) -> F,
    override val upgrade: (F) -> Rational
) : Calculator<Rational, F> {

    override val type: Set.Specific<Rational> = UniversalNumberSets.RationalNumbers

    override fun convert(it: Any): Rational = when (it) {
        is Int -> Rational.of(it)
        is Number -> Rational.of(it.toDouble())
        is Rational -> it
        else -> throw Calculator.Exception("cannot convert: $it")
    }

    override fun calculateOperation(
        expression: Expression.Operation,
        context: Context<Rational>
    ): Rational {
        val operator = expression.operator
        return when (expression.arity) {
            1 -> {
                val a = calculate(expression[0], context)
                when (operator) {
                    "p" -> a
                    "pos" -> a
                    "neg" -> a.negate()
                    // "!" -> MathUtil.factorial(toInt(a)).toDouble()
                    "sin" -> Rational.of(Math.sin(a.approximate()))
                    "cos" -> Rational.of(Math.cos(a.approximate()))
                    "tan" -> Rational.of(Math.tan(a.approximate()))
                    "ln" -> Rational.of(Math.log(a.approximate()))
                    "sqrt" -> Rational.of(Math.sqrt(a.approximate()))
                    else -> null
                }
            }
            2 -> {
                val a = calculate(expression[0], context)
                val b = calculate(expression[1], context)
                when (operator) {
                    "+" -> a + b
                    "-" -> a - b
                    "*", "x" -> a * b
                    "/", "over" -> a / b
                    "^" -> a.power(b.approximate().toInt())
                    "sin" -> Rational.of(pow(Math.sin(a.approximate()), b.approximate()))
                    "cos" -> Rational.of(pow(Math.cos(a.approximate()), b.approximate()))
                    "tan" -> Rational.of(pow(Math.tan(a.approximate()), b.approximate()))
                    "log" -> Rational.of(Math.log(a.approximate()) / Math.log(b.approximate()))
                    "root" -> Rational.of(Math.pow(a.approximate(), 1 / b.approximate()))
                    // "mod" -> {}
                    else -> null
                }
            }
            else -> {
                when (operator) {
                    "sum" -> {
                        val v: String = (expression[0] as Expression.Variable).name
                        val a: Int = calculate(expression[1], context).approximate().toInt()
                        val b: Int = calculate(expression[2], context).approximate().toInt()
                        Rational.Mutable.of(Rational.ZERO) { s ->
                            val reference = AtomicReference<Rational>(null)
                            val c: Context<Rational> = Context.of(v, reference, context)
                            for (i in a..b) {
                                reference.set(Rational.of(i))
                                s += calculate(expression[3], c)
                            }
                        }
                    }
                    "prod" -> {
                        val v: String = (expression[0] as Expression.Variable).name
                        val a: Int = calculate(expression[1], context).approximate().toInt()
                        val b: Int = calculate(expression[2], context).approximate().toInt()
                        Rational.Mutable.of(Rational.ONE) { s ->
                            val reference = AtomicReference<Rational>(null)
                            val c: Context<Rational> = Context.of(v, reference, context)
                            for (i in a..b) {
                                reference.set(Rational.of(i))
                                s *= calculate(expression[3], c)
                            }
                        }
                    }
                    else -> null
                }
            }
        } ?: throw Calculator.Exception("undefined operation: $operator")
    }

    companion object {
//        val extended = ExtendedCalculator(this)

        val extendingInt = RationalCalculator<Int>(IntCalculator.extendingNothing, {
            val d = it.approximate()
            val i = d.toInt()
            if (i.toDouble() == d) i
            else throw Calculator.Exception("integer expected")
        }, { Rational.of(it) })
    }
}