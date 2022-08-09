package ir.smmh.mage.demos

import ir.smmh.mage.core.BasicApp
import ir.smmh.mage.core.Color
import ir.smmh.mage.core.Graphics
import ir.smmh.mage.core.Platform
import ir.smmh.mage.core.Point.Companion.line

/**
 * This app demonstrates how by extending [BasicApp] you can create a working
 * app that can listen and respond to events and has its own graphical logic.
 */
class BasicDemo(platform: Platform) : BasicApp(platform) {
    override fun main() {
        val corners = size.getCorners()
        addVisual { g: Graphics ->
            g.color = Color.Named.Blue
            corners.forEach { corner ->
                g.line(corner, mousePoint)
            }
        }
    }
}