package ir.smmh.mage.core

sealed interface Group<T : Any> : Iterable<T> {
    fun add(it: T)
    fun remove(it: T)
    fun clear()
}