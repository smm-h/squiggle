package ir.smmh.util

import kotlin.random.Random

class Probable<T> : () -> T {

    private val list: MutableList<Pair<Double, () -> T>> = ArrayList()
    private var total = 0.0

    override fun invoke(): T = map(Random.nextFloat())

    fun map(gray: Float): T {
        var x = gray * total
        for ((p, f) in list) {
            x -= p
            if (x <= 0) return f()
        }
        return list.last().second()
    }

    fun add(that: Probable<T>) = apply {
        total += that.total
        list.addAll(that.list)
    }

    fun add(probability: Double, function: () -> T) = apply {
        total += probability
        list.add(probability to function)
    }

    fun add(probability: Int, function: () -> T) = add(probability.toDouble(), function)

    fun bagRandom(n: Int) = Bag<T>().also { bag -> repeat(n) { bag.add(this()) } }

    fun bagUniform(n: Int) = Bag<T>().also { bag -> for (i in 0 until n) bag.add(map(i.toFloat() / n)) }
}