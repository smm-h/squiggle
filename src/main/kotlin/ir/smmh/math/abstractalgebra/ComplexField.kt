package ir.smmh.math.abstractalgebra

import ir.smmh.math.numbers.BuiltinNumberSet
import ir.smmh.math.numbers.Complex
import ir.smmh.math.numbers.Numbers
import ir.smmh.math.settheory.Set

object ComplexField : RingLikeStructure.Field<Complex> {
    override val domain: Set<out Complex> = BuiltinNumberSet.DoubleReals(1000.0)
    override val additiveGroup = object : GroupLikeStructure.AbelianGroup<Complex> {
        override val domain: Set<out Complex> by this@ComplexField::domain
        override fun operate(a: Complex, b: Complex): Complex = a + b
        override val identityElement: Complex = Numbers.ZERO
        override fun invert(a: Complex): Complex = -a
    }
    override val multiplicativeGroup = object : GroupLikeStructure.AbelianGroup<Complex> {
        override val domain: Set<out Complex> by this@ComplexField::domain
        override fun operate(a: Complex, b: Complex): Complex = a * b
        override val identityElement: Complex = Numbers.ONE
        override fun invert(a: Complex): Complex = a.reciprocal
    }
}