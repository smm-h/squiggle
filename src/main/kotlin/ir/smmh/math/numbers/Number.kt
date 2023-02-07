package ir.smmh.math.numbers

import ir.smmh.math.numbers.Numbers.ZERO
import ir.smmh.math.symbolic.Context
import ir.smmh.math.symbolic.Expression
import ir.smmh.math.symbolic.conventions.Conventions
import ir.smmh.math.MathematicalObject as M

interface Number : M.WellDefined {

    operator fun unaryPlus(): Number = this
    operator fun minus(that: Number): Number
    operator fun unaryMinus(): Number
    operator fun plus(that: Number): Number

    fun isQuaternion() = this is Quaternion
    fun asQuaternion() = if (this is Quaternion) this else null

    class Plus(
        parts: List<Expression<Number>>
    ) : Expression.Combination.Closed<Number>(parts) {

        override val debugText: String
            get() = parts.joinToString("+", "", "") { it.debugText }

        override fun generateTeX(conventions: Conventions): String =
            parts.joinToString("+", "{", "}") { it.generateTeX(conventions) }

        override fun evaluate(context: Context): Number =
            parts.fold(ZERO as Number) { x, e -> x + e.evaluate(context) }
    }

    class Minus(
        val e0: Expression<Number>,
        val e1: Expression<Number>,
    ) : Expression.Combination.Closed<Number>(listOf(e0, e1)) {

        override val debugText: String
            get() = "${e0.debugText}-${e1.debugText}"

        override fun generateTeX(conventions: Conventions): String =
            "{${e0.generateTeX(conventions)}-${e1.generateTeX(conventions)}}"

        override fun evaluate(context: Context): Number =
            e0.evaluate(context) - e1.evaluate(context)
    }
}