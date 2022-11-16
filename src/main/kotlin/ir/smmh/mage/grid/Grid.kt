package ir.smmh.mage.grid

import ir.smmh.mage.core.*
import ir.smmh.mage.core.Point.Companion.gridLines
import kotlin.math.hypot

class Grid(platform: Platform) : App(platform) {

    private val mainTemporalGroup = Temporal.Group.List<Temporal>()
    private val mainVisualGroup = Visual.Group.List<Visual>()

    fun addTemporal(lambda: () -> Unit) =
        mainTemporalGroup.add(Temporal.Lambda(lambda))

    fun addVisual(lambda: Graphics.() -> Unit) =
        mainVisualGroup.add(Visual.Lambda(lambda))

    var gridSize: Size = Size.of(32)
    var mousePoint: Point = Point.origin

    override fun setup() {

        fps = 30.0
        val height = 640
        size = Size.of(height * 1080 / 1920, height)
        title = "Grid"

        temporalRoot.add(mainTemporalGroup)
        visualRoot.add(mainVisualGroup)

        on(Event.Window.CloseButton) { exit() }
        on(Event.Key.Released("ESCAPE")) { exit() }
        on(Event.Key.Released("R")) { restart() }
        on(Event.Mouse.Moved) { mousePoint = it }

        addVisual {
//            color = Color.Named.Black
//            fill = true
//            rectangle(0.0, 0.0, size.width, size.height)

            transformationMatrix = identityMatrix
            transformationMatrix = createTransformationMatrix().apply {
//                translate(mousePoint)
                val s = hypot(mousePoint.x, mousePoint.y) * 0.01
                scale(s, s)
            }

            color = Color.Named.White
            fill = false
            gridLines(size, gridSize)
        }
    }
}