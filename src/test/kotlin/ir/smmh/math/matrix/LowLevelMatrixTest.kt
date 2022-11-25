package ir.smmh.math.matrix

import ir.smmh.nile.Chronometer
import kotlin.random.Random

//internal object LowLevelMatrixTest {
fun main() {
    val n = 100
    val f = { _: Int, _: Int -> Random.nextInt(20) - 10 }
    val m = { LowLevelMatrix.Int(n, n, f) }
    val a = m()
    val b = m()
    val c = Chronometer.ByCurrentTimeMillis()
    c.reset()
    val x = a * b
    val t = c.stop()
    println(t)
}