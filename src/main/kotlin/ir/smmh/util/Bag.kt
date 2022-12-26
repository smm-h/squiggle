package ir.smmh.util

import ir.smmh.nile.CanIterateOverValues
import ir.smmh.nile.Change
import ir.smmh.nile.verbs.CanAddTo

class Bag<T>(override val changesToSize: Change = Change()) :
    CanIterateOverValues<T>, CanAddTo<T> {

    override var size = 0
        private set

    private val map: MutableMap<T, Int> = HashMap()

    fun getCount(it: T): Int = map[it] ?: 0

    fun getShare(it: T): Double = getCount(it).toDouble() / size

    override fun add(toAdd: T) {
        map[toAdd] = getCount(toAdd) + 1
        size++
    }

    override val overValues: Iterable<T>
        get() = map.keys

    override fun toString() = reportCounts()

    fun reportCounts(): String = overValues.joinToString("\n") { "${getCount(it)}\t$it" }

    fun reportShares(): String = overValues.joinToString("\n") { "${getShare(it) * 100}%\t$it" }
}