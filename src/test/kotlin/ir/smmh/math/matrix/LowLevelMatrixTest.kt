package ir.smmh.math.matrix

import ir.smmh.math.matrix.Matrix.ValueFunction.independent
import ir.smmh.nile.Chronometer
import kotlin.random.Random

//internal object LowLevelMatrixTest {
fun main() {
    val k = 10
    val f = independent { Random.nextInt(20) - 10 }
    val m = { n: Int -> LowLevelMatrix.Int(n, n, f) }
    for (i in (10..510 step 10).reversed()) Chronometer.N().apply {
        reset()
        repeat(k) { m(i) * m(i) }
        val t = stop() / k
        println("$i\t: $t")
    }
}