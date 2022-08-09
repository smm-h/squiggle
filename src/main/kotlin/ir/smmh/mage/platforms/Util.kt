package ir.smmh.mage.platforms
//
//import android.app.Activity
//import android.content.res.Resources
//import android.graphics.Canvas
//import android.graphics.Paint
//import android.graphics.Rect
//import java.lang.ref.WeakReference
//import kotlin.math.roundToInt
//
//object Util {
//
//    val statusBarHeight: Int
//        get() {
//            var result = 0
//            val resourceId =
//                Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
//            if (resourceId > 0) {
//                result = Resources.getSystem().getDimensionPixelSize(resourceId)
//            }
//            return result
//        }
//
//    private val DENSITY = Resources.getSystem().displayMetrics.density
//    fun dipToPixel(dip: Int): Int {
//        return (dip * DENSITY).roundToInt()
//    }
//
//    fun Paint.getLineHeight(): Float {
//        val m = fontMetrics
//        return m.bottom - m.top
//    }
//
//    fun Paint.getTextWidth(text: String?): Float {
//        return measureText(text)
//    }
//
//    fun drawAlignedText(
//        canvas: Canvas,
//        x: Float,
//        y: Float,
//        s: String,
//        p: Paint,
//        horizontalAlignment: Paint.Align,
//        verticalAlignment: TextVerticalAlignment,
//        useLineHeight: Boolean
//    ) {
//        var j = y
//        p.textAlign = horizontalAlignment
//        val r = Rect()
//        p.getTextBounds(s, 0, s.length, r)
//        val h = if (useLineHeight) p.getLineHeight() else r.height().toFloat()
//        when (verticalAlignment) {
//            TextVerticalAlignment.TOP -> j -= r.top.toFloat()
//            TextVerticalAlignment.MIDDLE -> j -= r.top + h / 2f
//            TextVerticalAlignment.BOTTOM -> j -= r.top + h
//            TextVerticalAlignment.BASELINE -> {}
//        }
//        canvas.drawText(s, x, j, p)
//    }
//
//    enum class TextVerticalAlignment {
//        TOP, MIDDLE, BOTTOM, BASELINE
//    }
//}