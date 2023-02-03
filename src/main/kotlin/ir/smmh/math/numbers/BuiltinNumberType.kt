package ir.smmh.math.numbers

import ir.smmh.math.numbers.Numbers.Integer
import ir.smmh.math.numbers.Numbers.Natural
import ir.smmh.math.numbers.Numbers.ONE
import ir.smmh.math.numbers.Numbers.Real
import ir.smmh.util.MathUtil

sealed interface BuiltinNumberType : Real {

    @JvmInline
    value class IntNatural(val value: Int) : Natural, BuiltinNumberType {
        override fun approximateAsInt64() = value.toLong()
    }

    @JvmInline
    value class LongNatural(val value: Long) : Natural, BuiltinNumberType {
        override fun approximateAsInt64() = value
    }

    @JvmInline
    value class IntInteger(val value: Int) : Integer, BuiltinNumberType {
        override fun approximateAsInt64() = value.toLong()
    }

    @JvmInline
    value class LongInteger(val value: Long) : Integer, BuiltinNumberType {
        override fun approximateAsInt64() = value
    }

    @JvmInline
    value class FloatReal(val value: Float) : Real, BuiltinNumberType {
        override fun approximateAsFP64() = value.toDouble()
    }

    @JvmInline
    value class DoubleReal(val value: Double) : Real, BuiltinNumberType {
        override fun approximateAsFP64() = value
    }
}