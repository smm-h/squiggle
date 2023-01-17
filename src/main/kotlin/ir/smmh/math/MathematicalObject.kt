package ir.smmh.math

/**
 * The parent interface to everything in [ir.smmh.math]
 */
interface MathematicalObject {

    val debugText: String
    //TODO val tex: String
    //TODO fun express(): Expression

    fun isEqualTo(that: MathematicalObject) = this === that || isNonReferentiallyEqualTo(that)

    /**
     * Do not call this directly; use the equality operator (`==`) instead.
     */
    fun isNonReferentiallyEqualTo(that: MathematicalObject): Boolean

    abstract class Abstract : MathematicalObject {
        override fun toString(): String = debugText
        override fun hashCode(): Int = debugText.hashCode()
        override fun equals(other: Any?) = other is MathematicalObject && this.isEqualTo(other)
    }
}