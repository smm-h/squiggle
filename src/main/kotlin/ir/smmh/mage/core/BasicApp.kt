@file:Suppress("MemberVisibilityCanBePrivate")

package ir.smmh.mage.core

import ir.smmh.mage.core.Point.Companion.gridDots
import ir.smmh.mage.core.Point.Companion.gridLines
import ir.smmh.mage.core.Point.Companion.move
import ir.smmh.mage.core.Point.Companion.translate
import ir.smmh.util.LanguageUtils.late

/**
 * - It is debuggable
 * - It follows the
 * [Principle of least astonishment](https://en.wikipedia.org/wiki/Principle_of_least_astonishment)
 * which says that programs should not surprise the users. In other words, it
 * has what is known as "sensible defaults".
 */
abstract class BasicApp(platform: Platform) : App(platform) {

    private val mainTemporalGroup = Temporal.Group.List<Temporal>()
    private val mainVisualGroup = Visual.Group.List<Visual>()
    private val debugTemporalGroup = Temporal.Group.List<Temporal>()
    private val debugVisualGroup = Visual.Group.List<Visual>()

    private val setups: MutableList<() -> Unit> = ArrayList()

    fun add(it: Any) {
        var added = false
        if (it is Temporal) {
            mainTemporalGroup.add(it)
            println("Added to main temporal group")
            added = true
        }
        if (it is Visual) {
            mainVisualGroup.add(it)
            println("Added to main visual group")
            added = true
        }
        if (it is Finalizable) {
            finally(it)
            println("Added to finalization stack")
            added = true
        }
        if (!added) {
            throw Exception("nothing was added")
        }
    }

    fun addSetup(lambda: () -> Unit) =
        setups.add(lambda)

    fun addTemporal(lambda: () -> Unit) =
        mainTemporalGroup.add(Temporal.Lambda(lambda))

    fun addVisual(lambda: (Graphics) -> Unit) =
        mainVisualGroup.add(Visual.Lambda(lambda))

    fun addFinalizable(lambda: Finalizable) =
        finally(lambda)

    var debugMode = false
        set(value) {
            if (field != value) {
                field = value
                mainTemporalGroup.enabled = !debugMode
                debugTemporalGroup.enabled = debugMode
                debugVisualGroup.visible = debugMode
                println("Debug mode ${if (debugMode) "on" else "off"}")
            }
        }

    var mousePoint: Point = Point.origin
    var gridSize: Size = Size.of(32)
    var gridLinesColor: Color.Packed = Color.Ranges100.TransparentBlack[90]
    var gridDotsColor: Color.Packed = Color.Ranges100.TransparentBlack[50]
    var backColor: Color.Packed = Color.gray(222)

    final override fun setup() {
        fps = 60.0
        size = Size.of(640, 480)
        title = ""

        debugTemporalGroup.enabled = false
        debugVisualGroup.visible = false

        temporalRoot.add(debugTemporalGroup)
        visualRoot.add(debugVisualGroup)

        temporalRoot.add(mainTemporalGroup)
        visualRoot.add(mainVisualGroup)

        on(Event.Window.CloseButton) { exit() }
        on(Event.Key.Released("ESCAPE")) { exit() }
        on(Event.Key.Released("R")) { restart() }
        on(Event.Key.Released("F5")) { debugMode = !debugMode }
        on(Event.Mouse.Moved) { mousePoint = it }

        addVisual { g ->
            g.color = backColor
            g.fill = true
            g.rectangle(0.0, 0.0, size.width, size.height)
        }

        val grid = { g: Graphics ->
            g.color = gridLinesColor
            g.gridLines(size, gridSize)
            g.color = gridDotsColor
            g.gridDots(size, gridSize)
        }

        debugVisualGroup.add(Visual.Lambda(grid))

        debugMode = false

        for (s in setups) s()
    }

    // val makePath: Graphics.Path.() -> Unit
    // TODO remove 'inner'
    abstract inner class PathAnimation : Temporal.AndVisual() {

        var color: Color.Packed = Color.Named.Black
        var fill: Boolean = false
        var translation: Point = Point.origin
        var scale: Double = 1.0
        var rotation: Double = 0.0

        private var path: Graphics.Path by late { remakePath() }

        abstract fun makePath(path: Graphics.Path)

        private fun remakePath(): Graphics.Path =
            platform.createPath().apply {
                move(Point.origin)
                makePath(this)
                transform {
                    translate(translation)
                    rotate(rotation)
                    scale(scale, scale)
                }
            }

        override fun draw(g: Graphics) {
            g.color = color
            g.fill = fill
            g.path(path)
        }

        override fun update() {
            path = remakePath()
        }
    }
}