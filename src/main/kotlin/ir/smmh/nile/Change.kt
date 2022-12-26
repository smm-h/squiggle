package ir.smmh.nile

/**
 * Represents change in some aspect of the mutable data of a mutable class.
 * You can customize [beforeChange] and [afterChange] which are called before
 * and after every mutation respectively. Note that it will only be called if
 * the data is actually changed, not when a change is attempted. For example
 * clearing an empty list should not call it.
 */
class Change {
    val beforeChange: MutableList<() -> Unit> = ArrayList()
    val afterChange: MutableList<() -> Unit> = ArrayList()
    fun beforeChange() = beforeChange.forEach { it() }
    fun afterChange() = afterChange.forEach { it() }
}