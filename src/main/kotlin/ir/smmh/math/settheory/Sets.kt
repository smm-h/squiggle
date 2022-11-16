package ir.smmh.math.settheory

import ir.smmh.math.numbers.Complex
import ir.smmh.math.numbers.Rational
import kotlin.random.Random

object Sets {
    private class BaseSet<T>(val r: () -> T) : ir.smmh.math.settheory.Set<T> {
        override fun pick(): T = r()
        override fun pickTwo(): Pair<T, T> = r() to r()
        override fun pickThree(): Triple<T, T, T> = Triple(r(), r(), r())
        override fun contains(it: T) = true
    }

    private fun randomInt() = Random.nextInt(1000) - 500
    private fun randomDouble() = Random.nextDouble(1000.0) - 500

    val Integer32: Set<Int> = BaseSet(::randomInt)
    val Integer64: Set<Long> = BaseSet { Random.nextLong(1000L) - 500L }
    val RealFP: Set<Float> = BaseSet { Random.nextFloat() * 1000F - 500F }
    val RealDP: Set<Double> = BaseSet(::randomDouble)
    val Rational: Set<Rational> = BaseSet { Rational(randomInt(), randomInt()) }
    val Complex: Set<Complex> = BaseSet { Complex(randomDouble(), randomDouble()) }
    val Boolean: Set<Boolean> = BaseSet { Random.nextBoolean() }
}