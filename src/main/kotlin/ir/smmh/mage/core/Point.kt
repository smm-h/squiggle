package ir.smmh.mage.core

import ir.smmh.mage.core.Utils.sqr
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

sealed interface Point {

    val x: Double
    val y: Double

    operator fun component1() = x
    operator fun component2() = y

    operator fun plus(other: Point): Point = of(
        x + other.x,
        y + other.y
    )

    operator fun minus(other: Point): Point = of(
        x - other.x,
        y - other.y
    )

    operator fun plus(vector: Vector): Point = of(
        x + vector.x,
        y + vector.y
    )

    operator fun minus(vector: Vector): Point = of(
        x - vector.x,
        y - vector.y
    )

    operator fun times(factor: Double): Point = of(
        x * factor,
        y * factor
    )

    operator fun div(factor: Double): Point = of(
        x / factor,
        y / factor
    )

    fun add(magnitude: Double, direction: Double): Point = of(
        x + magnitude * cos(direction),
        y + magnitude * -sin(direction)
    )

    private abstract class AbstractImpl : Point {
        final override fun equals(other: Any?): Boolean =
            if (other is Point) x == other.x && y == other.y else false

        final override fun hashCode(): Int = Objects.hash(x, y)

        final override fun toString(): String = "($x, $y)"
    }

    private data class Impl(override val x: Double, override val y: Double) : AbstractImpl()

    object OnIdentityLine {
        fun at(x: Double): Point = of(x, x)
        val Half: Point = at(0.5)
        val One: Point = at(1.0)
        val NegativeOne: Point = at(-1.0)
    }

    companion object {

        val origin = of(0.0, 0.0)

        fun of(x: Int, y: Int): Point =
            of(x.toDouble(), y.toDouble())

        fun of(x: Float, y: Float): Point =
            of(x.toDouble(), y.toDouble())

        fun of(x: Double, y: Double): Point =
            Impl(x, y)

        fun of(mutable: Mutable): Point =
            of(mutable.x, mutable.y)

        fun Graphics.point(point: Point) =
            point(point.x, point.y)

        fun Graphics.line(from: Point, to: Point) =
            line(from.x, from.y, to.x, to.y)

        fun Graphics.rectangle(point: Point, w: Double, h: Double) =
            rectangle(point.x, point.y, w, h)

        fun Graphics.ellipse(point: Point, w: Double, h: Double) =
            ellipse(point.x, point.y, w, h)

        fun Graphics.square(point: Point, w: Double) =
            square(point.x, point.y, w)

        fun Graphics.circle(point: Point, r: Double) =
            circle(point.x, point.y, r)

        fun Graphics.gridLines(size: Size, step: Size, start: Point = origin) {
            var i = start.x
            while (i < size.width) {
                line(i, start.y, i, size.height)
                i += step.width
            }
            var j = start.y
            while (j < size.height) {
                line(start.x, j, size.width, j)
                j += step.height
            }
        }

        @Suppress("DuplicatedCode")
        fun Graphics.gridDots(size: Size, step: Size, start: Point = origin) {
            var i = start.x
            while (i < size.width) {
                var y = start.y
                while (y < size.height) {
                    point(i, y)
                    y += step.height
                }
                i += step.width
            }
            var j = start.y
            while (j < size.height) {
                var x = start.x
                while (x < size.width) {
                    point(x, j)
                    x += step.width
                }
                j += step.height
            }
        }

        var Graphics.Path.point: Point
            get() = of(x, y)
            set(value) {
                move(value.x, value.y)
            }

        fun Graphics.Path.move(point: Point) =
            move(point.x, point.y)

        fun Graphics.Path.line(point: Point) =
            line(point.x, point.y)

        fun Graphics.Path.lines(vararg points: Point) =
            points.forEach { line(it) }

        fun Graphics.Path.lines(points: Iterable<Point>) =
            points.forEach { line(it) }

        fun Graphics.Path.quadratic(p1: Point, p2: Point) =
            quadratic(p1.x, p1.y, p2.x, p2.y)

        fun Graphics.Path.bezier(p1: Point, p2: Point, p3: Point) =
            bezier(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)

        fun Graphics.TransformationMatrix.translate(point: Point) =
            translate(point.x, point.y)

        fun Graphics.TransformationMatrix.scale(point: Point) =
            scale(point.x, point.y)

        fun Graphics.TransformationMatrix.shear(point: Point) =
            shear(point.x, point.y)

        operator fun Double.times(point: Point): Point = of(
            this * point.x,
            this * point.y
        )
    }

    sealed interface Mutable : Point {
        override var x: Double
        override var y: Double

        operator fun plusAssign(point: Point) {
            x += point.x
            y += point.y
        }

        operator fun minusAssign(point: Point) {
            x -= point.x
            y -= point.y
        }

        operator fun plusAssign(vector: Vector) {
            x += vector.x
            y += vector.y
        }

        operator fun minusAssign(vector: Vector) {
            x -= vector.x
            y -= vector.y
        }

        fun addInplace(magnitude: Double, direction: Double) {
            x += magnitude * cos(direction)
            y += magnitude * -sin(direction)
        }

        operator fun timesAssign(factor: Double) {
            x *= factor
            y *= factor
        }

        operator fun divAssign(factor: Double) {
            x /= factor
            y /= factor
        }

        private data class Impl(override var x: Double, override var y: Double) : AbstractImpl(), Mutable

        companion object {
            fun empty(): Mutable =
                Impl(0.0, 0.0)

            fun of(x: Int, y: Int): Mutable =
                of(x.toDouble(), y.toDouble())

            fun of(x: Float, y: Float): Mutable =
                of(x.toDouble(), y.toDouble())

            fun of(x: Double, y: Double): Mutable =
                Impl(x, y)

            fun of(point: Point): Mutable =
                of(point.x, point.y)
        }
    }

    fun length() =
        distance(origin)

    fun distance(other: Point) =
        distance(other.x, other.y)

    fun distance(x: Double, y: Double) =
        sqrt(distanceSquared(x, y))

    fun distanceSquared(other: Point) =
        distanceSquared(other.x, other.y)

    fun distanceSquared(x: Double, y: Double) =
        sqr(x - this.x) + sqr(y - this.y)
}