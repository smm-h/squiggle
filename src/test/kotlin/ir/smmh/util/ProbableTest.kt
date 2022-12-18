package ir.smmh.util

import kotlin.random.Random

fun main() {
    val dice = Probable<Int>().apply {
        add(3) { Random.nextInt(4) + 1 }
        add(1) { 5 }
        add(1) { 6 }
    }
    println(dice.bagUniform(1000).reportShares())
}