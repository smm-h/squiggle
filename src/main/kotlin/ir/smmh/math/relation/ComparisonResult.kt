package ir.smmh.math.relation

sealed class ComparisonResult {
    object Incomparable : ComparisonResult()
    sealed class Comparable : ComparisonResult() {
        object EqualTo : Comparable()

        //sealed class Strict : Comparable()
        object LessThan : Comparable()
        object GreaterThan : Comparable()
    }
}