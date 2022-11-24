package ir.smmh.math.symbolic

import ir.smmh.mage.demos.ImageViewer
import ir.smmh.mage.platforms.SwingPlatform
import org.scilab.forge.jlatexmath.TeXFormula
import java.awt.Color
import java.awt.Image

interface TeXable {
    val render: String

    val image: Image
        get() =
            TeXFormula("$$render$").createBufferedImage(0, 48F, Color.BLACK, Color.WHITE)

    fun show() {
        ImageViewer(SwingPlatform, image).start()
    }
}