package ir.smmh.math

import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical

/**
 * The parent interface to everything in [ir.smmh.math]
 */
interface MathematicalObject {

    val debugText: String
    val tex: String
    val type: String get() = javaClass.simpleName

    // TODO val wikipediaLink: String
    // TODO fun express() = Calculable.ContextIndependent(this)

    infix fun isEqualTo(that: MathematicalObject): Knowable =
        if (this === that) Logical.True else isNonReferentiallyEqualTo(that)

    /**
     * Do not call this directly; use [isEqualTo] instead.
     */
    fun isNonReferentiallyEqualTo(that: MathematicalObject): Knowable
    // TODO isClassicallyEqualTo

    interface WellDefined : MathematicalObject {
        override fun isEqualTo(that: MathematicalObject): Logical = super.isEqualTo(that) as Logical
        override fun isNonReferentiallyEqualTo(that: MathematicalObject): Logical
    }

    abstract class Abstract : MathematicalObject {
        override fun toString(): String = debugText
        override fun hashCode(): Int = debugText.hashCode()
        override fun equals(other: Any?): Boolean =
            other is MathematicalObject && this.isEqualTo(other) == Logical.True
    }
}