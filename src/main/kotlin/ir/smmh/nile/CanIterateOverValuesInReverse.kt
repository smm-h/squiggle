package ir.smmh.nile

interface CanIterateOverValuesInReverse<T> : CanIterateOverValues<T> {
    val overValuesInReverse: Iterable<T>
}