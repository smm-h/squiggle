package ir.smmh.util

import kotlin.random.Random

class Probable<T> : () -> T, Iterable<Pair<Double, () -> T>> {

    private val pairs: MutableSet<Pair<Double, () -> T>> = HashSet()
    private var total = 0.0

    override fun iterator(): Iterator<Pair<Double, () -> T>> =
        pairs.iterator()

    override fun invoke(): T = map(Random.nextFloat())

    fun map(value: Float): T {
        var x = value * total
        for ((p, f) in pairs) {
            x -= p
            if (x < 0) return f()
        }
        throw IllegalArgumentException("value must be in range [0, 1)")
    }

    fun addAll(that: Probable<T>) {
        total += that.total
        pairs.addAll(that.pairs)
    }

    fun add(probability: Double, function: () -> T): Pair<Double, () -> T> {
        total += probability
        val pair = probability to function
        pairs.add(pair)
        return pair
    }

    fun add(probability: Int, function: () -> T) =
        add(probability.toDouble(), function)

    operator fun contains(it: Pair<Double, () -> T>) =
        it in pairs

    fun remove(pair: Pair<Double, () -> T>) {
        if (pair in this) if (pairs.remove(pair)) total -= pair.first
    }

    fun clear() {
        total = 0.0
        pairs.clear()
    }

    fun bagRandom(n: Int) =
        Bag<T>().also { bag -> repeat(n) { bag.add(this()) } }

    fun bagUniform(n: Int) =
        Bag<T>().also { bag -> for (i in 0 until n) bag.add(map(i.toFloat() / n)) }
}