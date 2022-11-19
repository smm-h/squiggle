package ir.smmh.math.settheory

fun interface UniversalSet<T> : Set<T> {
    override fun contains(it: T) = true
}