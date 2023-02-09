package ir.smmh.math.abstractalgebra

import ir.smmh.math.numbers.Numbers
import ir.smmh.math.numbers.Numbers.Natural
import ir.smmh.math.settheory.FiniteNaturalsSet
import ir.smmh.math.settheory.Set

class FiniteCyclicGroup private constructor(val dInt: Int, val dNatural: Natural) :
    AlgebraicStructure.Abstract<Natural>(), GroupLikeStructure.AbelianGroup<Natural> {
    constructor(degree: Int) : this(degree, Natural.of(degree))
    constructor(degree: Natural) : this(degree.approximateAsLong().toInt(), degree)

    override val domain: Set<Natural> = FiniteNaturalsSet(dInt)
    override fun operate(a: Natural, b: Natural): Natural = (a + b) % dNatural
    override val identityElement: Natural = Numbers.ZERO
    override fun invert(a: Natural): Natural = (dNatural - a).asNatural()!! % dNatural
}