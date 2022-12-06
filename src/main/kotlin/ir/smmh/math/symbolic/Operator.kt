package ir.smmh.math.symbolic

class Operator(
    val name: String,
    val arity: IntRange,
    val render: (List<String>) -> String,
) {

    companion object {
        private val nullary = 0..0
        private val unary = 1..1
        private val binary = 2..2
        private val ternary = 3..3
        private val quadary = 4..4
    }

    fun interface Definitions {
        fun defineInto(table: Table): Table
    }

    fun interface Table {
        fun define(operator: Operator)

        fun arbitrary(name: String, arity: IntRange, render: (List<String>) -> String) =
            define(Operator(name, arity, render))

        fun nullary(name: String, render: String) =
            arbitrary(name, nullary) { render }

        fun unary(name: String, render: (String) -> String) =
            arbitrary(name, unary) { val (a) = it; render(a) }

        fun binary(name: String, render: (String, String) -> String) =
            arbitrary(name, binary) { val (a, b) = it; render(a, b) }

        fun ternary(name: String, render: (String, String, String) -> String) =
            arbitrary(name, ternary) { val (a, b, c) = it; render(a, b, c) }

        fun quadary(name: String, render: (String, String, String, String) -> String) =
            arbitrary(name, quadary) { val (a, b, c, d) = it; render(a, b, c, d) }

        fun unaryPrefix(name: String, symbol: String) =
            unary(name) { "$symbol{$it}" }

        fun unaryPostfix(name: String, symbol: String) =
            unary(name) { "{$it}$symbol" }

        fun binaryInfix(name: String, symbol: String) =
            binary(name) { a, b -> "{$a}$symbol{$b}" }

        fun quadaryLimited(name: String, symbol: String) = quadary(name) {
            variable, lowerLimit, upperLimit, function ->
            "${symbol}_{$variable = {$lowerLimit}}^{$upperLimit} {$function}"
        }
    }
}