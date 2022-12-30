package ir.smmh.nile.verbs

interface CanIterateOverValuesMutably<T> : CanIterateOverValues<T>, CanRemoveElementFrom<T> {
    val overValuesMutably: MutableIterable<T>
}