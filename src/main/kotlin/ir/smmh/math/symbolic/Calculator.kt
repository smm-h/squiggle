package ir.smmh.math.symbolic

import ir.smmh.math.settheory.Set
import ir.smmh.math.symbolic.Operator.Binary.Infix.Companion.Equal

interface Calculator<T : Any> {

    val set: Set.Specific<T>

    fun calculate(expression: Expression, context: Context<T>): T

    fun equate(expression: Expression): Expression =
        Equal(expression, Expression.of(set, calculate(expression, Context.empty())))

    class Exception(message: String) : kotlin.Exception(message)
}