package ir.smmh.mage.newton

//import ir.smmh.mage.core.AbstractCanvas
//import ir.smmh.mage.core.Drawable
//import ir.smmh.mage.core.Utils.sqr
//import ir.smmh.mage.newton.Universe.gravitationalConstant
//import java.util.concurrent.ConcurrentHashMap
//import kotlin.math.sqrt
//
//object SolarSystem : Game() {
//
//    @JvmStatic
//    fun main(args: Array<String>) {
//        SolarSystem.start()
//    }
//
//    val bodies: MutableSet<PhysicalObject> = ConcurrentHashMap.newKeySet()
//    override fun setup() {
//        super.setup()
//
//        mainTemporalGroup.add { canvas ->
//
//        }
//    }
//
//
//    interface Planet : PhysicalObject, Drawable {
//        companion object {
//            // https://en.wikipedia.org/wiki/Gravity_of_Earth
//            // TODO 9.80665 m/s.s
//            val Earth: Planet =
//                Impl()
//        }
//
//        private class Impl : PhysicalObject.Impl(), Planet {
//            val radius: Double get() = sqrt(mass)
//            override fun draw(canvas: AbstractCanvas) {
//                canvas.drawOval(
//                    position.x - radius,
//                    position.y - radius,
//                    position.x + radius,
//                    position.y + radius,
//                )
//            }
//
//            override fun update() {
//                super<Planet>.update()
//                acceleration.setToZero()
//                bodies.forEach { other ->
//                    if (this != other) {
//                        val distance = position.distance(other.position)
//                        val force = gravitationalConstant * mass * other.mass / sqr(distance)
//                        acceleration.addInplace(force, direction)
//                    }
//                }
//            }
//        }
//    }
//}
