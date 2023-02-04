package ir.smmh.math.abstractalgebra

import ir.smmh.math.settheory.Set
import ir.smmh.math.MathematicalObject as M

interface AlgebraicStructure<T : M> {
    val domain: Set<T>
}