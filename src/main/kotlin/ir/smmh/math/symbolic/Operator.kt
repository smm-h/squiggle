package ir.smmh.math.symbolic

sealed interface Operator {

    fun interface Unary : Operator {
        operator fun invoke(a: Any) = Expression.combine(this, a)
        fun render(a: String): String

        class Prefix(val symbol: String) : Unary {
            override fun render(a: String) = "$symbol{$a}"

            companion object {
                val Plus = Operator.Unary.Prefix("+")
                val Minus = Operator.Unary.Prefix("-")
                val PlusMinus = Operator.Unary.Prefix("\\pm")
                val Sin = Operator.Unary.Prefix("\\sin")
                val Cos = Operator.Unary.Prefix("\\cos")
                val Tan = Operator.Unary.Prefix("\\tan")
                val Ln = Operator.Unary.Prefix("\\ln_")
                val Root = Operator.Unary.Prefix("\\sqrt")
            }
        }

        class Postfix(val symbol: String) : Unary {
            override fun render(a: String) = "{$a}$symbol"

            companion object {
                val Exclamation = Operator.Unary.Postfix("!")
            }
        }
    }

    fun interface Binary : Operator {
        operator fun invoke(a: Any, b: Any) = Expression.combine(this, a, b)
        fun render(a: String, b: String): String

        class Infix(val symbol: String) : Binary {
            override fun render(a: String, b: String) = "{$a}$symbol{$b}"

            companion object {
                val Plus = Infix("+")
                val Minus = Infix("-")
                val PlusMinus = Infix("\\pm")
                val Cross = Infix("\\times")
                val Invisible = Infix("")
                val OverInline = Infix("\\div")
                val Over = Infix("\\over") // frac prefix?
                val Mod = Infix("mod")
                val Equal = Infix("=")
                val Superscript = Infix("^")
                val Subscript = Infix("_")
            }
        }

        companion object {
            val Sin = Binary { x, p -> "\\sin^{$p}{$x}" }
            val Cos = Binary { x, p -> "\\cos^{$p}{$x}" }
            val Tan = Binary { x, p -> "\\tan^{$p}{$x}" }
            val Log = Binary { x, p -> "\\log_{$p}{$x}" }
            val Root = Binary { x, p -> "\\sqrt[$p]{$x}" }
        }
    }

    fun interface Ternary : Operator {
        operator fun invoke(a: Any, b: Any, c: Any) = Expression.combine(this, a, b, c)
        fun render(a: String, b: String, c: String): String
    }

    fun interface Multiary : Operator {
        operator fun invoke(vararg arguments: Any) = Expression.combine(this, *arguments)
        fun render(input: List<String>): String

        companion object {
            val Sum = Operator.Multiary {
                val (variable, lowerLimit, upperLimit, function) = it
                "\\sum_{$variable = {$lowerLimit}}^{$upperLimit} {$function}"
            }
            val Prod = Operator.Multiary {
                val (variable, lowerLimit, upperLimit, function) = it
                "\\sum_{$variable = {$lowerLimit}}^{$upperLimit} {$function}"
            }
        }
    }
}