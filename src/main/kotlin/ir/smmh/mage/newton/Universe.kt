package ir.smmh.mage.newton

import ir.smmh.mage.core.Point
import ir.smmh.mage.core.Vector

object Universe {
    /**
     * @see <a href="https://en.wikipedia.org/wiki/Gravitational_constant">Wikipedia</a>
     */
    const val gravitationalConstant: Double = 6.6743e-11 // N.m.m/kg.kg

    /**
     * @see <a href="https://en.wikipedia.org/wiki/Frame_of_reference">Wikipedia</a>
     */
    interface FrameOfReference {

        val objects: Set<PhysicalObject>

        val origin: Point
        val x: Point
        val y: Point

        // TODO gettable
        val orientation: Double
        val scale: Double

        /**
         * @see <a href="https://en.wikipedia.org/wiki/Inertial_frame_of_reference">Wikipedia</a>
         */
        interface Inertial : FrameOfReference

        /**
         * @see <a href="https://en.wikipedia.org/wiki/Non-inertial_reference_frame">Wikipedia</a>
         */
        interface NonInertial : FrameOfReference {
            val acceleration: Vector
        }

        /**
         * @see <a href="https://en.wikipedia.org/wiki/Rotating_reference_frame">Wikipedia</a>
         */
        interface Rotating : NonInertial
    }
}