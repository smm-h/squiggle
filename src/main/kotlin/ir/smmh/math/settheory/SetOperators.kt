package ir.smmh.math.settheory

import ir.smmh.math.symbolic.TeXer

object SetOperators {
    fun define(texer: TeXer) = texer.apply {
        defineNullaryOperator("nullset", "\\emptyset")
        defineNullaryOperator("universalset", "U")
        defineUnaryOperator("complement") { "{$it}^\\complement" } // { "\\overline{$it}" } //
        defineBinaryOperator("union") { a, b -> "$a \\cup $b" }
        defineBinaryOperator("intersection") { a, b -> "$a \\cap $b" }
        defineBinaryOperator("setdifference") { a, b -> "$a / $b" } // try -
        defineBinaryOperator("issubsetof") { a, b -> "$a \\subset $b" } // also supset
        defineBinaryOperator("ismemberof") { a, b -> "$a \\in $b" } // also notin
    }
}