package ir.smmh.mage.pacman

import ir.smmh.mage.core.Color
import ir.smmh.mage.core.Graphics
import ir.smmh.mage.core.Point
import ir.smmh.mage.core.Point.Companion.circle
import ir.smmh.mage.core.Point.Companion.times
import ir.smmh.mage.core.Temporal
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Agent(private val world: World) : Temporal.AndVisual() {

    init {
        ageSpeed = 0.1
    }

    override fun update() {
        var decision: Action? = null
        if (position == world.finish) {
            finished = true
        } else {
            var choices = 0
            val notVisited: MutableSet<Action> = HashSet()
            for (a in Action.values()) {
                val p = world.perceive(position, a)
                if (!p.isWall) {
                    choices++
                    if (p.point !in visited) {
                        notVisited.add(a)
                    }
                }
            }
            if (choices > 0) {
                if (notVisited.isEmpty()) {
                    if (recent.isNotEmpty()) {
                        decision = recent.pop().undo()
                    }
                } else {
                    // Prioritize them by their distance to the end goal
                    var priority = Float.POSITIVE_INFINITY
                    val iterator: Iterator<Action> = notVisited.iterator()
                    while (iterator.hasNext()) {
                        val d = iterator.next()
                        val p = world.finish.distanceSquared(world.perceive(position, d).point).toFloat()
                        if (priority > p) {
                            priority = p
                            decision = d
                        }
                    }
                    recent.push(decision)
                }
            }
        }
        if (decision == null) {
            enabled = false
            println(if (finished) "Finished!" else "Not finished.")
        } else {
            position = world.perceive(position, decision).point
            visited.add(position)
        }
    }

    private var position: Point = world.start
    private val visited: MutableSet<Point> = ConcurrentHashMap.newKeySet()
    private val recent: Stack<Action> = Stack()
    private var finished = false

    init {
        visited.add(position)
    }

    override fun draw(g: Graphics) {
        g.color = Color.Named.Gray
        visited.forEach {
            g.circle(world.offset + world.scale * (it + Point.OnIdentityLine.Half), world.scale * 0.15)
        }
        g.color = Color.Named.Yellow
        g.circle(world.offset + world.scale * (position + Point.OnIdentityLine.Half), world.scale * 0.45)
    }

    enum class Action {
        R, U, L, D;

        fun undo(): Action = when (this) {
            R -> L
            U -> D
            L -> R
            D -> U
        }

        fun move(point: Point.Mutable) = when (this) {
            R -> point.x++
            U -> point.y--
            L -> point.x--
            D -> point.y++
        }
    }
}