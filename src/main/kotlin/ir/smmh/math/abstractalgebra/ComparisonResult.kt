package ir.smmh.math.abstractalgebra

sealed class ComparisonResult {
    sealed class Comparable : ComparisonResult()
    object LessThan : Comparable()
    object EqualTo : Comparable()
    object GreaterThan : Comparable()
    object Incomparable : ComparisonResult()
}