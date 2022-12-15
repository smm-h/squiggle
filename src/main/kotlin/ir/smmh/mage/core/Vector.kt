package ir.smmh.mage.core

import ir.smmh.mage.core.Point.Companion.line
import ir.smmh.mage.core.Utils.degrees
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * "Historically, vectors were introduced in geometry and physics (typically in
 * mechanics) for quantities that have both a magnitude and a direction, such as
 * displacements, forces and velocity. Such quantities are represented by
 * geometric vectors in the same way as distances, masses and time are
 * represented by real numbers."
 */
sealed interface Vector {

    val x: Double
    val y: Double

    val point: Point
        get() = Point.of(x, y)
    val magnitude: Double
        get() = hypot(x, y)
    val direction: Double
        get() = angle(x, y)
    val unit: Vector
        get() = towards(cos(direction), -sin(direction))

    fun add(magnitude: Double, direction: Double): Vector = towards(
        x + magnitude * cos(direction),
        y + magnitude * -sin(direction)
    )

    operator fun plus(other: Vector): Vector = towards(
        x + other.x,
        y + other.y
    )

    operator fun minus(other: Vector): Vector = towards(
        x - other.x,
        y - other.y
    )

    operator fun times(factor: Double): Vector = towards(
        x * factor,
        y * factor
    )

    operator fun div(factor: Double): Vector = towards(
        x / factor,
        y / factor
    )

    companion object {
        operator fun Double.times(vector: Vector): Vector = towards(
            this * vector.x,
            this * vector.y
        )
        operator fun Double.div(vector: Vector): Vector = towards(
            this / vector.x,
            this / vector.y
        )

        fun towards(x: Double, y: Double): Vector =
            Impl(x, y)

        fun unit(direction: Double): Vector = towards(
            cos(direction),
            -sin(direction)
        )

        fun of(magnitude: Double, direction: Double): Vector = towards(
            magnitude * cos(direction),
            magnitude * -sin(direction)
        )

        fun Graphics.vector(from: Point, vector: Vector) {
            val to = from + vector
            line(from, to)
            val magnitude = vector.magnitude / 4.0
            val direction = vector.direction + PI
            line(to, to + of(magnitude, direction + 30.0.degrees))
            line(to, to + of(magnitude, direction - 30.0.degrees))
        }

        fun Graphics.Path.line(vector: Vector) =
            line(vector.x, vector.y)

        val Graphics.TransformationMatrix.translation: Vector
            get() = towards(translationX, translationY)

        val Graphics.TransformationMatrix.scale: Vector
            get() = towards(scaleX, scaleY)

        val Graphics.TransformationMatrix.shear: Vector
            get() = towards(scaleX, scaleY)

        private fun angle(x: Double, y: Double): Double =
            -kotlin.math.atan2(y, x)
    }

    private data class Impl(override val x: Double, override val y: Double) : Vector

    sealed interface Mutable : Vector {

        override var x: Double
        override var y: Double

        override var magnitude: Double
            get() = hypot(x, y)
            set(value) {
                set(value, direction)
            }
        override var direction: Double
            get() = angle(x, y)
            set(value) {
                set(magnitude, value)
            }

        fun addInplace(magnitude: Double, direction: Double) {
            x += magnitude * cos(direction)
            y += magnitude * -sin(direction)
        }

        fun set(magnitude: Double, direction: Double) {
            x = magnitude * cos(direction)
            y = magnitude * -sin(direction)
        }

        operator fun plusAssign(other: Vector) {
            x += other.x
            y += other.y
        }

        operator fun minusAssign(other: Vector) {
            x -= other.x
            y -= other.y
        }

        operator fun timesAssign(factor: Double) {
            x *= factor
            y *= factor
        }

        operator fun divAssign(factor: Double) {
            x /= factor
            y /= factor
        }

        fun setToZero() {
            x = 0.0
            y = 0.0
        }

        companion object {
            fun empty(): Mutable =
                Impl(0.0, 0.0)
        }

        private data class Impl(override var x: Double, override var y: Double) : Mutable
    }
}