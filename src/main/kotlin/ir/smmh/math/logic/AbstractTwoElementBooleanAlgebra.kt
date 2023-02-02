package ir.smmh.math.logic

import ir.smmh.math.MathematicalObject as M

/**
 * A [AbstractTwoElementBooleanAlgebra] is the two-element [BooleanAlgebra] whose
 * [domain] is a [BooleanDomain].
 *
 * [Wikipedia](https://en.wikipedia.org/wiki/Two-element_Boolean_algebra)
 */
abstract class AbstractTwoElementBooleanAlgebra<T : M>(
    override val domain: BooleanDomain<T>,
) : AbstractBooleanAlgebra<T>() {
    override val greatestElement by domain::truth
    override val leastElement by domain::falsehood
}