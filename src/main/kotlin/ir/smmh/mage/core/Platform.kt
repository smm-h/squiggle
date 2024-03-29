package ir.smmh.mage.core

/**
 * Platform provides an abstract way to work with specific devices and their
 * graphics.
 *
 * Platforms would include: Swing, JavaFx, Android, and with a DSL for behavior,
 * PyGame and GameMaker as well.
 */
interface Platform {

    val identityMatrix: Graphics.TransformationMatrix

    val screenSize: Size

    /**
     * Creates a new [Process] for this [App] on this [Platform].
     */
    fun createProcess(dispatch: Event.Dispatch, draw: Graphics.Draw): Process

    /**
     * Creates an empty [Graphics.Path] that can be efficiently drawn on the
     * [Graphics] of this [Platform].
     */
    fun createPath(): Graphics.Path

    /**
     * Creates an empty [Graphics.TransformationMatrix] that can efficiently
     * transform a [Graphics.Path] of this [Platform].
     */
    fun createTransformationMatrix(): Graphics.TransformationMatrix

    /**
     * Creates an empty [Graphics] that can be drawn on
     */
    fun createGraphics(size: Size): Graphics

    fun createColor(hue: Float, saturation: Float, brightness: Float): Color.Packed

    fun createColor(hue: Float) = createColor(hue, 1F, 1F)

    fun renderTeX(tex: String, scale: Float, foreground: Color, background: Color): Image
}