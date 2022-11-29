package ir.smmh.mage.core

interface Graphics : PlatformSpecific {

    val identityMatrix: TransformationMatrix
    var transformationMatrix: TransformationMatrix

    fun interface Draw : (Graphics) -> Unit

    var size: Size

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

    fun image(x: Double, y: Double, image: Image)

    fun image(point: Point, image: Image) =
        image(point.x, point.y, image)

    fun toImage(): Image

    interface Path : PlatformSpecific {
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
            transform(platform.createTransformationMatrix().apply(transformation))
    }

    interface TransformationMatrix : PlatformSpecific {
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