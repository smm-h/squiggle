package ir.smmh.mage.core

import kotlin.random.Random

interface Size {
    val width: Double
    val height: Double

    val area: Double get() = width * height

    operator fun component1() = width
    operator fun component2() = height

    private data class Impl(override val width: Double, override val height: Double) : Size {
        init {
            if (width <= 0) throw IllegalArgumentException("width cannot be zero or negative")
            if (height <= 0) throw IllegalArgumentException("height cannot be zero or negative")
        }
    }

    fun getCorners(): List<Point> = listOf(
        Point.of(0.0, 0.0),
        Point.of(width, 0.0),
        Point.of(0.0, height),
        Point.of(width, height),
    )

    fun forEach(action: (Point) -> Unit) {
        Point.Mutable.empty().apply {
            while (y < height) {
                while (x < width) {
                    action(this)
                    x++
                }
                y++
                x = 0.0
            }
        }
    }

    fun randomPoint(): Point = Point.of(
        Random.nextDouble(width),
        Random.nextDouble(height)
    )

    companion object {
        val OneOne: Size = Impl(1.0, 1.0)

        fun of(widthAndHeight: Int): Size =
            of(widthAndHeight, widthAndHeight)

        fun of(width: Int, height: Int): Size =
            Impl(width.toDouble(), height.toDouble())
    }
}