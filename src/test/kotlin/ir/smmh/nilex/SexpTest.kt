package ir.smmh.nilex

import ir.smmh.lingu.Code
import ir.smmh.lingu.Token

fun main() {
    println(Sexp.code("a(b(c(d \"e\" f g10 20 30 40h ())))").beConstructedInto<Token.Structure>())
}