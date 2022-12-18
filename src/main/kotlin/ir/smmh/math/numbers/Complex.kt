package ir.smmh.math.numbers

import ir.smmh.mage.core.Utils.sqr
import kotlin.math.sqrt

class Complex(val r: Double, val i: Double) {
    constructor(value: Int) : this(value.toDouble())
    constructor(value: Double) : this(value, 0.0)
    constructor(real: Int, imaginary: Int) : this(real.toDouble(), imaginary.toDouble())

    operator fun component1() = r
    operator fun component2() = i

    operator fun plus(that: Int) = Complex(r + that, i)
    operator fun minus(that: Int) = Complex(r - that, i)
    operator fun times(that: Int) = Complex(r * that, i * that)
    operator fun div(that: Int) = Complex(r / that, i / that)

    operator fun plus(that: Double) = Complex(r + that, i)
    operator fun minus(that: Double) = Complex(r - that, i)
    operator fun times(that: Double) = Complex(r * that, i * that)
    operator fun div(that: Double) = Complex(r / that, i / that)

    operator fun plus(that: Complex) = add(that)
    operator fun minus(that: Complex) = subtract(that)
    operator fun times(that: Complex) = multiply(that)
    operator fun div(that: Complex) = divide(that)

    val magnitudeSquared: Double by lazy { sqr(r) + sqr(i) }
    val magnitude: Double by lazy { sqrt(magnitudeSquared) }
    val angle: Double by lazy { Math.atan2(i, r) }

    fun add(other: Complex) = Complex(
        r + other.r,
        i + other.i,
    )

    fun negate() =
        Complex(-r, i)

    fun subtract(other: Complex) = Complex(
        r - other.r,
        i - other.i,
    )

    fun multiply(other: Complex) = Complex(
        r * other.r - i * other.i,
        i * other.r + r * other.i,
    )

    val reciprocal: Complex? by lazy {
        if (magnitudeSquared == 0.0) null else Complex(r / magnitudeSquared, -i / magnitudeSquared)
    }

    fun divide(other: Complex) = Complex(
        (r * other.r - i * other.i) / other.magnitudeSquared,
        (i * other.r - r * other.i) / other.magnitudeSquared,
    )

    override fun toString() = "$r+$i·i"

    fun power(p: Int): Complex {
        var x = ONE
        repeat(p) { x = x.multiply(this) }
        return x
    }

    companion object {
        val ZERO = Complex(0)
        val ONE = Complex(1)
        val i = Complex(0, 1)
    }
}
