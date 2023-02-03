package ir.smmh.math.settheory

import ir.smmh.math.logic.Logical
import ir.smmh.math.MathematicalObject as M

abstract class AbstractSet<T : M> : M.Abstract(), Set<T> {
    override val debugText by lazy {
        overElements?.joinToString(", ", "{", "}", limit = if (this !is Set.Finite<*>) 10 else -1) { it.debugText }
            ?: "{???}"
    }
    override val tex by lazy {
        if (isEmpty() == Logical.True) "\\emptyset" else
            overElements?.joinToString(",", "{\\{", "\\}}", limit = if (this !is Set.Finite<*>) 10 else -1) { it.tex }
                ?: "{\\{???\\}}"
    }
}