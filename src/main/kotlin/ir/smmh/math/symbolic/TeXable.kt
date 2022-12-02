package ir.smmh.math.symbolic

import ir.smmh.mage.core.BasicApp
import ir.smmh.mage.core.Point.Companion.origin
import ir.smmh.mage.platforms.SwingPlatform
import org.scilab.forge.jlatexmath.TeXFormula
import java.awt.Color

interface TeXable {
    val tex: String

    fun show(scale: Int) {
        val image = TeXFormula("$$tex$").createBufferedImage(0, scale.toFloat(), Color.BLACK, Color.WHITE)
        val g = SwingPlatform.createImage(image)
        BasicApp(SwingPlatform).apply {
            addSetup {
                addVisual {
                    it.image(origin, g)
                }
                size = g.size
            }
            start()
        }
    }

    companion object {
        fun texOf(it: Any): String =
            if (it is TeXable) it.tex else it.toString()
    }
}