package ir.smmh.mage.newton

import ir.smmh.mage.core.Event
import ir.smmh.mage.core.Temporal

object Collision : Event<Triple<PhysicalObject, PhysicalObject, Collision.Context>>("Collision") {
    interface Context

    /**
     * A [Collision.Checker] checks to see if any of [these], [PhysicalObject]s,
     * collides with any of [those] ones, at any given time. If such a collision
     * occurs, it will dispatch a [Collision] event with a collision [Context]
     * that may hold more details regarding the collision.
     */
    class Checker(
        val dispatch: Dispatch,
        val these: Iterable<PhysicalObject>,
        val those: Iterable<PhysicalObject>,
    ) : Temporal() {
        override fun update() {
            these.forEach { collider ->
                those.forEach { collidee ->
                    collider.collidesWith(collidee)?.also { context ->
                        dispatch(Collision.happen(Triple(collider, collidee, context)))
                    }
                }
            }
        }
    }
}