package ir.smmh.lisp

fun interface Callable {
    fun call(arguments: List<Value>): Value
}