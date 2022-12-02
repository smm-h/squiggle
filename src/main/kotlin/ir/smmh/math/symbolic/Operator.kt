package ir.smmh.math.symbolic

abstract class Operator(val name: String) {

    override fun toString() = name

    class Unary(name: String, val render: (String) -> String) : Operator(name) {
        operator fun invoke(a: Any) = Expression.combine(this, a)

        companion object {
            fun prefix(name: String, symbol: String) = Unary(name) { "$symbol{$it}" }
            val Plus = prefix("Plus", "+")
            val Minus = prefix("Minus", "-")
            val PlusMinus = prefix("PlusMinus", "\\pm")
            val Sin = prefix("Sin", "\\sin")
            val Cos = prefix("Cos", "\\cos")
            val Tan = prefix("Tan", "\\tan")
            val Ln = prefix("Ln", "\\ln_")
            val Root = prefix("Root", "\\sqrt")

            fun postfix(name: String, symbol: String) = Unary(name) { "{$it}$symbol" }
            val Exclamation = postfix("Exclamation", "!")
        }
    }

    class Binary(name: String, val render: (String, String) -> String) : Operator(name) {
        operator fun invoke(a: Any, b: Any) = Expression.combine(this, a, b)

        companion object {
            fun infix(name: String, symbol: String) = Binary(name) { a, b -> "{$a}$symbol{$b}" }
            val Plus = infix("Plus", "+")
            val Minus = infix("Minus", "-")
            val PlusMinus = infix("PlusMinus", "\\pm")
            val Cross = infix("Cross", "\\times")
            val Invisible = infix("Invisible", "")
            val OverInline = infix("OverInline", "\\div")
            val Over = infix("Over", "\\over") // TODO frac?
            val Mod = infix("Mod", "mod")
            val Equal = infix("Equal", "=")
            val Superscript = infix("Superscript", "^")
            val Subscript = infix("Subscript", "_")

            val Sin = Binary("Sin") { x, p -> "\\sin^{$p}{$x}" }
            val Cos = Binary("Cos") { x, p -> "\\cos^{$p}{$x}" }
            val Tan = Binary("Tan") { x, p -> "\\tan^{$p}{$x}" }
            val Log = Binary("Log") { x, p -> "\\log_{$p}{$x}" }
            val Root = Binary("Root") { x, p -> "\\sqrt[$p]{$x}" }
        }
    }

    class Multiary(name: String, val render: (List<String>) -> String) : Operator(name) {
        operator fun invoke(vararg arguments: Any) = Expression.combine(this, *arguments)

        companion object {
            val Sum = Multiary("Sum") {
                val (variable, lowerLimit, upperLimit, function) = it
                "\\sum_{$variable = {$lowerLimit}}^{$upperLimit} {$function}"
            }
            val Prod = Multiary("Prod") {
                val (variable, lowerLimit, upperLimit, function) = it
                "\\prod_{$variable = {$lowerLimit}}^{$upperLimit} {$function}"
            }
        }
    }
}