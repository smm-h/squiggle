package ir.smmh.math.numbers

import ir.smmh.math.logic.Logical
import ir.smmh.math.numbers.Numbers.Real
import ir.smmh.math.numbers.Numbers.ZERO
import ir.smmh.math.MathematicalObject as M

/**
 * Sum of four [Real] numbers, three multiplied by [i], [j], and [k]
 */
sealed interface Quaternion : Number {

    val realPart: Real
    val coefficientOfI: Real
    val coefficientOfJ: Real
    val coefficientOfK: Real

    operator fun component1(): Real = realPart
    operator fun component2(): Real = coefficientOfI
    operator fun component3(): Real = coefficientOfJ
    operator fun component4(): Real = coefficientOfK

    override fun unaryPlus(): Quaternion = this
    override fun unaryMinus(): Quaternion = Quaternion.of(
        -realPart,
        -coefficientOfI,
        -coefficientOfJ,
        -coefficientOfK
    )

    override fun minus(that: Number) = this - that.asQuaternion()!!
    override fun plus(that: Number) = this + that.asQuaternion()!!

    operator fun minus(that: Quaternion): Quaternion = this + (-that)
    operator fun plus(that: Quaternion) = Quaternion.of(
        this.component1() + that.component1(),
        this.component2() + that.component2(),
        this.component3() + that.component3(),
        this.component4() + that.component4(),
    )

    fun isComplex(): Boolean =
        this is Complex || ((coefficientOfJ isEqualTo ZERO) and (coefficientOfK isEqualTo ZERO)).toBoolean()

    fun asComplex(): Complex? =
        if (this is Complex) this
        else if (isComplex()) coefficientOfI
        else null

    override fun isQuaternion() = true
    override fun asQuaternion() = this

    val absoluteSquared: Real get() = realPart.squared + coefficientOfI.squared + coefficientOfJ.squared + coefficientOfK.squared
    val absolute: Real get() = absoluteSquared.squareRoot.asReal()!!

    private class QuaternionImpl(
        override val realPart: Real = ZERO,
        override val coefficientOfI: Real = ZERO,
        override val coefficientOfJ: Real = ZERO,
        override val coefficientOfK: Real = ZERO,
    ) : Quaternion, M.Abstract() {
        override fun isNonReferentiallyEqualTo(that: M) = Logical.of(
            that is Quaternion
                    && that.realPart == realPart
                    && that.coefficientOfI == coefficientOfI
                    && that.coefficientOfJ == coefficientOfJ
                    && that.coefficientOfK == coefficientOfK
        )

        override val debugText by lazy { "${realPart.debugText}+${coefficientOfI.debugText}·i+${coefficientOfJ.debugText}·j+${coefficientOfK.debugText}·k" }
        override val tex by lazy { "{${realPart.tex}+${coefficientOfI.tex}\\cdot i+${coefficientOfJ.tex}\\cdot j+${coefficientOfK.tex}\\cdot k}" }
        override fun hashCode() =
            realPart.hashCode() xor coefficientOfI.hashCode() xor coefficientOfJ.hashCode() xor coefficientOfK.hashCode()
    }

    companion object {
        fun of(
            realPart: Real,
            coefficientOfI: Real,
            coefficientOfJ: Real,
            coefficientOfK: Real,
        ): Quaternion =
            if (((coefficientOfJ isEqualTo ZERO) and (coefficientOfK isEqualTo ZERO)).toBoolean())
                Complex.of(realPart, coefficientOfI)
            else
                QuaternionImpl(realPart, coefficientOfI, coefficientOfJ, coefficientOfK)
    }
}