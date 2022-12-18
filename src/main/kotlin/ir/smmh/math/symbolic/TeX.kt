package ir.smmh.math.symbolic

import ir.smmh.mage.core.BasicApp
import ir.smmh.mage.core.Color
import ir.smmh.mage.core.Platform
import ir.smmh.mage.core.Point.Companion.origin

object TeX {
    fun show(platform: Platform, tex: String, scale: Float) {
        val g = platform.renderTeX(tex, scale, Color.Named.Black, Color.Named.White)
        BasicApp(platform).apply {
            initially {
                addVisual {
                    it.image(origin, g)
                }
                size = g.size
            }
            start()
        }
    }
}