package ir.smmh.nile.verbs

interface CanIterateOverValuesAndRemove<T> : CanIterateOverValues<T>, CanRemoveElementFrom<T> {
    val overValuesMutably: MutableIterable<T>
}