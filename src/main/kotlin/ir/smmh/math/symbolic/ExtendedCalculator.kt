package ir.smmh.math.symbolic

import ir.smmh.math.settheory.Set
import ir.smmh.math.settheory.StoredSet

class ExtendedCalculator<T : Any>(val source: Calculator<T>) : Calculator<Set.Specific.Finite<T>> {

    override val domain = Set.Specific.CountablyInfinite.Universal<Set.Specific.Finite<T>> {
        Set.Specific.Finite.Singleton(source.domain.choose())
    }

    override val convert: (Any) -> Set.Specific.Finite<T> = { value ->
        @Suppress("UNCHECKED_CAST")
        if (value is Set.Specific.Finite<*>) value as Set.Specific.Finite<T>
        else Set.Specific.Finite.Singleton(source.convert(value))
    }

    override fun calculateOperation(
        expression: Expression.Operation,
        context: Context<Set.Specific.Finite<T>>
    ): Set.Specific.Finite<T> {
        val operator = expression.operator
        val arguments = (0 until expression.arity).map { calculate(expression[it], context) }
        val output = HashSet<T>()
        val c = Context.empty<T>()
        val operators = ArrayList<Operator>()
        when (operator) {
            Operator.Unary.PlusMinus -> {
                operators.add(Operator.Unary.Plus)
                operators.add(Operator.Unary.Minus)
            }
            Operator.Binary.PlusMinus -> {
                operators.add(Operator.Binary.Plus)
                operators.add(Operator.Binary.Minus)
            }
            else -> operators.add(operator)
        }
        for (o in operators) when (o) {
            is Operator.Unary ->
                for (a in arguments[0].over)
                    output.add(source.calculate(Expression.combine(o, a), c))
            is Operator.Binary ->
                for (a in arguments[0].over)
                    for (b in arguments[1].over)
                        output.add(source.calculate(Expression.combine(o, a, b), c))
            else -> throw Calculator.Exception("undefined operation: $o")
        }
        return StoredSet(output)
    }
}