package ir.smmh.math.numbers

import ir.smmh.mage.core.Utils.sqr
import kotlin.math.sqrt

class Complex(val r: Double, val i: Double) {
    constructor(value: Int) : this(value.toDouble())
    constructor(value: Double) : this(value, 0.0)
    constructor(real: Int, imaginary: Int) : this(real.toDouble(), imaginary.toDouble())

    val magnitudeSquared: Double by lazy { sqr(r) + sqr(i) }
    val magnitude: Double by lazy { sqrt(magnitudeSquared) }
    val reciprocal: Complex by lazy { Complex(r / magnitudeSquared, -i / magnitudeSquared) }

    fun negate() =
        Complex(-r, i)

    fun add(other: Complex) = Complex(
        r + other.r,
        i + other.i,
    )

    fun subtract(other: Complex) = Complex(
        r - other.r,
        i - other.i,
    )

    fun multiply(other: Complex) = Complex(
        r * other.r - i * other.i,
        i * other.r + r * other.i,
    )

    fun divide(other: Complex) = Complex(
        (r * other.r - i * other.i) / other.magnitudeSquared,
        (i * other.r - r * other.i) / other.magnitudeSquared,
    )

    override fun toString() = "$r+$i·i"

    companion object {
        val ZERO = Complex(0)
        val ONE = Complex(1)
    }
}
