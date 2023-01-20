package ir.smmh.math.logic

import ir.smmh.math.MathematicalObject

sealed class Knowable : MathematicalObject.Abstract() {

    override fun isNonReferentiallyEqualTo(that: MathematicalObject) = Known.False

    sealed class Known : Knowable() {
        abstract override fun toBoolean(): Boolean
        abstract override fun not(): Known
        abstract infix fun and(that: Known): Known
        abstract infix fun or(that: Known): Known
        abstract infix fun xor(that: Known): Known
        abstract infix fun imp(that: Known): Known

        object True : Known() {
            override fun toBoolean() = true
            override val debugText = "True"
            override fun not() = False
            override fun and(that: Known) = that
            override fun and(that: Knowable) = if (that is Known) and(that) else Unknown
            override fun or(that: Known) = True
            override fun or(that: Knowable) = if (that is Known) or(that) else True
            override fun xor(that: Known) = if (that is True) True else False
            override fun xor(that: Knowable) = if (that is Known) xor(that) else Unknown
            override fun imp(that: Known) = that
            override fun imp(that: Knowable) = that
        }

        object False : Known() {
            override fun toBoolean() = false
            override val debugText = "False"
            override fun not() = True
            override fun and(that: Known) = False
            override fun and(that: Knowable) = if (that is Known) and(that) else False
            override fun or(that: Known) = that
            override fun or(that: Knowable) = if (that is Known) or(that) else Unknown
            override fun xor(that: Known) = if (that is False) True else False
            override fun xor(that: Knowable) = if (that is Known) xor(that) else Unknown
            override fun imp(that: Known) = True // vacuous truth
            override fun imp(that: Knowable) = True // vacuous truth
        }

        companion object {
            fun of(boolean: Boolean): Known =
                if (boolean) True else False
        }
    }

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
        override fun and(that: Knowable) = if (that is Known) that.and(this) else Unknown
        override fun or(that: Knowable) = if (that is Known) that.or(this) else Unknown
        override fun xor(that: Knowable) = if (that is Known) that.xor(this) else Unknown
        override fun imp(that: Knowable) = if (that is Known.True) Known.True else Unknown
    }

    companion object {
        fun of(boolean: Boolean?): Knowable =
            if (boolean == null) Unknown else Known.of(boolean)
    }
}