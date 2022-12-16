package ir.smmh.util

class Bag<T> : Iterable<T> {

    var total = 0
        private set

    private val map: MutableMap<T, Int> = HashMap()

    fun getCount(it: T): Int = map[it] ?: 0

    fun getShare(it: T): Double = getCount(it).toDouble() / total

    fun add(it: T) {
        map[it] = getCount(it) + 1
        total++
    }

    fun addAll(them: Iterable<T>) {
        for (it in them)
            add(it)
    }

    override fun iterator(): Iterator<T> =
        map.keys.iterator()

    override fun toString() = reportCounts()

    fun reportCounts(): String = joinToString("\n") { "${getCount(it)}\t$it" }

    fun reportShares(): String = joinToString("\n") { "${getShare(it) * 100}%\t$it" }
}