package ir.smmh.math.settheory

import ir.smmh.math.MathematicalObject

abstract class AbstractSet<T : MathematicalObject> : MathematicalObject.Abstract(), Set<T> {
    override val debugText by lazy {
        overElements?.joinToString(", ", "{", "}", limit = if (this !is Set.Finite<*>) 10 else -1) { it.debugText }
            ?: "{???}"
    }
}