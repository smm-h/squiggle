package ir.smmh.nile

import org.jetbrains.annotations.Contract

fun interface RecursivelySpecific<out T> {
    @Contract("->this")
    fun specificThis(): T
}