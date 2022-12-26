package ir.smmh.math.settheory

abstract class AbstractSet : Set.Finite.NonEmpty {
    override fun toString() = string
    private val string by lazy { overElements.joinToString(", ", "{", "}") }
}