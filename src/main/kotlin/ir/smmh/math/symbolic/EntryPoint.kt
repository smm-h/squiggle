package ir.smmh.math.symbolic

import ir.smmh.math.settheory.UniversalNumberSets.IntIntegers
import ir.smmh.math.symbolic.Operator.Binary.Infix.Companion.Minus
import ir.smmh.math.symbolic.Operator.Binary.Infix.Companion.Over
import ir.smmh.math.symbolic.Operator.Binary.Infix.Companion.Plus
import ir.smmh.math.symbolic.Operator.Binary.Infix.Companion.Superscript
import ir.smmh.math.symbolic.Operator.Binary.Prefix.Companion.Cos
import ir.smmh.math.symbolic.Operator.Binary.Prefix.Companion.Sin
import ir.smmh.math.symbolic.Operator.Multiary.Companion.Sum

fun main() {
    val i = Expression.variable(IntIntegers, "i")
    RationalCalculator.equate(
//        Plus(Sin(2, Over(1, 2)), Cos(2, Over(1, 2)))
//        Minus(7, 4)
//        PlusMinus(3, 2)
//        Sum(i, 0, 10, Superscript(i, 2))
        Sum(i, 1, 10, Over(1, Superscript(2, i)))
    ).show(72)
}