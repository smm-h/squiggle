package ir.smmh.math.numbers

import ir.smmh.math.InfinitelyIterable
import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject
import ir.smmh.math.abstractalgebra.AbstractRingLikeStructure
import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical
import ir.smmh.math.numbers.Complex.Companion.i
import ir.smmh.math.settheory.AbstractSet
import kotlin.random.Random
import ir.smmh.math.settheory.Set.Infinite as InfiniteSet

/**
 * Sum of two [Real] numbers, one multiplied by [i]
 */
sealed interface Complex : Quaternion {
    val imaginaryPart: Numbers.Real

    override val coefficientOfI: Numbers.Real get() = imaginaryPart
    override val coefficientOfJ: Numbers.Real get() = Numbers.ZERO
    override val coefficientOfK: Numbers.Real get() = Numbers.ZERO

    override fun unaryPlus(): Complex = this
    override fun unaryMinus(): Complex = if (isReal()) -realPart else RR(-realPart, -imaginaryPart)

    override val absoluteSquared: Numbers.Real get() = realPart.squared + imaginaryPart.squared

    override fun isComplex() = true
    override fun asComplex() = this

    fun isReal(): Boolean = imaginaryPart == Numbers.ZERO
    fun asReal(): Numbers.Real? = if (isReal()) realPart else null

    override fun isNonReferentiallyEqualTo(that: MathematicalObject): Knowable =
        if (that is Complex) realPart.isEqualTo(that.realPart) and imaginaryPart.isEqualTo(that.imaginaryPart)
        else Logical.False

    override val debugText: String
        get() = "$realPart+$imaginaryPart·i"

    val angle: Numbers.Real
        get() = BuiltinNumberType.DoubleReal(
            Math.atan2(
                imaginaryPart.approximateAsFP64(),
                realPart.approximateAsFP64()
            )
        )

    operator fun plus(that: Complex): Complex {
        val a = this.asReal()
        val b = that.asReal()
        return if (a != null && b != null) a + b else RR(
            this.component1() + that.component1(),
            this.component2() + that.component2(),
        )
    }

    operator fun minus(that: Complex): Complex {
        val a = this.asReal()
        val b = that.asReal()
        return if (a != null && b != null) a - b else RR(
            this.component1() - that.component1(),
            this.component2() - that.component2(),
        )
    }

    val reciprocal: Complex
        get() = if (absoluteSquared == Numbers.ZERO) throw DivisionByZeroException()
        else RR(realPart / absoluteSquared, -imaginaryPart / absoluteSquared)

    operator fun times(that: Complex): Complex =
        RR(
            realPart * that.realPart - imaginaryPart * that.imaginaryPart,
            imaginaryPart * that.realPart + realPart * that.imaginaryPart
        )

    operator fun div(that: Complex): Complex = RR(
        (realPart * that.realPart - imaginaryPart * that.imaginaryPart) / that.absoluteSquared,
        (imaginaryPart * that.realPart - realPart * that.imaginaryPart) / that.absoluteSquared,
    )

    fun power(p: Int): Complex {
        var x = Numbers.ONE as Complex
        repeat(p) { x *= this }
        return x
    }

    class RR(
        override val realPart: Numbers.Real,
        override val imaginaryPart: Numbers.Real = Numbers.ZERO,
    ) : MathematicalObject.Abstract(), Complex {
        constructor(real: Int, imaginary: Int) :
                this(BuiltinNumberType.IntInteger(real), BuiltinNumberType.IntInteger(imaginary))

        constructor(real: Double, imaginary: Double) :
                this(BuiltinNumberType.DoubleReal(real), BuiltinNumberType.DoubleReal(imaginary))
    }

    companion object {
        val i = RR(Numbers.ZERO, Numbers.ONE)
    }

    class Set(val pickerSize: Double) : AbstractSet<Complex>(), InfiniteSet<Complex> {
        override val debugText = "ComplexNumbers"
        override fun isNonReferentiallyEqualTo(that: MathematicalObject) = Logical.False
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
                RR(x, y)
            }
        }

        override fun getPicker(random: Random) = MathematicalCollection.Picker<Complex> {
            RR(
                BuiltinNumberType.DoubleReal((Random.nextDouble() * 2 - 1) * pickerSize),
                BuiltinNumberType.DoubleReal((Random.nextDouble() * 2 - 1) * pickerSize),
            )
        }
    }

    class Field(override val domain: Set) : AbstractRingLikeStructure<Complex>() {
        override fun add(a: Complex, b: Complex): Complex = a + b
        override fun multiply(a: Complex, b: Complex): Complex = a * b
    }
}