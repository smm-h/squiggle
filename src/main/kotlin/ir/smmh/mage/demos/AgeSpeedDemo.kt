package ir.smmh.mage.demos

import ir.smmh.mage.core.*
import ir.smmh.mage.core.Point.Companion.circle
import ir.smmh.mage.core.Utils.degrees
import ir.smmh.mage.core.Utils.direction
import ir.smmh.mage.core.Vector.Companion.vector
import ir.smmh.mage.newton.Collision
import ir.smmh.mage.newton.PhysicalObject
import kotlin.random.Random

class AgeSpeedDemo(platform: Platform) : BasicApp(platform) {

    private val mouseInnerRadius = 30.0
    private val mouseOuterRadius = 120.0
    private val ballBlur = 0
    private val ballColor = Color.Named.Yellow
    private val ballBlurColor = ballColor.alphaVariant(16)

    init {
        initially {
            backColor = Color.Named.Black
            val balls = Temporal.AndVisual.Group.List<Ball>()
            repeat(30) {
                balls.add(Ball(size.randomPoint()))
            }
            add(balls)
            addVisual { g ->
                g.color = Color.Ranges100.TransparentWhite[75]
                g.fill = false
                g.circle(mousePoint, mouseInnerRadius)
                g.circle(mousePoint, mouseOuterRadius)
            }
            addTemporal {
//            var count = 0
                balls.forEach {
                    val distance = it.position.distance(mousePoint)
                    it.ageSpeed = if (distance > mouseOuterRadius) 1.0
                    else if (distance < mouseInnerRadius) 0.0 // (0.0).also { count++ }
                    else (distance - mouseInnerRadius) / (mouseOuterRadius - mouseInnerRadius)
                }
//            println(count)
            }
        }
    }

    private inner class Ball(initialPosition: Point) : PhysicalObject(1.0, initialPosition) {

        private val radius = 10.0
        private val q = ArrayDeque<Point>()

        init {
            // F/m = a
            acceleration += Vector.of(0.0981, 270.0.degrees)
//            acceleration *= 1 / mass
            direction = Random.direction()
            speed = 1.0
        }

        override fun update() {
            super.update()
            if (position outsideHorizontally size) deflect(90.0.degrees, 0.7)
            if (position outsideVertically size) deflect(180.0.degrees, 0.7)
        }

        override fun collidesWith(other: PhysicalObject): Collision.Context? {
            // TODO
            return null
        }

        override fun draw(g: Graphics) {
            q.add(Point.of(position))
            if (q.size >= ballBlur) q.removeFirst()
            g.fill = true
            g.color = ballBlurColor
            q.forEach {
                g.circle(it, radius)
            }
            g.color = ballColor
            g.circle(position, radius)
            g.color = Color.Named.LightGreen
            g.vector(position, velocity * 8.0)
            g.color = Color.Named.LightRed
            g.vector(position, acceleration * 400.0)
        }
    }

    companion object {

        infix fun Point.outside(size: Size): Boolean =
            this outsideHorizontally size || this outsideVertically size

        infix fun Point.outsideHorizontally(size: Size): Boolean =
            y <= 0 || y >= size.height

        infix fun Point.outsideVertically(size: Size): Boolean =
            x <= 0 || x >= size.width
    }
}