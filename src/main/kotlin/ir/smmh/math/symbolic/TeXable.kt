package ir.smmh.math.symbolic

import ir.smmh.mage.demos.ImageViewer
import ir.smmh.mage.platforms.SwingPlatform
import org.scilab.forge.jlatexmath.TeXFormula
import java.awt.Color

interface TeXable {
    val render: String

    fun show(size: Int) {
        ImageViewer(
            SwingPlatform,
            TeXFormula("$$render$").createBufferedImage(0, size.toFloat(), Color.BLACK, Color.WHITE)
        ).start()
    }
}