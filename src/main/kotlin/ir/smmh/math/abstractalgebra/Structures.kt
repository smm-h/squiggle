package ir.smmh.math.abstractalgebra

import ir.smmh.math.abstractalgebra.GroupLike.Properties.ASSOCIATIVE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.CLOSED
import ir.smmh.math.abstractalgebra.GroupLike.Properties.COMMUTATIVE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.COMPLEMENTIVE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.IDEMPOTENT
import ir.smmh.math.abstractalgebra.GroupLike.Properties.INVERTIBLE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.UNITAL
import ir.smmh.math.abstractalgebra.RingLike.Companion.abelianGroup
import ir.smmh.math.abstractalgebra.RingLike.Companion.field
import ir.smmh.math.abstractalgebra.RingLike.Companion.ring
import ir.smmh.math.abstractalgebra.RingLike.Properties.ABSORPTIVE
import ir.smmh.math.abstractalgebra.RingLike.Properties.DISTRIBUTIVE
import ir.smmh.math.matrix.Matrix
import ir.smmh.math.matrix.UniformMatrix
import ir.smmh.math.numbers.Complex
import ir.smmh.math.numbers.Rational
import ir.smmh.math.settheory.FiniteIntegersSet
import ir.smmh.math.settheory.MatricesSet
import ir.smmh.math.settheory.UniversalNumberSets

object Structures {
    // a field is INVERTIBLE and COMMUTATIVE, but a ring is not
    val Integer32Ring = ring<Int>(
        UniversalNumberSets.IntIntegers,
        Int::plus,
        Int::unaryMinus,
        0,
        Int::times,
        null,
        1,
        Int::minus,
        Int::div,
    )
    val Integer64Ring = ring<Long>(
        UniversalNumberSets.LongIntegers,
        Long::plus,
        Long::unaryMinus,
        0L,
        Long::times,
        null,
        1L,
        Long::minus,
        Long::div,
    )
    val RealDPField = field<Double>(
        UniversalNumberSets.DoubleRealNumbers,
        Double::plus,
        Double::unaryMinus,
        0.0,
        Double::times,
        { a -> if (a == 0.0) null else 1.0 / a },
        1.0,
        Double::minus,
        Double::div,
    )
    val RealFPField = field<Float>(
        UniversalNumberSets.FloatRealNumbers,
        Float::plus,
        Float::unaryMinus,
        0.0F,
        Float::times,
        { a -> if (a == 0F) null else 1F / a },
        1.0F,
        Float::minus,
        Float::div,
    )
    val RationalField = field<Rational>(
        UniversalNumberSets.RationalNumbers,
        Rational::add,
        Rational::negate,
        Rational.ZERO,
        Rational::multiply,
        Rational::reciprocal,
        Rational.ONE,
        Rational::subtract,
        Rational::divide,
    )
    val ComplexField = field<Complex>(
        UniversalNumberSets.ComplexNumbers,
        Complex::add,
        Complex::negate,
        Complex.ZERO,
        Complex::multiply,
        Complex::reciprocal,
        Complex.ONE,
        Complex::subtract,
        Complex::divide,
    )
    val BooleanRing = RingLike<Boolean>(
        UniversalNumberSets.Booleans,
        GroupLike(
            UniversalNumberSets.Booleans,
            Boolean::xor,
            Boolean::not,
            false,
            Property.Holder(
                CLOSED,
                UNITAL,
                ASSOCIATIVE,
                COMMUTATIVE,
                COMPLEMENTIVE,
            )
        ),
        GroupLike(
            UniversalNumberSets.Booleans,
            Boolean::and,
            Boolean::not,
            true,
            Property.Holder(
                CLOSED,
                UNITAL,
                ASSOCIATIVE,
                COMMUTATIVE,
                COMPLEMENTIVE,
                IDEMPOTENT,
            )
        ),
        null,
        null,
        Property.Holder(
            ABSORPTIVE,
        ),
        Property.Holder(
            DISTRIBUTIVE,
            ABSORPTIVE,
        )
    )

    fun <T : Any> matrixAdditiveGroup(
        degree: Int,
        structure: RingLike<T>,
        identity: Matrix<T>?,
    ): GroupLike<Matrix<T>> = GroupLike<Matrix<T>>(
        MatricesSet.of(degree, degree, structure),
        Matrix<T>::plus,
        Matrix<T>::negative,
        identity,
        Property.Holder(
            CLOSED,
            UNITAL,
            COMMUTATIVE,
            ASSOCIATIVE,
            INVERTIBLE,
        )
    )

    /**
     * Also known as a [general linear group](https://en.wikipedia.org/wiki/General_linear_group)
     */
    fun <T : Any> matrixMultiplicativeGroup(
        degree: Int,
        structure: RingLike<T>,
        identity: Matrix<T>?,
    ): GroupLike<Matrix<T>> = GroupLike<Matrix<T>>(
        MatricesSet.of(degree, degree, structure),
        Matrix<T>::times,
        Matrix<T>::inverse,
        identity,
        Property.Holder(

        )
    )

    /**
     * [Matrix ring](https://en.wikipedia.org/wiki/Matrix_ring)
     */
    fun <T : Any> matrixRing(
        degree: Int,
        structure: RingLike<T>,
        additiveIdentity: Matrix<T>?,
        multiplicativeIdentity: Matrix<T>?,
    ): RingLike<Matrix<T>> {
        return RingLike(
            MatricesSet.of(degree, degree, structure),
            matrixAdditiveGroup(degree, structure, additiveIdentity),
            matrixMultiplicativeGroup(degree, structure, multiplicativeIdentity),
            null,
            null,
            Property.Holder(),
            Property.Holder()
        )
    }

    fun <T : Any> numericMatrixRing(degree: Int, structure: RingLike<T>): RingLike<Matrix<T>> {
        val zero: T = structure.addition.identity!!
        val one: T = structure.multiplication.identity!!
        return matrixRing(
            degree,
            structure,
            UniformMatrix(degree, degree, structure, zero),
            Matrix.identity(degree).convert(structure) { if (it) one else zero }
        )
    }

    fun finiteCyclicGroup(degree: Int): GroupLike<Int> = abelianGroup(
        FiniteIntegersSet.of(degree),
        { a, b -> (a + b) % degree },
        { a -> (degree - a) % degree },
        0,
    )
}