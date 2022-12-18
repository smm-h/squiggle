package ir.smmh.math.symbolic

import ir.smmh.math.matrix.Matrix
import ir.smmh.math.numbers.Complex
import ir.smmh.math.numbers.Rational
import ir.smmh.math.settheory.CardinalNumber
import ir.smmh.math.settheory.Set
import ir.smmh.math.settheory.UniversalNumberSets

class TeXer {
    fun texOf(it: Any): String {
        it.apply {
            return when (this) {
                is Expression -> when (this) {
                    is Expression.Operation -> find(this).invoke(values.map(::texOf))
                    is Expression.Constant -> texOf(value)
                    is Expression.Variable -> name
                }
                is Matrix<*> ->
                    (0 until rows).joinToString(" \\\\\n", "\\begin{bmatrix}\n", "\n\\end{bmatrix}")
                    { row(it).joinToString(" & ") }
                is Rational ->
                    if (precise) "{${numerator.toInt()}}${if (denominator == 1.0) "" else "\\over{${denominator.toInt()}}"}"
                    else texOf(approximate())
                is Complex -> "${texOf(r)} + ${texOf(i)} \\cdot i"
                UniversalNumberSets.Booleans -> "\\mathbb{B}"
                UniversalNumberSets.IntIntegers -> "\\mathbb{I}"
                UniversalNumberSets.DoubleRealNumbers -> "\\mathbb{R}"
                UniversalNumberSets.RationalNumbers -> "\\mathbb{Q}"
                UniversalNumberSets.ComplexNumbers -> "\\mathbb{C}"
                is Set.Finite.NonEmpty -> over.joinToString(", ", "\\{", "\\}", transform = ::texOf)
                is Set.Empty -> "\\emptyset"
                is CardinalNumber -> when (this) {
                    CardinalNumber.alephNought -> "\\aleph_0"
                    CardinalNumber.cardinalityOfTheContinuum -> "\\mathfrak{c}"
                    CardinalNumber.absoluteInfinite -> "\\Omega"
                }
                else -> toString()
            }
        }
    }

    private sealed class Operator {
        class Single(val arities: Arities, val render: (List<String>) -> String) : Operator()

        class Multiple : Operator() {
            private val set: MutableSet<Single> = HashSet()
            fun add(it: Single) = apply { set.add(it) }
            fun find(arity: Int): ((List<String>) -> String)? =
                set.filter { arity in it.arities }.run {
                    when (size) {
                        0 -> null
                        1 -> first().render
                        else -> sortedBy { it.arities.size }.first().render
                    }
                }
        }
    }

    private fun find(operation: Expression.Operation) =
        find(operation.operator, operation.arity)

    private fun find(name: String, arity: Int): (List<String>) -> String {
        return when (val it = map[name]) {
            null -> throw Exception("no such operator: $arity-ary $name")
            is Operator.Single -> it.render
            is Operator.Multiple -> it.find(arity) ?: throw Exception("unsupported arity for operator $name: $arity")
        }
    }

    private val map: MutableMap<String, Operator> = HashMap()

    fun arbitrary(name: String, arities: Arities, render: (List<String>) -> String) {
        val single = Operator.Single(arities, render)
        when (val it = map[name]) {
            null -> map[name] = single
            is Operator.Single -> map[name] = Operator.Multiple().add(it).add(single)
            is Operator.Multiple -> it.add(single)
        }
    }

    fun nullary(name: String, render: String) =
        arbitrary(name, Arities.nullary) { render }

    fun unary(name: String, render: (String) -> String) =
        arbitrary(name, Arities.unary) { val (a) = it; render(a) }

    fun binary(name: String, render: (String, String) -> String) =
        arbitrary(name, Arities.binary) { val (a, b) = it; render(a, b) }

    fun ternary(name: String, render: (String, String, String) -> String) =
        arbitrary(name, Arities.ternary) { val (a, b, c) = it; render(a, b, c) }

    fun quadary(name: String, render: (String, String, String, String) -> String) =
        arbitrary(name, Arities.quadary) { val (a, b, c, d) = it; render(a, b, c, d) }

    fun unaryPrefix(name: String, symbol: String) =
        unary(name) { "$symbol{$it}" }

    fun unaryPostfix(name: String, symbol: String) =
        unary(name) { "{$it}$symbol" }

    fun binaryInfix(name: String, symbol: String) =
        binary(name) { a, b -> "{$a}$symbol{$b}" }

    fun quadaryLimited(name: String, symbol: String) = quadary(name) { variable, lowerLimit, upperLimit, function ->
        "${symbol}_{$variable = {$lowerLimit}}^{$upperLimit} {$function}"
    }
}