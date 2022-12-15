package ir.smmh.math.plotting

import ir.smmh.mage.core.Size
import ir.smmh.mage.platforms.SwingPlatform
import kotlin.math.sin

fun main() {
    Plotter(SwingPlatform).apply {
        addSetup {
            size = Size.of(1200, 600)
            panCenter(true)
            var frequency = 1.0
            addTemporal { frequency += 0.05 }
            addPlot { x -> sin(x * frequency) }
        }
        start()
    }
}