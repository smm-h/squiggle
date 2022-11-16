package ir.smmh.math.settheory

interface Set<T> {
    fun pick(): T
    fun pickTwo(): Pair<T, T>
    fun pickThree(): Triple<T, T, T>
    operator fun contains(it: T): Boolean

    interface Ordered<T> : Set<T> {
        val partialOrder: (T, T) -> Boolean

//        interface Partially<T> : Ordered<T> {
//            val partialOrder:
//        }
//        interface Totally<T> : Ordered<T> {
//
//        }
    }
}