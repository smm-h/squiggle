package ir.smmh.util

import ir.smmh.util.Probable.Companion.p
import kotlin.random.Random

sealed class Probable<T> : () -> T {

    abstract operator fun plus(that: Probable<T>): Probable<T>

    private class List<T>(atom: Atom<T>) : Probable<T>() {

        private val list: MutableList<Pair<Double, () -> T>> = ArrayList()

        private var total = 0.0

        override fun invoke(): T {
            var x = Random.nextFloat() * total
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
        override fun invoke(): T = function()
        override fun plus(that: Probable<T>): Probable<T> = List<T>(this).also { it + that }
    }

    fun bag(n: Int) =
        Bag<T>().also { bag -> repeat(n) { bag.add(this()) } }

    companion object {

        fun <T> p(probability: Int, function: () -> T): Probable<T> =
            p(probability.toDouble(), function)

        fun <T> p(probability: Double, function: () -> T): Probable<T> =
            Atom(probability, function)
    }
}