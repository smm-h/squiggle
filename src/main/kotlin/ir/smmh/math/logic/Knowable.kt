package ir.smmh.math.logic

import ir.smmh.math.MathematicalObject

sealed class Knowable : MathematicalObject.Abstract() {

    override fun isNonReferentiallyEqualTo(that: MathematicalObject) = Logical.False

    abstract fun toBoolean(): Boolean?
    abstract operator fun not(): Knowable
    abstract infix fun and(that: Knowable): Knowable
    abstract infix fun or(that: Knowable): Knowable
    abstract infix fun xor(that: Knowable): Knowable
    abstract infix fun imp(that: Knowable): Knowable

    object Unknown : Knowable() {
        override fun toBoolean() = null
        override val debugText = "Unknown"
        override fun not() = Unknown
        override fun and(that: Knowable) = if (that is Logical) that.and(this) else Unknown
        override fun or(that: Knowable) = if (that is Logical) that.or(this) else Unknown
        override fun xor(that: Knowable) = if (that is Logical) that.xor(this) else Unknown
        override fun imp(that: Knowable) = if (that is Logical.True) Logical.True else Unknown
    }

    companion object {
        fun of(boolean: Boolean?): Knowable =
            if (boolean == null) Unknown else Logical.of(boolean)
    }
}