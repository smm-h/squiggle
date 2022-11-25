package ir.smmh.mage.demos

import ir.smmh.mage.core.BasicApp
import ir.smmh.mage.core.Platform
import ir.smmh.mage.core.Point
import ir.smmh.mage.core.Size
import java.awt.Image

class ImageViewer(platform: Platform, val image: Image) : BasicApp(platform) {
    override fun main() {
        size = Size.of(image.getWidth(null), image.getHeight(null))
        addVisual { it.image(Point.origin, image) }
    }
}