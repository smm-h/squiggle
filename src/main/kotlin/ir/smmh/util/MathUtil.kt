@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package ir.smmh.util

import kotlin.math.ln
import kotlin.math.pow

object MathUtil {
    fun factorial(n: Int): Long {
        return if (n == 0) 1 else n * factorial(n - 1)
    }

    fun gcd(a: Int, b: Int): Int {
        return if (b == 0) a else gcd(b, a % b)
    }

    fun gcd(a: Long, b: Long): Long {
        return if (b == 0L) a else gcd(b, a % b)
    }

    @JvmOverloads
    fun tens(x: Double, depth: Int = 0): Int {
        return if (isInt(x)) depth else tens(x * 10, depth + 1)
    }

    fun floor(x: Double): Int {
        return x.toInt()
    }

    fun ceil(x: Double): Int {
        val f = floor(x)
        return if (f.toDouble() == x) f else f + 1
    }

    fun round(x: Double): Int {
        val f = floor(x)
        return if (x - f < 0.5) f else f + 1
    }

    fun isInt(x: Double): Boolean {
        return floor(x).toDouble() == x
    }

    fun sqrt(x: Float): Float {
        return kotlin.math.sqrt(x.toDouble()).toFloat()
    }

    fun sqrt(x: Double): Double {
        return kotlin.math.sqrt(x)
    }

    fun sqrt(x: Int): Float {
        return kotlin.math.sqrt(x.toDouble()).toFloat()
    }

    fun sqr(x: Int): Int {
        return x * x
    }

    fun sqr(x: Double): Double {
        return x * x
    }

    fun power(b: Int, p: Int): Long {
        var x: Long = 1
        for (i in 0 until p) x *= b.toLong()
        return x
    }

    fun power(b: Double, p: Double): Double {
        return b.pow(p)
    }

    fun log(n: Double, b: Double): Double {
        return ln(n) / ln(b)
    }

    fun isPowerOf(n: Double, b: Double): Boolean {
        return isInt(log(n, b))
    }

    fun distance(x1: Int, y1: Int, x2: Int, y2: Int): Float {
        return sqrt(sqr(x1 - x2) + sqr(y1 - y2))
    }

    fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        return sqrt(sqr(x1 - x2) + sqr(y1 - y2))
    }

    fun sign(n: Int): Int {
        return n.compareTo(0)
    }

    fun sum(n: Int): Int {
        return n * (n + 1) / 2
    }

    fun countDigits(number: Int): Int {
        var n = number
        var count = 0
        do {
            count++
            n /= 10
        } while (n > 0)
        return count
    }
}