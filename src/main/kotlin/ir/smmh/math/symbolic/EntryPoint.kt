package ir.smmh.math.symbolic

import ir.smmh.math.settheory.Sets.Integer32
import ir.smmh.math.symbolic.Operator.Binary.Infix.Companion.Superscript
import ir.smmh.math.symbolic.Operator.Multiary.Companion.Sum

fun main() {
    // RealCalculator.express(Minus(7, 4)).show(48)
    val i = Expression.variable(Integer32, "i")
    RationalCalculator.equate(
        Expression.combine(
            Sum,
            i,
            Expression.of(0),
            Expression.of(10),
            Superscript(i, Expression.of(2))
        )
    ).show(72)
}