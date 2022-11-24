package ir.smmh.mage.demos

import ir.smmh.mage.core.BasicApp
import ir.smmh.mage.core.Platform
import ir.smmh.mage.core.Point
import java.awt.Image

class ImageViewer(platform: Platform, val image: Image) : BasicApp(platform) {
    override fun main() {
        addVisual {
            it.image(Point.origin, image)
        }
    }
}