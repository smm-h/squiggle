package ir.smmh.math.symbolic

import java.util.concurrent.atomic.AtomicReference

fun interface Context<T : Any> {
    operator fun get(name: String): T

    fun <F : Any> convert(convert: (T) -> F) =
        Context<F> { convert(this[it]) }

    companion object {

        fun unknownVariable(name: String): Nothing = throw Calculator.Exception("unknown variable $name")

        fun <T : Any> empty() = Context<T>(::unknownVariable)

        fun <T : Any> of(variable: Expression.Variable, value: T, fallback: Context<T>) =
            of(variable.name, value, fallback)

        fun <T : Any> of(name: String, value: T, fallback: Context<T>) =
            Context { if (it == name) value else fallback[name] }

        fun <T : Any> of(name: String, value: AtomicReference<T>, fallback: Context<T>) =
            Context { if (it == name) value.get() else fallback[name] }
    }
}