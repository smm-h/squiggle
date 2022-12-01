package ir.smmh.math.symbolic

import ir.smmh.math.settheory.UniversalNumberSets.IntIntegers
import ir.smmh.math.symbolic.Operator.Binary.Companion.Cos
import ir.smmh.math.symbolic.Operator.Binary.Companion.Root
import ir.smmh.math.symbolic.Operator.Binary.Companion.Sin
import ir.smmh.math.symbolic.Operator.Binary.Infix.Companion.Minus
import ir.smmh.math.symbolic.Operator.Binary.Infix.Companion.Over
import ir.smmh.math.symbolic.Operator.Binary.Infix.Companion.Plus
import ir.smmh.math.symbolic.Operator.Binary.Infix.Companion.PlusMinus
import ir.smmh.math.symbolic.Operator.Binary.Infix.Companion.Superscript
import ir.smmh.math.symbolic.Operator.Multiary.Companion.Sum

fun main() {

    val e1 = Plus(Sin(2, Over(1, 2)), Cos(2, Over(1, 2)))

    val e2 = Minus(7, 4)

    val e3 = PlusMinus(3, 2)

    val i = Expression.variable(IntIntegers, "i")

    val e4 = Sum(i, 0, 10, Superscript(i, 2))

    val e5 = Root(Over(1, Minus(1, Sum(i, 1, 10, Over(1, Superscript(2, i))))), 10)

    RationalCalculator.equate(e5).show(72)
}