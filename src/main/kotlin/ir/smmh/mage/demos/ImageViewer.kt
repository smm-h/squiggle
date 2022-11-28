package ir.smmh.mage.demos

import ir.smmh.mage.core.BasicApp
import ir.smmh.mage.core.Platform
import ir.smmh.mage.core.Point
import ir.smmh.mage.core.Size
import java.awt.Image
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.util.concurrent.CopyOnWriteArrayList

class ImageViewer(platform: Platform, image: Image?) : BasicApp(platform) {

    val images: MutableList<Image> = CopyOnWriteArrayList()

    init {
        if (image != null) images.add(image)
        addSetup { addVisual { for (i in images) it.image(Point.origin, i) } }
    }

    companion object {
        val Image.size: Size get() = Size.of(getWidth(null), getHeight(null))

        fun BufferedImage.flip(horizontally: Boolean = false, vertically: Boolean = false): BufferedImage {
            val t = AffineTransform.getScaleInstance(
                if (horizontally) -1.0 else 1.0,
                if (vertically) -1.0 else 1.0
            )
            t.translate(
                if (horizontally) -getWidth(null).toDouble() else 0.0,
                if (vertically) -getHeight(null).toDouble() else 0.0
            )
            return AffineTransformOp(t, AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter(this, null)
        }
    }
}