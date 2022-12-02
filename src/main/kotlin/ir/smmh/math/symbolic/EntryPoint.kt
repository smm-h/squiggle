package ir.smmh.math.symbolic

import ir.smmh.math.settheory.UniversalNumberSets.IntIntegers
import ir.smmh.math.symbolic.Operator.Binary.Companion.Cos
import ir.smmh.math.symbolic.Operator.Binary.Companion.Cross
import ir.smmh.math.symbolic.Operator.Binary.Companion.Minus
import ir.smmh.math.symbolic.Operator.Binary.Companion.Over
import ir.smmh.math.symbolic.Operator.Binary.Companion.Plus
import ir.smmh.math.symbolic.Operator.Binary.Companion.PlusMinus
import ir.smmh.math.symbolic.Operator.Binary.Companion.Root
import ir.smmh.math.symbolic.Operator.Binary.Companion.Sin
import ir.smmh.math.symbolic.Operator.Binary.Companion.Superscript
import ir.smmh.math.symbolic.Operator.Multiary.Companion.Sum

fun main() {

    val e1 = Minus(7, 4)
    val e2 = Plus(Sin(2, Over(1, 2)), Cos(2, Over(1, 2)))

    val i = Expression.variable(IntIntegers, "i")
    val e3 = Sum(i, 0, 10, Superscript(i, 2))
    val e4 = Root(Over(1, Minus(1, Sum(i, 1, 10, Over(1, Superscript(2, i))))), 10)

    // RationalCalculator.equate(e3).show(72)

    val e5 = Cross(PlusMinus(3, 2), PlusMinus(3, 4))
    RationalCalculator.extended.equate(e5).show(72)
}