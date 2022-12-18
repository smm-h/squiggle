package ir.smmh.math.settheory

import ir.smmh.math.numbers.Complex
import ir.smmh.math.numbers.Rational
import kotlin.random.Random

object UniversalNumberSets {

    private fun randomInt() = Random.nextInt(1000) - 500
    private fun randomDouble() = Random.nextDouble(1000.0) - 500

    val Booleans =
        Set.Specific.Finite.Universal<Boolean>(listOf(false, true), Random::nextBoolean)
    val IntIntegers =
        Set.Specific.Infinite.Countable.Universal<Int>(::randomInt)
    val LongIntegers =
        Set.Specific.Infinite.Countable.Universal<Long> { Random.nextLong(1000L) - 500L }
    val RationalNumbers =
        Set.Specific.Infinite.Countable.Universal<Rational> { Rational.of(randomInt(), randomInt()) }
    val DoubleRealNumbers =
        Set.Specific.Infinite.Uncountable.Universal<Double>(::randomDouble)
    val FloatRealNumbers =
        Set.Specific.Infinite.Uncountable.Universal<Float> { Random.nextFloat() * 1000F - 500F }
    val ComplexNumbers =
        Set.Specific.Infinite.Uncountable.Universal<Complex> { Complex(randomDouble(), randomDouble()) }
}