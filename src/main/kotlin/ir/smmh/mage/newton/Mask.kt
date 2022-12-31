package ir.smmh.mage.newton

/* EVENTS:
 * collision
 * mouse on/in mask
 * animation start/end (sprites)
 */

//import kotlin.math.PI
//import kotlin.math.roundToInt
//import kotlin.random.Random
//
//import java.awt.Point as AwtPoint
//
//interface Mask {
//    var mx: Double
//    var my: Double
//
//    val cx: Double get() = x + mx
//    val cy: Double get() = y + my
//    val c: Point get() = AwtPoint(cx, cy)
//
//    val perimeter: Double
//    val area: Double
//
//    fun contains(point: Point): Boolean
//
//    fun randomPointOnEdge(random: Random = Random): Point
//
//    fun drawMask(canvas: AbstractCanvas)
//
//    interface Rectangular : Mask {
//        var width: Double
//        var height: Double
//
//        override val perimeter: Double
//            get() = width * 2 + height * 2
//
//        override val area: Double
//            get() = width * height
//
//        override fun contains(point: Point): Boolean {
//
//        }
//
//        override fun randomPointOnEdge(random: Random): Point {
//            val rx = cx + random.nextDouble(width)
//            val ry = cy + random.nextDouble(height)
//            return java.awt.Point(rx, ry)
//        }
//
//        override fun drawMask(canvas: AbstractCanvas) {
//
//        }
//    }
//
//    interface Circular : Mask {
//        var radius: Double
//        var diameter: Double
//            get() = radius * 2
//            set(value) {
//                radius = value / 2
//            }
//        val origin: Point.Immutable get() = c
//
//        override val perimeter: Double
//            get() = 2 * radius * PI
//
//        override val area: Double
//            get() = MageUtils.sqr(radius) * PI
//
//        override fun contains(point: Point) =
//            origin.distance(point) <= radius
//
//        override fun randomPointOnEdge(random: Random) =
//            origin.add(radius, random.direction())
//
//        override fun drawMask(canvas: AbstractCanvas) {
//            canvas.drawOval(
//                origin.x.roundToInt(),
//                origin.y.roundToInt(),
//                diameter.roundToInt(),
//                diameter.roundToInt()
//            )
//        }
//    }
//}