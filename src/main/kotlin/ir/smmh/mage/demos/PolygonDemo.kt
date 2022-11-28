package ir.smmh.mage.demos

import ir.smmh.mage.core.*
import ir.smmh.mage.core.Point.Companion.lines
import ir.smmh.mage.core.Point.Companion.move
import ir.smmh.mage.core.Utils.degrees
import ir.smmh.mage.core.Utils.fullCircle

open class PolygonDemo(platform: Platform) : BasicApp(platform) {
    init {
        addSetup {
            regularStar(100.0, 50.0, 5).also {
                add(it)
                addTemporal {
                    it.translation = mousePoint
                    it.rotation += 1.0.degrees
                }
            }
        }
    }

    fun regularStar(outerRadius: Double, innerRadius: Double, count: Int, rotation: Double = 0.0): PathAnimation =
        Polygon(mutableListOf<Point>().apply {
            var angle = rotation
            val angleStep = fullCircle / count / 2
            (0 until count).forEach { _ ->
                angle += angleStep; add(Vector.of(outerRadius, angle).point)
                angle += angleStep; add(Vector.of(innerRadius, angle).point)
            }
        })

    fun regularPolygon(radius: Double, count: Int, rotation: Double = 0.0): PathAnimation =
        Polygon((0 until count).map { Vector.of(radius, rotation + it * fullCircle / count).point })

    private inner class Polygon(list: List<Point>) : PathAnimation() {

        private val points: MutableList<Point>

        init {
            points = if (list is MutableList) list else list.toMutableList()
            points.add(points[0])
        }

        override fun makePath(path: Graphics.Path) {
            path.move(points[0])
            path.lines(points)
        }
    }
}