package ir.smmh.math.symbolic

import java.util.concurrent.atomic.AtomicReference

fun interface Context<T : Any> {
    operator fun get(name: String): T

    companion object {
        private fun unknownVariable(name: String): Nothing = throw Calculator.Exception("unknown variable $name")
        fun <T : Any> empty() = Context<T>(::unknownVariable)
        fun <T : Any> of(name: String, value: T, fallback: Context<T>) = Context {
            if (it == name) value else fallback[name]
        }
        fun <T : Any> of(name: String, value: AtomicReference<T>, fallback: Context<T>) = Context {
            if (it == name) value.get() else fallback[name]
        }
    }
}