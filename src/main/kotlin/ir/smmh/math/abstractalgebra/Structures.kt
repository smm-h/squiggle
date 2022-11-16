package ir.smmh.math.abstractalgebra

import ir.smmh.math.abstractalgebra.GroupLike.Properties.ASSOCIATIVE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.CLOSED
import ir.smmh.math.abstractalgebra.GroupLike.Properties.COMMUTATIVE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.COMPLEMENTIVE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.IDEMPOTENT
import ir.smmh.math.abstractalgebra.GroupLike.Properties.UNITAL
import ir.smmh.math.abstractalgebra.RingLike.Companion.field
import ir.smmh.math.abstractalgebra.RingLike.Companion.ring
import ir.smmh.math.abstractalgebra.RingLike.Properties.ABSORPTIVE
import ir.smmh.math.abstractalgebra.RingLike.Properties.DISTRIBUTIVE
import ir.smmh.math.numbers.Complex
import ir.smmh.math.numbers.Rational
import ir.smmh.math.settheory.Sets

object Structures {
    // a field is INVERTIBLE and COMMUTATIVE, but a ring is not
    val Integer32Ring = ring<Int>(
        Sets.Integer32,
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
        Sets.Integer64,
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
        Sets.RealDP,
        Double::plus,
        Double::unaryMinus,
        0.0,
        Double::times,
        { a -> 1.0 / a },
        1.0,
        Double::minus,
        Double::div,
    )
    val RealFPField = field<Float>(
        Sets.RealFP,
        Float::plus,
        Float::unaryMinus,
        0.0F,
        Float::times,
        { a -> 1.0F / a },
        1.0F,
        Float::minus,
        Float::div,
    )
    val RationalField = field<Rational>(
        Sets.Rational,
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
        Sets.Complex,
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
        Sets.Boolean,
        GroupLike(
            Sets.Boolean,
            Boolean::xor,
            Boolean::not,
            false,
            Property.Holder(
                CLOSED,
                UNITAL,
                ASSOCIATIVE,
                COMMUTATIVE,
                COMPLEMENTIVE,
            ),
        ),
        GroupLike(
            Sets.Boolean,
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
            ),
        ),
        null,
        null,
        Property.Holder(
            ABSORPTIVE,
        ),
        Property.Holder(
            DISTRIBUTIVE,
            ABSORPTIVE,
        ),
    )
}