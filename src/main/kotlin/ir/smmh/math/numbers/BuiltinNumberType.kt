package ir.smmh.math.numbers

import ir.smmh.math.numbers.Numbers.Integer
import ir.smmh.math.numbers.Numbers.Natural
import ir.smmh.math.numbers.Numbers.ONE
import ir.smmh.math.numbers.Numbers.Real

sealed interface BuiltinNumberType : Real {

    @JvmInline
    value class IntNatural(val value: Int) : Natural, BuiltinNumberType

    @JvmInline
    value class LongNatural(val value: Long) : Natural, BuiltinNumberType

    @JvmInline
    value class IntInteger(val value: Int) : Integer, BuiltinNumberType {
        override val squareRoot get() = DoubleReal(Math.sqrt(value.toDouble()))
        override val reciprocal get() = Numbers.Rational.RR(ONE, this)
    }

    @JvmInline
    value class LongInteger(val value: Long) : Integer, BuiltinNumberType

    @JvmInline
    value class FloatReal(val value: Float) : Real, BuiltinNumberType

    @JvmInline
    value class DoubleReal(val value: Double) : Real, BuiltinNumberType
}