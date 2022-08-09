package ir.smmh.mage.pacman

import ir.smmh.mage.core.*
import ir.smmh.mage.core.Point.Companion.circle
import ir.smmh.mage.core.Point.Companion.square
import ir.smmh.mage.core.Point.Companion.times
import ir.smmh.mage.pacman.Agent.Action

class World(
    val size: Size,
    val walls: Set<Point>,
    val start: Point,
    val finish: Point,
) : Visual.Abstract() {
    val offset = Point.Mutable.empty()
    var scale = 0.0
    fun perceive(point: Point, action: Action): Percept = Point.Mutable.of(point).run {
        action.move(this)
        Percept(this, walls.contains(this))
    }

    override fun draw(g: Graphics) {
        size.forEach {
            g.color = Color.Ranges100.BlackWhite[if (it in walls) 10 else 100]
            g.square(offset + scale * it, scale)
        }
        g.color = Color.Named.Blue
        g.circle(offset + scale * (finish + Point.OnIdentityLine.Half), scale * 0.45)
    }

    companion object {
        fun fromText(text: String): World {
            var x: Int
            var y = 0
            var w = 0
            val walls: MutableSet<Point> = HashSet()
            val spawn: MutableSet<Point> = HashSet()
            for (line in text.split("\n").toTypedArray()) {
                w = w.coerceAtLeast(line.length)
                x = 0
                for (c in line.toCharArray()) {
                    when (c) {
                        ' ' -> Unit
                        '*' -> walls.add(Point.of(x, y))
                        'x' -> spawn.add(Point.of(x, y))
                        else -> throw Exception("unknown character in map: $c")
                    }
                    x += 1
                }
                y += 1
            }
            val h: Int = y
            if (spawn.size < 2) {
                throw Exception("less than two spawn points")
            } else {
                val list: List<Point> = spawn.toList().shuffled()
                return World(Size.of(w, h), walls, list[0], list[1])
            }
        }
    }

    data class Percept(val point: Point, val isWall: Boolean)
}