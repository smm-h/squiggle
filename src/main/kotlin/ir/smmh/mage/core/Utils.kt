package ir.smmh.mage.core

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.random.Random

object Utils {

    const val fullCircle = PI * 2

    private const val ratio = PI / 180.0

    fun Double.toDegrees() = this / ratio
    val Double.degrees get() = this * ratio

    fun Random.direction() =
        nextDouble(fullCircle)

    fun sqr(n: Double): Double =
        n * n

    fun hypotenuse(a: Double, b: Double): Double =
        sqrt(sqr(a) + sqr(b))

    fun angle(x: Double, y: Double): Double =
        -atan2(y, x)
}