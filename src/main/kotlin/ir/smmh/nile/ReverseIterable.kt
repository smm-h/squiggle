package ir.smmh.nile

interface ReverseIterable<T> : Iterable<T> {
    fun inReverse(): Iterable<T>
}