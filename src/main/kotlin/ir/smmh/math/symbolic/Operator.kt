package ir.smmh.math.symbolic

sealed interface Operator {
    fun interface Unary : Operator {

        operator fun invoke(a: Any) =
            Expression.combine(this, Expression.of(a))

        fun render(a: String): String

        class Prefix(val symbol: String) : Unary {
            override fun render(a: String) = "$symbol{$a}"

            companion object {
                val Plus = Operator.Unary.Prefix("+")
                val Minus = Operator.Unary.Prefix("-")
                val PlusMinus = Operator.Unary.Prefix("\\pm")

                val Sin = Operator.Unary.Prefix("sin")
                val Cos = Operator.Unary.Prefix("cos")
                val Tan = Operator.Unary.Prefix("tan")
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

        operator fun invoke(a: Any, b: Any) =
            Expression.combine(this, Expression.of(a), Expression.of(b))

        fun render(a: String, b: String): String

        class Prefix(val symbol: String) : Binary {
            override fun render(a: String, b: String) = "$symbol{$a}{$b}"
        }

        class Infix(val symbol: String) : Binary {
            override fun render(a: String, b: String) = "{$a}$symbol{$b}"

            companion object {
                val Plus = Operator.Binary.Infix("+")
                val Minus = Operator.Binary.Infix("-")
                val PlusMinus = Operator.Binary.Infix("\\pm")
                val Cross = Operator.Binary.Infix("\\times")
                val Invisible = Operator.Binary.Infix("")
                val OverInline = Operator.Binary.Infix("\\div")
                val Over = Operator.Binary.Infix("\\over") // frac prefix?
                val Mod = Operator.Binary.Infix("mod")
                val Equal = Operator.Binary.Infix("=")
            }
        }

        class Postfix(val symbol: String) : Binary {
            override fun render(a: String, b: String) = "{$a}{$b}$symbol"
        }
    }

    fun interface Ternary : Operator {

        operator fun invoke(a: Any, b: Any, c: Any) =
            Expression.combine(this, Expression.of(a), Expression.of(b), Expression.of(c))

        fun render(a: String, b: String, c: String): String
    }

    fun interface Multiary : Operator {
        fun render(input: List<String>): String
    }

    /*
    arithmetic, modulo, factorial, exponent, logarithm
    vector/matrix (concat, scalar mul, ...)
    bitwise/boolean (and/or/xor/not/...)
    set (union, intersecton, in, subset)
    comparison
    forall/exists
    ^ _
    \frac \sqrt \sum \prod
    \neg \wedge \vee
    \pm \cdot
    \bar \dot \hat \tilde
    \in \notin \cup \cap
    \leq \neq \geq
    \supset \subset
    \circ \prime
    \{ \} \mid
     */
}