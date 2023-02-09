package ir.smmh.math.numbers

import ir.smmh.math.InfinitelyIterable
import ir.smmh.math.MathematicalCollection
import ir.smmh.math.logic.Logical
import ir.smmh.math.numbers.Complex.Companion.i
import ir.smmh.math.numbers.Numbers.Integer
import ir.smmh.math.numbers.Numbers.ONE
import ir.smmh.math.numbers.Numbers.Real
import ir.smmh.math.numbers.Numbers.ZERO
import ir.smmh.math.settheory.AbstractSet
import kotlin.random.Random
import ir.smmh.math.MathematicalObject as M
import ir.smmh.math.settheory.Set.Infinite as InfiniteSet

/**
 * Sum of two [Real] numbers, one multiplied by [i]
 */
sealed interface Complex : Quaternion {
    val imaginaryPart: Real

    override val coefficientOfI: Real get() = imaginaryPart
    override val coefficientOfJ: Real get() = ZERO
    override val coefficientOfK: Real get() = ZERO

    override val absoluteSquared: Real get() = realPart.squared + imaginaryPart.squared

    override fun isComplex() = true
    override fun asComplex() = this

    fun isReal(): Boolean = this is Real || (imaginaryPart isEqualTo ZERO).toBoolean()
    fun asReal(): Real? =
        if (this is Real) this
        else if (isReal()) realPart
        else null

    override fun isNonReferentiallyEqualTo(that: M) =
        if (that is Complex) realPart.isEqualTo(that.realPart) and imaginaryPart.isEqualTo(that.imaginaryPart)
        else Logical.False

    override val debugText: String get() = "${realPart.debugText}+${imaginaryPart.debugText}·i"
    override val tex: String get() = "${realPart.tex}+${imaginaryPart.tex}\\cdot i"

    val angle: Real get() = Real.of(Math.atan2(imaginaryPart.approximateAsDouble(), realPart.approximateAsDouble()))

    override fun unaryPlus(): Complex = this
    operator fun minus(that: Complex): Complex = this + (-that)
    operator fun div(that: Complex): Complex = this * that.reciprocal

    override fun unaryMinus(): Complex = of(-realPart, -imaginaryPart)

    operator fun plus(that: Complex): Complex = of(
        this.component1() + that.component1(),
        this.component2() + that.component2(),
    )

    val reciprocal: Complex
        get() =
            if ((absoluteSquared isEqualTo ZERO).toBoolean())
                throw DivisionByZeroException()
            else
                of(realPart / absoluteSquared, -imaginaryPart / absoluteSquared)

    operator fun times(that: Complex): Complex = of(
        realPart * that.realPart - imaginaryPart * that.imaginaryPart,
        imaginaryPart * that.realPart + realPart * that.imaginaryPart
    )

    // TODO exponent
    fun power(p: Int): Complex {
        var x = ONE as Complex
        repeat(p) { x *= this }
        return x
    }

    private class ComplexImpl(
        override val realPart: Real,
        override val imaginaryPart: Real = ZERO,
    ) : M.Abstract(), Complex

    companion object {
        val i = of(ZERO, ONE)

        fun of(realPart: Int, imaginaryPart: Int): Complex =
            if (imaginaryPart == 0) Integer.of(realPart) else of(Integer.of(realPart), Integer.of(imaginaryPart))

        fun of(realPart: Double, imaginaryPart: Double): Complex =
            if (imaginaryPart == 0.0) Real.of(realPart) else of(Real.of(realPart), Real.of(imaginaryPart))

        fun of(realPart: Real, imaginaryPart: Real): Complex =
            if ((imaginaryPart isEqualTo ZERO).toBoolean()) realPart else ComplexImpl(realPart, imaginaryPart)
    }

    class Set(val pickerSize: Double) : AbstractSet<Complex>(), InfiniteSet<Complex> {
        override val debugText = "ComplexNumbers"
        override val tex = "{\\mathbb{C}}"
        override fun isNonReferentiallyEqualTo(that: M) = Logical.False
        override fun contains(it: Complex) = Logical.True

        // 1+4\sum_{i=0}^{n-1}{i}
        override val overElements = InfinitelyIterable<Complex> {
            var x = -1
            var y = -1
            var stage = 3
            InfinitelyIterable.Iterator {
                when (stage) {
                    //@formatter:off
                    0 -> { x--; y++; if (x == 0) stage++ }
                    1 -> { x--; y--; if (y == 0) stage++ }
                    2 -> { x++; y--; if (x == 0) stage++ }
                    3 -> { x++; y++; if (y == 0) stage++ }
                    4 -> { x++; stage = 0 }
                    //@formatter:on
                }
                of(x, y)
            }
        }

        override fun getPicker(random: Random) = MathematicalCollection.Picker<Complex> {
            of(
                (Random.nextDouble() * 2 - 1) * pickerSize,
                (Random.nextDouble() * 2 - 1) * pickerSize,
            )
        }
    }
}