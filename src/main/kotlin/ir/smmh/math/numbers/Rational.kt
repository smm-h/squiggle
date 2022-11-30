package ir.smmh.math.numbers

import ir.smmh.math.symbolic.TeXable
import ir.smmh.util.MathUtil

class Rational private constructor(
    val numerator: Double,
    val denominator: Double,
    val precise: Boolean,
) : TeXable {

    init {
        require(denominator != 0.0) { "denominator cannot be zero" }
    }

    override fun toString() =
        if (precise) "${numerator.toInt()}${if (denominator == 1.0) "" else "/${denominator.toInt()}"}"
        else approximate().toString()

    override val tex: String =
        if (precise) "{${numerator.toInt()}}${if (denominator == 1.0) "" else "\\over{${denominator.toInt()}}"}"
        else approximate().toString()

    fun approximate(): Double = numerator.toDouble() / denominator

    operator fun plus(that: Int) = Rational(numerator + that * denominator, denominator, precise)
    operator fun minus(that: Int) = Rational(numerator - that * denominator, denominator, precise)
    operator fun times(that: Int) = Rational(numerator * that, denominator, precise)
    operator fun div(that: Int) = Rational(numerator, that * denominator, precise)

    operator fun plus(that: Double) = Rational(numerator + that * denominator, denominator, false)
    operator fun minus(that: Double) = Rational(numerator - that * denominator, denominator, false)
    operator fun times(that: Double) = Rational(numerator * that, denominator, false)
    operator fun div(that: Double) = Rational(numerator, that * denominator, false)

    operator fun plus(that: Rational): Rational = add(that)
    operator fun minus(that: Rational): Rational = subtract(that)
    operator fun times(that: Rational): Rational = multiply(that)
    operator fun div(that: Rational): Rational = divide(that)

    fun add(that: Rational): Rational = Rational(
        numerator * that.denominator + that.numerator * denominator,
        denominator * that.denominator,
        precise && that.precise
    )

    fun negate(): Rational = Rational(-numerator, denominator, precise)

    fun subtract(that: Rational): Rational = Rational(
        numerator * that.denominator - that.numerator * denominator,
        denominator * that.denominator,
        precise && that.precise
    )

    fun multiply(that: Rational): Rational = Rational(
        numerator * that.numerator,
        denominator * that.denominator,
        precise && that.precise
    )

    val reciprocal: Rational? by lazy {
        if (numerator == 0.0) null else Rational(denominator, numerator, precise)
    }

    fun divide(that: Rational): Rational = Rational(
        numerator * that.denominator,
        that.numerator * denominator,
        precise && that.precise
    )

    fun power(p: Int): Rational =
        Mutable.of(ONE) { m -> repeat(p) { m *= this } }

    companion object {
        val ZERO = Rational.of(0)
        val ONE = Rational.of(1)

        fun of(value: Int) = of(value, 1)
        fun of(numerator: Int, denominator: Int): Rational {
            val gcd = MathUtil.gcd(numerator, denominator)
            return Rational((numerator / gcd).toDouble(), (denominator / gcd).toDouble(), true)
        }

        fun of(value: Double): Rational = of(value, 1.0)
        fun of(numerator: Double, denominator: Double): Rational {
            return Rational(numerator, denominator, false)
        }
    }

    class Mutable private constructor(
        var numerator: Double,
        var denominator: Double,
        var precise: Boolean,
    ) {
        fun toRational(): Rational =
            Rational(numerator, denominator, precise)

        operator fun plusAssign(that: Int) {
            numerator + that * denominator
        }

        operator fun minusAssign(that: Int) {
            numerator - that * denominator
        }

        operator fun timesAssign(that: Int) {
            numerator *= that
        }

        operator fun divAssign(that: Int) {
            denominator *= that
        }

        operator fun plusAssign(that: Double) {
            numerator + that * denominator; precise = false
        }

        operator fun minusAssign(that: Double) {
            numerator - that * denominator; precise = false
        }

        operator fun timesAssign(that: Double) {
            numerator *= that; precise = false
        }

        operator fun divAssign(that: Double) {
            denominator *= that; precise = false
        }

        operator fun plusAssign(that: Rational) {
            numerator = numerator * that.denominator + that.numerator * denominator
            denominator *= that.denominator
            precise = precise && that.precise
        }

        operator fun minusAssign(that: Rational) {
            numerator = numerator * that.denominator - that.numerator * denominator
            denominator *= that.denominator
            precise = precise && that.precise
        }

        operator fun timesAssign(that: Rational) {
            numerator *= that.numerator
            denominator *= that.denominator
            precise = precise && that.precise
        }

        operator fun divAssign(that: Rational) {
            numerator *= that.denominator
            denominator *= that.numerator
            precise = precise && that.precise
        }

        override fun toString() = "$numerator/$denominator"

        companion object {
            fun of(
                initial: Rational,
                changes: (Mutable) -> Unit
            ): Rational {
                val m = Mutable(initial.numerator, initial.denominator, initial.precise)
                changes(m)
                return m.toRational()
            }
        }
    }
}
