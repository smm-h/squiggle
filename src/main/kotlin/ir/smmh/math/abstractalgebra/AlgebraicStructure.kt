package ir.smmh.math.abstractalgebra

import ir.smmh.math.MathematicalObject
import ir.smmh.math.settheory.Set

interface AlgebraicStructure<T : MathematicalObject> {
    val domain: Set<T>
}