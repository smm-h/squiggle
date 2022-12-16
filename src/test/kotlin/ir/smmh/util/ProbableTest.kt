package ir.smmh.util

import ir.smmh.util.Probable.Companion.p
import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random

fun main() {
    val dice = p(1) { 6 } + p(1) { 5 } + p(3) { Random.nextInt(4) + 1 }
    println(dice.bagUniform(1000).reportShares())
}