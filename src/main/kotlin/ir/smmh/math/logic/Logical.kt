package ir.smmh.math.logic

sealed class Logical : Knowable() {
    abstract override fun toBoolean(): Boolean
    abstract override fun not(): Logical
    abstract infix fun and(that: Logical): Logical
    abstract infix fun or(that: Logical): Logical
    abstract infix fun xor(that: Logical): Logical
    abstract infix fun imp(that: Logical): Logical

    object True : Logical() {
        override fun toInt() = 1
        override fun toBoolean() = true
        override val debugText = "⊤"
        override fun not() = False
        override fun and(that: Logical) = that
        override fun and(that: Knowable) = if (that is Logical) and(that) else Unknown
        override fun or(that: Logical) = True
        override fun or(that: Knowable) = if (that is Logical) or(that) else True
        override fun xor(that: Logical) = if (that is True) True else False
        override fun xor(that: Knowable) = if (that is Logical) xor(that) else Unknown
        override fun imp(that: Logical) = that
        override fun imp(that: Knowable) = that
    }

    object False : Logical() {
        override fun toInt() = 0
        override fun toBoolean() = false
        override val debugText = "⊥"
        override fun not() = True
        override fun and(that: Logical) = False
        override fun and(that: Knowable) = if (that is Logical) and(that) else False
        override fun or(that: Logical) = that
        override fun or(that: Knowable) = if (that is Logical) or(that) else Unknown
        override fun xor(that: Logical) = if (that is False) True else False
        override fun xor(that: Knowable) = if (that is Logical) xor(that) else Unknown
        override fun imp(that: Logical) = True // vacuous truth
        override fun imp(that: Knowable) = True // vacuous truth
    }

    override fun negateIf(condition: Boolean) = if (condition) !this else this

    companion object {
        val Domain = BooleanDomain<Logical>(False, True)

        fun of(boolean: Boolean): Logical =
            if (boolean) True else False
    }

    object Structure : AbstractTwoElementBooleanAlgebra<Logical>(Domain) {
        override fun join(a: Logical, b: Logical) = a or b
        override fun meet(a: Logical, b: Logical) = a and b
        override fun complement(a: Logical) = !a
        override fun symmetricDifference(a: Logical, b: Logical) = a xor b
    }
}