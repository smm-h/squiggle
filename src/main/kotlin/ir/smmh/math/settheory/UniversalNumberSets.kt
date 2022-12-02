package ir.smmh.math.settheory

import ir.smmh.math.numbers.Complex
import ir.smmh.math.numbers.Rational
import ir.smmh.math.symbolic.Expression
import kotlin.random.Random

object UniversalNumberSets {

    private fun randomInt() = Random.nextInt(1000) - 500
    private fun randomDouble() = Random.nextDouble(1000.0) - 500

    val Booleans = Set.Specific.Finite.Universal<Boolean>(listOf(false, true), Random::nextBoolean)
    val IntIntegers = Set.Specific.CountablyInfinite.Universal<Int>(::randomInt)
    val LongIntegers = Set.Specific.CountablyInfinite.Universal<Long> { Random.nextLong(1000L) - 500L }
    val RationalNumbers = Set.Specific.CountablyInfinite.Universal<Rational> { Rational.of(randomInt(), randomInt()) }
    val DoubleRealNumbers = Set.Specific.Uncountable.Universal<Double>(::randomDouble)
    val FloatRealNumbers = Set.Specific.Uncountable.Universal<Float> { Random.nextFloat() * 1000F - 500F }
    val ComplexNumbers = Set.Specific.Uncountable.Universal<Complex> { Complex(randomDouble(), randomDouble()) }

    val B = Expression.of(Set.Universal, Booleans, "\\mathbb{B}")
    val Z = Expression.of(Set.Universal, IntIntegers, "\\mathbb{I}")
    val R = Expression.of(Set.Universal, DoubleRealNumbers, "\\mathbb{R}")
    val Q = Expression.of(Set.Universal, RationalNumbers, "\\mathbb{Q}")
    val C = Expression.of(Set.Universal, ComplexNumbers, "\\mathbb{C}")
}