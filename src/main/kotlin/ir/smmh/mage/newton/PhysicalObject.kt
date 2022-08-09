package ir.smmh.mage.newton

import ir.smmh.mage.core.Point
import ir.smmh.mage.core.Temporal
import ir.smmh.mage.core.Vector
import ir.smmh.mage.core.Vector.Companion.times
import kotlin.math.PI

/**
 * A collection of matter constrained within a contiguous boundary in space that
 * can move and rotate.
 * @see <a href="https://en.wikipedia.org/wiki/Physical_object">Wikipedia</a>
 */
abstract class PhysicalObject(initialMass: Double, initialPosition: Point) : Temporal.AndVisual() {

    var mass: Double = initialMass //  kg
    val position: Point.Mutable = Point.Mutable.of(initialPosition) // m
    val velocity: Vector.Mutable = Vector.Mutable.empty() // m/s
    val acceleration: Vector.Mutable = Vector.Mutable.empty() // m/s.s

    // https://en.wikipedia.org/wiki/Weight
    // TODO  its weight is the magnitude of the gravity on its mass

    //    val weight: Vector
//        get() = mass * gravity
    var speed: Double // m/s
            by velocity::magnitude
    var direction: Double // radian
            by velocity::direction

    val momentum: Vector // kg.m/s
        get() = mass * velocity

    fun displacement(initial: Point) = // m
        (position - initial).length()

    override fun update() {
        position += velocity
        velocity += acceleration
        // TODO detect collisions
    }

    /**
     * Efficiency can never be equal to or more than one.
     * @see <a href="https://en.wikipedia.org/wiki/Deflection_(physics)">Wikipedia</a>
     */
    fun deflect(angle: Double, efficiency: Double) {
        direction = angle * 2 + PI - direction
        speed *= efficiency
        if (speed < 0.5) {
            speed = 0.0
        }
    }

    abstract fun collidesWith(other: PhysicalObject): Collision.Context?
}
