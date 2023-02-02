package ir.smmh.math

import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical

/**
 * The parent interface to everything in [ir.smmh.math]
 */
interface MathematicalObject {

    val debugText: String
    //TODO val tex: String
    //TODO fun express(): Expression

    fun isEqualTo(that: MathematicalObject): Knowable =
        if (this === that) Logical.True else isNonReferentiallyEqualTo(that)

    /**
     * Do not call this directly; use the equality operator (`==`) instead.
     */
    fun isNonReferentiallyEqualTo(that: MathematicalObject): Knowable
    // TODO isClassicallyEqualTo

    abstract class Abstract : MathematicalObject {
        override fun toString(): String = debugText
        override fun hashCode(): Int = debugText.hashCode()
        override fun equals(other: Any?): Boolean =
            other is MathematicalObject && this.isEqualTo(other) == Logical.True
    }
}