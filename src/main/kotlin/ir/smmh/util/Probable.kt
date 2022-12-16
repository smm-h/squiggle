package ir.smmh.util

import kotlin.random.Random

sealed class Probable<T> : () -> T {

    abstract operator fun plus(that: Probable<T>): Probable<T>

    abstract fun map(gray: Float): T

    private class List<T>(atom: Atom<T>) : Probable<T>() {

        private val list: MutableList<Pair<Double, () -> T>> = ArrayList()

        private var total = 0.0

        override fun invoke(): T = map(Random.nextFloat())

        override fun map(gray: Float): T {
            var x = gray * total
            for ((p, f) in list) {
                x -= p
                if (x <= 0) return f()
            }
            return list.last().second()
        }

        override fun plus(that: Probable<T>) = apply {
            when (that) {
                is List -> {
                    total += that.total
                    list.addAll(that.list)
                }
                is Atom -> {
                    total += that.probability
                    list.add(that.probability to that.function)
                }
            }
        }

        init {
            this + atom
        }
    }

    private class Atom<T>(val probability: Double, val function: () -> T) : Probable<T>() {
        override fun map(gray: Float): T = invoke()
        override fun invoke(): T = function()
        override fun plus(that: Probable<T>): Probable<T> = List<T>(this).also { it + that }
    }

    fun bagRandom(n: Int) = Bag<T>().also { bag -> repeat(n) { bag.add(this()) } }

    fun bagUniform(n: Int) = Bag<T>().also { bag -> for (i in 0 until n) bag.add(map(i.toFloat() / n)) }

    companion object {

        fun <T> p(probability: Int, function: () -> T): Probable<T> =
            p(probability.toDouble(), function)

        fun <T> p(probability: Double, function: () -> T): Probable<T> =
            Atom(probability, function)
    }
}