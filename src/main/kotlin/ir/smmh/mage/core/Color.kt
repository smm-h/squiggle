@file:Suppress("unused")

package ir.smmh.mage.core

import ir.smmh.util.ArrayUtils.arrayOf
import kotlin.math.roundToInt

sealed interface Color {

    val r: Int
    val g: Int
    val b: Int
    val a: Int

    val rgb: Int
    val rgba: Int

    sealed interface Packed : Color {
        override fun alphaVariant(a: Int): Packed
    }

    sealed interface Unpacked : Color {
        override val rgb: Int
            get() =
                ((r and full) shl 16) or ((g and full) shl 8) or (b and full)
        override val rgba: Int
            get() =
                rgb or ((a and full) shl 24)

        fun pack(): Packed

        override fun alphaVariant(a: Int): Unpacked = UnpackedInt(r, g, b, a)

        sealed interface Precise : Unpacked {
            val rPrecise: Float
            val gPrecise: Float
            val bPrecise: Float
            val aPrecise: Float
        }
    }

    private data class PackedInt(override val rgba: Int) : Packed {
        // @formatter:off
        override val r: Int get() = rgba shr 16 and full
        override val g: Int get() = rgba shr 8 and full
        override val b: Int get() = rgba and full
        override val a: Int get() = rgba shr 24 and full

        // @formatter:on
        override val rgb: Int get() = rgba and 0x00ffffff
        override fun alphaVariant(a: Int): Packed =
            if (this.a == a) this else PackedInt(rgb or (a shl 24))
    }

    private data class UnpackedInt(
        override val r: Int,
        override val g: Int,
        override val b: Int,
        override val a: Int,
    ) : Unpacked {
        override fun pack() = PackedInt(rgba)
        override fun alphaVariant(a: Int): Unpacked = copy(a = a)
    }

    private data class UnpackedFloat(
        override val rPrecise: Float,
        override val gPrecise: Float,
        override val bPrecise: Float,
        override val aPrecise: Float,
    ) : Unpacked.Precise {
        override val r: Int get() = componentToInt(rPrecise)
        override val g: Int get() = componentToInt(gPrecise)
        override val b: Int get() = componentToInt(bPrecise)
        override val a: Int get() = componentToInt(aPrecise)
        override fun pack() = rgba(r, g, b, a)
        override fun alphaVariant(a: Int): Unpacked = copy(aPrecise = a / 255f)

        companion object {
            private fun componentToInt(c: Float) = (c * 255).roundToInt()
        }
    }

    private class Mixture(vararg colors: Color) : Unpacked.Precise {

        override var rPrecise: Float = (colors.sumOf { it.r }).toFloat()
            private set
        override var gPrecise: Float = (colors.sumOf { it.g }).toFloat()
            private set
        override var bPrecise: Float = (colors.sumOf { it.b }).toFloat()
            private set
        override var aPrecise: Float = (colors.sumOf { it.a }).toFloat()
            private set

        private var maxComponent: Float = 0f
        override val r: Int get() = componentToInt(rPrecise)
        override val g: Int get() = componentToInt(gPrecise)
        override val b: Int get() = componentToInt(bPrecise)
        override val a: Int get() = componentToInt(aPrecise)
        override fun pack() = rgba(r, g, b, a)
        private fun componentToInt(c: Float) =
            if (maxComponent == 0f) 0 else (c / maxComponent * 255).roundToInt()

        private fun setMax() {
            maxComponent = maxOf(rPrecise, gPrecise, bPrecise, aPrecise)
        }

        init {
            setMax()
        }

        fun add(other: Color) {
            rPrecise += other.r
            gPrecise += other.g
            bPrecise += other.b
            aPrecise += other.a
            setMax()
        }

        fun multiply(factor: Float) {
            rPrecise *= factor
            gPrecise *= factor
            bPrecise *= factor
            aPrecise *= factor
            maxComponent *= factor
        }
    }

    fun alphaVariant(a: Int): Color
    fun opaqueVariant(): Color = alphaVariant(full)

    operator fun plus(other: Color): Unpacked.Precise {
        return if (this is Mixture)
            this.also { it.add(other) }
        else if (other is Mixture)
            other.also { it.add(this) }
        else
            Mixture(this, other)
    }

    operator fun times(factor: Number): Unpacked.Precise {
        return (if (this is Mixture) this else Mixture(this))
            .also { it.multiply(factor.toFloat()) }
    }

    /**
     * [Named colors in Wikipedia](https://en.wikipedia.org/wiki/Color_term)
     */
    object Named {
        // @formatter:off
        val Black = gray(none)
        val Gray = gray(half)
        val White = gray(full)

        val DarkRed = rgbNone(r = half)
        val Red = rgbNone(r = full)
        val LightRed = rgbHalf(r = full)
        val DarkCyan = rgbHalf(r = none)
        val Cyan = rgbFull(r = none)
        val LightCyan = rgbFull(r = half)
        val DarkGreen = rgbNone(g = half)
        val Green = rgbNone(g = full)
        val LightGreen = rgbHalf(g = full)
        val DarkMagenta = rgbHalf(g = none)
        val Magenta = rgbFull(g = none)
        val LightMagenta = rgbFull(g = half)
        val DarkBlue = rgbNone(b = half)
        val Blue = rgbNone(b = full)
        val LightBlue = rgbHalf(b = full)
        val DarkYellow = rgbHalf(b = none)
        val Yellow = rgbFull(b = none)
        val LightYellow = rgbFull(b = half)

        val Azure = rgb(none, half, full)
        val Spring = rgb(none, full, half)
        val Violet = rgb(half, none, full)
        val Chartreuse = rgb(half, full, none)
        val Rose = rgb(full, none, half)
        val Orange = rgb(full, half, none)

        val Transparent = Black.alphaVariant(0)
        // @formatter:on

        private fun rgbNone(r: Int = none, g: Int = none, b: Int = none) = rgb(r, g, b)
        private fun rgbHalf(r: Int = half, g: Int = half, b: Int = half) = rgb(r, g, b)
        private fun rgbFull(r: Int = full, g: Int = full, b: Int = full) = rgb(r, g, b)
    }

    object Ranges100 {
        private const val shadeCount = 100

        val BlackWhite =
            range(Named.Black, Named.White, shadeCount)
        val TransparentBlack =
            range(Named.Transparent, Named.Black, shadeCount)
        val TransparentWhite =
            range(Named.Transparent, Named.White, shadeCount)
        val TransparentGray =
            range(Named.Transparent, Named.Gray, shadeCount)
    }

    companion object {
        private const val none = 0
        private const val half = 128
        private const val full = 255

        fun packedInt(rgba: Int): Packed =
            PackedInt(rgba)

        fun unpackedInt(r: Int, g: Int, b: Int, a: Int): Unpacked =
            UnpackedInt(r, g, b, a)

        fun rgba(r: Int, g: Int, b: Int, a: Int): Packed =
            unpackedInt(r, g, b, a).pack()

        fun rgb(r: Int, g: Int, b: Int) =
            rgba(r, g, b, full)

        fun gray(value: Int) =
            rgb(value, value, value)

        private fun mergeComponent(from: Int, to: Int, amount: Float): Int =
            from + ((to - from) * amount).roundToInt()

        fun merge(from: Color, to: Color, amount: Float = 0.5f) = rgba(
            mergeComponent(from.r, to.r, amount),
            mergeComponent(from.g, to.g, amount),
            mergeComponent(from.b, to.b, amount),
            mergeComponent(from.a, to.a, amount),
        )

        fun range(from: Color, to: Color, shades: Int): Array<Packed> = arrayOf(shades + 1) {
            merge(from, to, it.toFloat() / shades)
        }
    }
}
