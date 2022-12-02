package ir.smmh.math.symbolic

import ir.smmh.math.numbers.Rational
import ir.smmh.math.settheory.Set
import ir.smmh.math.settheory.UniversalNumberSets
import java.lang.Math.pow
import java.util.concurrent.atomic.AtomicReference

object RationalCalculator : Calculator<Rational> {

    override val domain: Set.Specific<Rational> = UniversalNumberSets.RationalNumbers

    override val convert: (Any) -> Rational = { value ->
        when (value) {
            is Int -> Rational.of(value)
            is Number -> Rational.of(value.toDouble())
            is Rational -> value
            else -> throw Calculator.Exception("cannot convert: $value")
        }
    }

    override fun calculateOperation(expression: Expression.Operation, context: Context<Rational>): Rational {
        val operator = expression.operator
        return when (operator) {
            is Operator.Unary -> {
                val a = calculate(expression[0], context)
                when (operator) {
                    Operator.Unary.Plus -> a
                    Operator.Unary.Minus -> a.negate()
                    // Operator.Unary.Postfix.Exclamation -> MathUtil.factorial(toInt(a)).toDouble()
                    Operator.Unary.Sin -> Rational.of(Math.sin(a.approximate()))
                    Operator.Unary.Cos -> Rational.of(Math.cos(a.approximate()))
                    Operator.Unary.Tan -> Rational.of(Math.tan(a.approximate()))
                    Operator.Unary.Ln -> Rational.of(Math.log(a.approximate()))
                    Operator.Unary.Root -> Rational.of(Math.sqrt(a.approximate()))
                    else -> null
                }
            }
                is Operator.Binary -> {
                    val a = calculate(expression[0], context)
                    val b = calculate(expression[1], context)
                    when (operator) {
                        Operator.Binary.Plus -> a + b
                        Operator.Binary.Minus -> a - b
                        Operator.Binary.Cross, Operator.Binary.Invisible -> a * b
                        Operator.Binary.OverInline, Operator.Binary.Over -> a / b
                        Operator.Binary.Superscript -> a.power(b.approximate().toInt())
                        Operator.Binary.Sin -> Rational.of(pow(Math.sin(a.approximate()), b.approximate()))
                        Operator.Binary.Cos -> Rational.of(pow(Math.cos(a.approximate()), b.approximate()))
                        Operator.Binary.Tan -> Rational.of(pow(Math.tan(a.approximate()), b.approximate()))
                        Operator.Binary.Log -> Rational.of(Math.log(a.approximate()) / Math.log(b.approximate()))
                        Operator.Binary.Root -> Rational.of(Math.pow(a.approximate(), 1 / b.approximate()))
                        // Operator.Binary.Mod -> {}
                        else -> null
                    }
                }
                Operator.Multiary.Sum -> {
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
                Operator.Multiary.Prod -> {
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
        } ?: throw Calculator.Exception("undefined operation: $operator")
    }

    private fun toInt(it: Rational): Int {
        val d = it.approximate()
        val i = d.toInt()
        if (i.toDouble() == d) return i else throw Calculator.Exception("integer expected")
    }

    val extended = ExtendedCalculator(this)
}