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

    val reciprocal: Rational? by lazy {
        if (numerator == 0.0) null else Rational(denominator, numerator, precise)
    }

    fun negate(): Rational = Rational(-numerator, denominator, precise)

    fun approximate(): Double = numerator.toDouble() / denominator

    override fun toString() =
        if (precise) "${numerator.toInt()}${if (denominator == 1.0) "" else "/${denominator.toInt()}"}"
        else approximate().toString()

    override val tex: String =
        if (precise) "{${numerator.toInt()}}${if (denominator == 1.0) "" else "\\over{${denominator.toInt()}}"}"
        else approximate().toString()

    fun add(that: Rational) = Rational(
        numerator * that.denominator + that.numerator * denominator,
        denominator * that.denominator,
        precise && that.precise
    )

    fun subtract(that: Rational) = Rational(
        numerator * that.denominator - that.numerator * denominator,
        denominator * that.denominator,
        precise && that.precise
    )

    fun multiply(that: Rational) = Rational(
        numerator * that.numerator,
        denominator * that.denominator,
        precise && that.precise
    )

    fun divide(that: Rational) = Rational(
        numerator * that.denominator,
        that.numerator * denominator,
        precise && that.precise
    )

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
}
