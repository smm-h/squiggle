package ir.smmh.nile.verbs

interface CanIterateOverValuesInReverse<T> : CanIterateOverValues<T> {
    val overValuesInReverse: Iterable<T>
}