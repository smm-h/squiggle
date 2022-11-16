package ir.smmh.mage.core

interface Graphics {

    /**
     * Creates an empty [Path] that can be efficiently drawn on this [Graphics].
     */
    fun createPath(): Path

    /**
     * Creates an empty [TransformationMatrix] that can efficiently transform
     * a [Path] on this [Graphics].
     */
    fun createTransformationMatrix(): TransformationMatrix

    val identityMatrix: TransformationMatrix
    var transformationMatrix: TransformationMatrix

    fun interface Draw : (Graphics) -> Unit

    // val stroke: Stroke
    var fill: Boolean
    var color: Color.Packed
    var alpha: Int
        get() = color.a
        set(value) {
            color = color.alphaVariant(value)
        }

    fun line(x1: Double, y1: Double, x2: Double, y2: Double)
    fun rectangle(x: Double, y: Double, w: Double, h: Double)
    fun ellipse(x: Double, y: Double, w: Double, h: Double)

    fun point(x: Double, y: Double) =
        line(x, y, x, y)

    fun square(x: Double, y: Double, w: Double) =
        rectangle(x, y, w, w)

    fun circle(x: Double, y: Double, r: Double) =
        ellipse(x - r, y - r, r * 2, r * 2)

    fun path(path: Path)

    interface Path {
        var x: Double
        var y: Double
        fun move(x: Double, y: Double) {
            this.x = x
            this.y = y
        }

        fun line(x: Double, y: Double)
        fun quadratic(x1: Double, y1: Double, x2: Double, y2: Double)
        fun bezier(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double)
        fun transform(transformationMatrix: TransformationMatrix)
        fun transform(transformation: TransformationMatrix.() -> Unit) =
            transform(createTransformationMatrix().apply(transformation))

        /**
         * Creates an empty [TransformationMatrix] that can efficiently
         * transform this [Path] on its [Graphics].
         */
        fun createTransformationMatrix(): TransformationMatrix
    }

    interface TransformationMatrix {
        fun translate(x: Double, y: Double)
        val translationX: Double
        val translationY: Double
        fun scale(x: Double, y: Double)
        val scaleX: Double
        val scaleY: Double
        fun shear(x: Double, y: Double)
        val shearX: Double
        val shearY: Double
        fun rotate(radians: Double)
        val inverse: TransformationMatrix?
    }
}