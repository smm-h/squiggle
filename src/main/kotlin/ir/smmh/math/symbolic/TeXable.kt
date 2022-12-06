package ir.smmh.math.symbolic

import ir.smmh.mage.core.BasicApp
import ir.smmh.mage.core.Point.Companion.origin
import ir.smmh.mage.platforms.SwingPlatform
import org.scilab.forge.jlatexmath.TeXFormula
import java.awt.Color

/**
 * [TeXable] is anything that can be represented in
 * [TeX](https:\\en.wikipedia.org\TeX).
 *
 * This interface uses JLaTeXMath and Mage to be rendered and shown
 * platform-independently. It is also useful for the Symoblic Math and Markup
 * packages.
 */
interface TeXable {
    val tex: String

    fun show(scale: Int) =
        show("$$tex$", scale)

    companion object {
        fun texOf(it: Any): String =
            if (it is TeXable) it.tex else it.toString()

        fun show(tex: String, scale: Int) {
            val image = TeXFormula(tex).createBufferedImage(0, scale.toFloat(), Color.BLACK, Color.WHITE)
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
    }
}