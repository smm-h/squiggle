package ir.smmh.math.symbolic

import ir.smmh.math.MathematicalObject
import ir.smmh.math.symbolic.Expression.*
import ir.smmh.math.symbolic.conventions.Conventions
import ir.smmh.math.MathematicalObject as M

/**
 * An [Expression] is a hierarchy of symbols including [Variable]s, [Value]s,
 * and their [Combination]s.
 *
 * - Semantically, an expression can be [evaluate]d to a [MathematicalObject]
 * of a certain [type], when provided with a [Context].
 * - Syntactically, it can [generateTeX] to produce a string that can be
 * rendered with a TeX engine, when provided with certain [Conventions].
 *
 * [Wikipedia](https://en.wikipedia.org/wiki/Expression_(mathematics))
 */
sealed class Expression<out T : M> {

    abstract val debugText: String
    abstract fun generateTeX(conventions: Conventions = Conventions.Defaults): String
    abstract fun evaluate(context: Context = Context.empty): T

    override fun toString() = debugText
    override fun hashCode() = debugText.hashCode()

    class Value<T : M>(val value: T) : Expression<T>() {
        override fun equals(other: Any?) = other is Value<*> && value == other.value
        override val debugText get() = value.debugText
        override fun generateTeX(conventions: Conventions) = value.tex
        override fun evaluate(context: Context): T = value
    }

    class UnexpectedVariableTypeException(name: String) :
        EvaluationException("unexpected variable type: $name")

    class Variable<T : M>(val name: String) : Expression<T>() {
        override fun equals(other: Any?) = other is Variable<*> && name == other.name
        override fun generateTeX(conventions: Conventions) = name
        override val debugText: String get() = "Variable($name)"
        override fun evaluate(context: Context): T = try {
            @Suppress("UNCHECKED_CAST")
            context[name] as T
        } catch (_: ClassCastException) {
            throw UnexpectedVariableTypeException(name)
        }
    }

    abstract class Combination<T : M>(val parts: List<Expression<*>>) : Expression<T>() {
        // TODO arity
        abstract class Closed<T : M>(val parts: List<Expression<T>>) : Expression<T>()
    }
}