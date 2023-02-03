package ir.smmh.math.abstractalgebra

import ir.smmh.math.numbers.BuiltinNumberType
import ir.smmh.math.numbers.Numbers
import ir.smmh.math.numbers.Numbers.Integer
import ir.smmh.math.settheory.FiniteIntegersSet
import ir.smmh.math.settheory.Set

class FiniteCyclicGroup private constructor(val dInt: Int, val dInteger: Integer) :
    GroupLikeStructure.AbelianGroup<Integer> {
    constructor(degree: Int) : this(degree, BuiltinNumberType.IntInteger(degree))
    constructor(degree: Integer) : this(degree.approximateAsLong().toInt(), degree)

    override val domain: Set<Integer> = FiniteIntegersSet(dInt)
    override fun operate(a: Integer, b: Integer): Integer = (a + b) % dInteger
    override val identityElement: Integer = Numbers.ZERO
    override fun invert(a: Integer): Integer = (dInteger - a) % dInteger
}