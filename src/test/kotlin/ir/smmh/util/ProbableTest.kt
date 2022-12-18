package ir.smmh.util

import kotlin.random.Random

fun main() {
    val dice = Probable<Int>()
        .add(1) { 6 }
        .add(1) { 5 }
        .add(3) { Random.nextInt(4) + 1 }
    println(dice.bagUniform(1000).reportShares())
}