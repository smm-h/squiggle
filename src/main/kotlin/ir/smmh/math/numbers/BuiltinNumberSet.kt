package ir.smmh.math.numbers

import ir.smmh.math.InfinitelyIterable
import ir.smmh.math.MathematicalCollection
import ir.smmh.math.logic.Logical
import ir.smmh.math.numbers.BuiltinNumberType.*
import ir.smmh.math.numbers.Numbers.ZERO
import ir.smmh.math.settheory.AbstractSet
import kotlin.random.Random
import ir.smmh.math.MathematicalObject as M

sealed class BuiltinNumberSet<B : BuiltinNumberType, T : Numbers.Real> : AbstractSet<T>() {

    abstract val maxValue: B
    abstract val minValue: B

    override fun contains(it: T) = Logical.True
    override fun isEmpty() = Logical.False

    class IntNaturals(val pickerSize: Int) : BuiltinNumberSet<IntNatural, Numbers.Natural>() {
        override val debugText: String = "IntNaturals"
        override fun isNonReferentiallyEqualTo(that: M) = Logical.of(that is IntNaturals)

        override val maxValue = IntNatural(Int.MAX_VALUE)
        override val minValue = ZERO

        override val overElements = InfinitelyIterable<IntNatural> {
            var i = 0
            InfinitelyIterable.Iterator<IntNatural> {
                IntNatural(i++)
            }
        }

        override fun getPicker(random: Random) = MathematicalCollection.Picker<Numbers.Natural> {
            IntNatural(random.nextInt(pickerSize))
        }
    }

    class LongNaturals(val pickerSize: Long) : BuiltinNumberSet<LongNatural, Numbers.Natural>() {
        override val debugText: String = "LongNaturals"
        override fun isNonReferentiallyEqualTo(that: M) = Logical.of(that is LongNaturals)

        override val maxValue = LongNatural(Long.MAX_VALUE)
        override val minValue = LongNatural(0L)

        override val overElements = InfinitelyIterable<LongNatural> {
            var i = 0L
            InfinitelyIterable.Iterator<LongNatural> {
                LongNatural(i++)
            }
        }

        override fun getPicker(random: Random) = MathematicalCollection.Picker<Numbers.Natural> {
            LongNatural(random.nextLong(pickerSize))
        }
    }

    class IntIntegers(val pickerSize: Int) : BuiltinNumberSet<IntInteger, Numbers.Integer>() {
        override val debugText: String = "IntIntegers"
        override fun isNonReferentiallyEqualTo(that: M) = Logical.of(that is IntIntegers)

        override val maxValue = IntInteger(Int.MAX_VALUE)
        override val minValue = IntInteger(Int.MIN_VALUE)

        override val overElements = InfinitelyIterable<IntInteger> {
            var i = 0
            var n = false
            InfinitelyIterable.Iterator<IntInteger> {
                i = -i
                n = !n
                if (n) i++
                IntInteger(i)
            }
        }

        override fun getPicker(random: Random) = MathematicalCollection.Picker<Numbers.Integer> {
            IntInteger(random.nextInt(pickerSize * 2) - pickerSize)
        }
    }

    class LongIntegers(val pickerSize: Long) : BuiltinNumberSet<LongInteger, Numbers.Integer>() {
        override val debugText: String = "LongIntegers"
        override fun isNonReferentiallyEqualTo(that: M) = Logical.of(that is LongIntegers)

        override val maxValue = LongInteger(Long.MAX_VALUE)
        override val minValue = LongInteger(Long.MIN_VALUE)

        override val overElements = InfinitelyIterable<LongInteger> {
            var i = 0L
            var n = false
            InfinitelyIterable.Iterator<LongInteger> {
                i = -i
                n = !n
                if (n) i++
                LongInteger(i)
            }
        }

        override fun getPicker(random: Random) = MathematicalCollection.Picker<Numbers.Integer> {
            LongInteger(random.nextLong(pickerSize * 2) - pickerSize)
        }
    }

    class FloatReals(val pickerSize: Float) : BuiltinNumberSet<FloatReal, Numbers.Real>() {
        override val debugText: String = "FloatReals"
        override fun isNonReferentiallyEqualTo(that: M) = Logical.of(that is FloatReals)

        override val maxValue = FloatReal(Float.MAX_VALUE)
        override val minValue = FloatReal(Float.MIN_VALUE)

        private val increment = 0.1F

        override val overElements = InfinitelyIterable<FloatReal> {
            var i = 0F
            var n = false
            InfinitelyIterable.Iterator<FloatReal> {
                i = -i
                n = !n
                if (n) i += increment
                FloatReal(i)
            }
        }

        override fun getPicker(random: Random) = MathematicalCollection.Picker<Numbers.Real> {
            FloatReal((random.nextFloat() * 2 - 1) * pickerSize)
        }
    }

    class DoubleReals(val pickerSize: Double) : BuiltinNumberSet<DoubleReal, Numbers.Real>() {
        override val debugText: String = "DoubleReals"
        override fun isNonReferentiallyEqualTo(that: M) = Logical.of(that is DoubleReals)

        override val maxValue = DoubleReal(Double.MAX_VALUE)
        override val minValue = DoubleReal(Double.MIN_VALUE)

        private val increment = 0.1

        override val overElements = InfinitelyIterable<DoubleReal> {
            var i = 0.0
            var n = false
            InfinitelyIterable.Iterator<DoubleReal> {
                i = -i
                n = !n
                if (n) i += increment
                DoubleReal(i)
            }
        }

        override fun getPicker(random: Random) = MathematicalCollection.Picker<Numbers.Real> {
            DoubleReal((random.nextDouble() * 2 - 1) * pickerSize)
        }
    }
}