package ir.smmh.math.numbers

import ir.smmh.util.MathUtil

class Rational(numerator: Int, denominator: Int) {
    constructor(value: Int) : this(value, 1)

    val numerator: Int
    val denominator: Int

    init {
        require(denominator != 0) { "denominator cannot be zero" }
        val gcd: Int = MathUtil.gcd(numerator, denominator)
        this.numerator = numerator / gcd
        this.denominator = denominator / gcd
    }

    fun approximate(): Double = numerator.toDouble() / denominator
    val reciprocal: Rational? by lazy { if (numerator == 0) null else Rational(denominator, numerator) }
    fun negate(): Rational = Rational(-numerator, denominator)

    fun add(other: Rational) =
        Rational(numerator * other.denominator + other.numerator * denominator, denominator * other.denominator)

    fun subtract(other: Rational) =
        Rational(numerator * other.denominator - other.numerator * denominator, denominator * other.denominator)

    fun multiply(other: Rational): Rational =
        Rational(numerator * other.numerator, denominator * other.denominator)

    fun divide(other: Rational): Rational =
        Rational(numerator * other.denominator, other.numerator * denominator)

    override fun toString(): String {
        // return approximate().toString();
        return "${numerator}/${denominator}"
    }

    companion object {
        val ZERO = Rational(0)
        val ONE = Rational(1)
    }
}