package ir.smmh.math.symbolic

import ir.smmh.math.settheory.Set
import ir.smmh.math.symbolic.Operator.Binary.Companion.Equal

interface Calculator<T : Any> {

    val domain: Set.Specific<T>

    val convert: (Any) -> T

    fun calculate(expression: Expression, context: Context<T>): T = when (expression) {
        is Expression.Operation -> calculateOperation(expression, context)
        is Expression.Constant -> convert(expression.value)
        is Expression.Variable -> context[expression.name]
    }

    fun calculateOperation(expression: Expression.Operation, context: Context<T>): T

    fun equate(expression: Expression): Expression =
        Equal(expression, Expression.of(domain, calculate(expression, Context.empty())))

    class Exception(message: String) : kotlin.Exception(message)
}