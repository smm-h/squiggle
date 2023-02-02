package ir.smmh.math.numbers

import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical
import ir.smmh.math.numbers.Numbers.ZERO

/**
 * Sum of four [Real] numbers, three multiplied by [i], [j], and [k]
 */
sealed interface Quaternion : MathematicalObject {

    val realPart: Numbers.Real
    val coefficientOfI: Numbers.Real
    val coefficientOfJ: Numbers.Real
    val coefficientOfK: Numbers.Real

    operator fun component1(): Numbers.Real = realPart
    operator fun component2(): Numbers.Real = coefficientOfI
    operator fun component3(): Numbers.Real = coefficientOfJ
    operator fun component4(): Numbers.Real = coefficientOfK

    operator fun unaryPlus(): Quaternion = this
    operator fun unaryMinus(): Quaternion
    operator fun plus(that: Quaternion): Quaternion {
        val a = this.asComplex()
        val b = that.asComplex()
        return if (a != null && b != null) a + b else Quaternion.RRRR(
            this.component1() + that.component1(),
            this.component2() + that.component2(),
            this.component3() + that.component3(),
            this.component4() + that.component4(),
        )
    }

    operator fun minus(that: Quaternion): Quaternion {
        val a = this.asComplex()
        val b = that.asComplex()
        return if (a != null && b != null) a - b else Quaternion.RRRR(
            this.component1() - that.component1(),
            this.component2() - that.component2(),
            this.component3() - that.component3(),
            this.component4() - that.component4(),
        )
    }

    fun isComplex(): Boolean = coefficientOfJ == ZERO && coefficientOfK == ZERO
    fun asComplex(): Complex? = if (isComplex()) coefficientOfI else null

    fun isQuaternion() = true
    fun asQuaternion() = this

    val absoluteSquared: Numbers.Real get() = realPart.squared + coefficientOfI.squared + coefficientOfJ.squared + coefficientOfK.squared
    val absolute: Numbers.Real get() = absoluteSquared.squareRoot.asReal()!!

    abstract class Abstract : Quaternion, MathematicalObject.Abstract() {
        override fun isNonReferentiallyEqualTo(that: MathematicalObject): Knowable = if (that is Quaternion
            && that.realPart == realPart
            && that.coefficientOfI == coefficientOfI
            && that.coefficientOfJ == coefficientOfJ
            && that.coefficientOfK == coefficientOfK
        ) Logical.True else Knowable.Unknown

        override val debugText by lazy { "$realPart+$coefficientOfI·i+$coefficientOfJ·j+$coefficientOfK·k" }
        override fun hashCode() =
            realPart.hashCode() xor coefficientOfI.hashCode() xor coefficientOfJ.hashCode() xor coefficientOfK.hashCode()
    }

    class CC(
        val complexPart: Complex = ZERO,
        val complexCoefficientOfJ: Complex = ZERO,
    ) : Abstract() {
        override val realPart: Numbers.Real by complexPart::realPart
        override val coefficientOfI: Numbers.Real by complexPart::imaginaryPart
        override val coefficientOfJ: Numbers.Real by complexCoefficientOfJ::realPart
        override val coefficientOfK: Numbers.Real by complexCoefficientOfJ::imaginaryPart

        override fun unaryMinus(): Quaternion =
            if (isComplex()) -complexPart else
                Quaternion.CC(-complexPart, -complexCoefficientOfJ)
    }

    class CRR(
        val complexPart: Complex = ZERO,
        override val coefficientOfJ: Numbers.Real = ZERO,
        override val coefficientOfK: Numbers.Real = ZERO,
    ) : Abstract() {
        override val realPart: Numbers.Real by complexPart::realPart
        override val coefficientOfI: Numbers.Real by complexPart::imaginaryPart

        override fun unaryMinus(): Quaternion =
            if (isComplex()) -complexPart else
                Quaternion.CRR(-complexPart, -coefficientOfJ, -coefficientOfK)
    }

    class RRRR(
        override val realPart: Numbers.Real = ZERO,
        override val coefficientOfI: Numbers.Real = ZERO,
        override val coefficientOfJ: Numbers.Real = ZERO,
        override val coefficientOfK: Numbers.Real = ZERO,
    ) : Abstract() {

        override fun unaryMinus(): Quaternion =
            if (isComplex()) -Complex.RR(realPart, coefficientOfI) else
                Quaternion.RRRR(-realPart, -coefficientOfI, -coefficientOfJ, -coefficientOfK)
    }
}