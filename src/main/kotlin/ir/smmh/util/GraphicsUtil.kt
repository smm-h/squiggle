package ir.smmh.util

import java.awt.Color

object GraphicsUtil {
    fun changeTransparency(color: Color, transparency: Float): Color {
        return Color(color.rgb and 0x00ffffff or getAlpha(transparency), true)
    }

    fun getAlpha(transparency: Float): Int {
        return (transparency * 255f).toInt() shl 24
    }

    fun hueToRgb(h: Float, s: Float, v: Float): Int {
        return Color.getHSBColor(h, s, v).rgb
    }
}