package ir.smmh.nile

import org.jetbrains.annotations.Contract

// TODO get rid of this
fun interface RecursivelySpecific<out T> {
    @Contract("->this")
    fun specificThis(): T
}