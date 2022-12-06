package ir.smmh.math.symbolic

import ir.smmh.math.settheory.Set

interface Calculator<T : Any, F : Any> {

    val fallback: Calculator<F, *>
    val downgrade: (T) -> F
    val upgrade: (F) -> T

    val type: Set.Specific<out T>

    val baseContext: Context<T> get() = Context.empty()

    fun convert(it: Any): T

    fun isValid(it: T): Boolean = it in type

    fun calculate(expression: Expression, context: Context<T>): T = (when (expression) {
        is Expression.Operation -> calculateOperation(expression, context)
        is Expression.Constant -> convert(expression.value)
        is Expression.Variable -> context[expression.name]
    } ?: upgrade(fallback.calculate(expression, context.convert(downgrade))))
        .also { if (!isValid(it)) throw Exception("invalid expression: $it") }

    fun calculateOperation(expression: Expression.Operation, context: Context<T>): T?

    fun equate(expression: Expression): Expression =
        Expression.Operation("=", expression, Expression.Constant(type, calculate(expression, baseContext)))

    class Exception(message: String) : kotlin.Exception(message)
}