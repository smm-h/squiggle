package ir.smmh.math.settheory

import ir.smmh.math.abstractalgebra.RingLike
import ir.smmh.math.matrix.FunctionMatrix
import ir.smmh.math.matrix.Matrix
import ir.smmh.math.numbers.Complex
import ir.smmh.math.numbers.Rational
import ir.smmh.nile.Cache
import kotlin.random.Random

object Sets {

    val U = UniversalSet<Any?> { null }

    private fun randomInt() = Random.nextInt(1000) - 500
    private fun randomDouble() = Random.nextDouble(1000.0) - 500

    val Integer32: Set<Int> = UniversalSet(::randomInt)
    val Integer64: Set<Long> = UniversalSet { Random.nextLong(1000L) - 500L }
    val RealFP: Set<Float> = UniversalSet { Random.nextFloat() * 1000F - 500F }
    val RealDP: Set<Double> = UniversalSet(::randomDouble)
    val RationalNumbers: Set<Rational> = UniversalSet { Rational.of(randomInt(), randomInt()) }
    val ComplexNumbers: Set<Complex> = UniversalSet { Complex(randomDouble(), randomDouble()) }
    val Boolean: Set<Boolean> = UniversalSet { Random.nextBoolean() }

    class Integers private constructor(val degree: Int): Set<Int> {
        override fun pick(): Int = Random.nextInt(degree)
        override fun contains(it: Int): Boolean = it >= 0 && it < degree
        companion object {
            private val cache = Cache<Int, Integers> { Integers(it) }
            fun of(degree: Int): Integers = cache(degree)
        }
    }

    class Matrices<T> private constructor(
        val rows: Int,
        val columns: Int,
        val structure: RingLike<T>,
    ) : Set<Matrix<T>> {
        override fun pick(): Matrix<T> =
            FunctionMatrix.Memoized(rows, columns, structure) { _, _ -> structure.domain.pick() }

        override fun contains(it: Matrix<T>): Boolean =
            it.rows == rows && it.columns == columns && it.structure == structure

        companion object {
            private val cache = Cache<Triple<Int, Int, RingLike<*>>, Matrices<*>> {
                Matrices(it.first, it.second, it.third)
            }

            @Suppress("UNCHECKED_CAST")
            fun <T> of(rows: Int, columns: Int, structure: RingLike<T>): Matrices<T> =
                cache(Triple(rows, columns, structure)) as Matrices<T>
        }
    }
}