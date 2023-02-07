package ir.smmh.math.symbolic

import java.util.concurrent.atomic.AtomicReference
import ir.smmh.math.MathematicalObject as M

fun interface Context {
    operator fun get(name: String): M

    class UnknownVariableException(name: String) :
        EvaluationException("unknown variable: $name")

    companion object {

        val empty = Context { throw UnknownVariableException(it) }

//        fun of(variable: Expression.Variable, value: M, fallback: Context) =
//            of(variable.name, value, fallback)

        fun of(name: String, value: M, fallback: Context) =
            Context { if (it == name) value else fallback[name] }

        fun of(name: String, reference: AtomicReference<M>, fallback: Context) =
            Context { if (it == name) reference.get() else fallback[name] }
    }
}