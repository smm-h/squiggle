package ir.smmh.math.settheory

import ir.smmh.math.symbolic.TeXer

object SetOperators {
    fun define(texer: TeXer) = texer.apply {
        nullary("nullset", "\\emptyset")
        nullary("universalset", "U")
        unary("complement") { "{$it}^\\complement" } // { "\\overline{$it}" } //
        binary("union") { a, b -> "$a \\cup $b" }
        binary("intersection") { a, b -> "$a \\cap $b" }
        binary("setdifference") { a, b -> "$a - $b" } // TODO try /
        binary("issubsetof") { a, b -> "$a \\subset $b" } // also supset
        binary("ismemberof") { a, b -> "$a \\in $b" } // also notin
    }
}