package ir.smmh.mage.platforms

//import android.annotation.SuppressLint
//import android.graphics.*
//import android.view.MotionEvent
//import android.view.View
//import androidx.core.graphics.values
//import ir.smmh.mage.MainActivity
//import ir.smmh.mage.MainActivity.Companion.mainActivity
//import ir.smmh.mage.core.*
//import ir.smmh.mage.core.Color
//import ir.smmh.mage.core.Utils.toDegrees
//import kotlin.math.roundToInt
//import android.graphics.Color as AndroidColor
//
//object AndroidPlatform : Platform {
//
//    override val identityMatrix: Graphics.TransformationMatrix =
//        AndroidTransformationMatrix()
//
//    override val screenSize: Size
//        get() = Size.OneOne // TODO screen size
//
//    override fun createColor(hue: Float, saturation: Float, brightness: Float): Color.Packed =
//        Color.packedInt(AndroidColor.HSVToColor(FloatArray(hue, saturation, brightness)))
//
//    override fun createGraphics(size: Size): Graphics =
//        AndroidGraphics(size)
//
//    override fun createProcess(dispatch: Event.Dispatch, draw: Graphics.Draw): Process =
//        mainActivity?.let {
//            AndroidProcess(it, dispatch, draw)
//        } ?: throw RuntimeException("MainActivity is null")
//
//    override fun createPath(): Graphics.Path =
//        AndroidPath()
//
//    private class AndroidPath(val path: Path = Path()) : Graphics.Path {
//
//        override val platform: Platform get() = AndroidPlatform
//
//        override var x: Double = 0.0
//            set(value) {
//                field = value
//                move(x, y)
//            }
//        override var y: Double = 0.0
//            set(value) {
//                field = value
//                move(x, y)
//            }
//
//        override fun move(x: Double, y: Double) =
//            path.moveTo(x.toFloat(), y.toFloat())
//
//        override fun line(x: Double, y: Double) =
//            path.lineTo(x.toFloat(), y.toFloat())
//
//        override fun quadratic(x1: Double, y1: Double, x2: Double, y2: Double) =
//            path.quadTo(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat())
//
//        override fun bezier(
//            x1: Double,
//            y1: Double,
//            x2: Double,
//            y2: Double,
//            x3: Double,
//            y3: Double
//        ) =
//            path.cubicTo(
//                x1.toFloat(),
//                y1.toFloat(),
//                x2.toFloat(),
//                y2.toFloat(),
//                x3.toFloat(),
//                y3.toFloat()
//            )
//
//        override fun transform(transformationMatrix: Graphics.TransformationMatrix) =
//            path.transform((transformationMatrix as AndroidTransformationMatrix).matrix)
//
//    }
//
//    private class AndroidTransformationMatrix(val matrix: Matrix = Matrix()) :
//        Graphics.TransformationMatrix {
//
//        override val platform: Platform get() = AndroidPlatform
//
//        override fun translate(x: Double, y: Double) {
//            matrix.preTranslate(x.toFloat(), y.toFloat())
//        }
//
//        override fun scale(x: Double, y: Double) {
//            matrix.preScale(x.toFloat(), y.toFloat())
//        }
//
//        override fun shear(x: Double, y: Double) {
//            matrix.preSkew(x.toFloat(), y.toFloat())
//        }
//
//        override fun rotate(radians: Double) {
//            matrix.preRotate(radians.toDegrees().toFloat())
//        }
//
//        private fun getMatrixContentsAsArray(): FloatArray =
//            matrix.values()
//
//        override val translationX: Double get() = getMatrixContentsAsArray()[4].toDouble() // m02
//        override val translationY: Double get() = getMatrixContentsAsArray()[5].toDouble() // m12
//        override val scaleX: Double get() = getMatrixContentsAsArray()[0].toDouble() // m00
//        override val scaleY: Double get() = getMatrixContentsAsArray()[3].toDouble() // m11
//        override val shearX: Double get() = getMatrixContentsAsArray()[2].toDouble() // m01
//        override val shearY: Double get() = getMatrixContentsAsArray()[1].toDouble() // m10
//
//        override val inverse: Graphics.TransformationMatrix?
//            get() {
//                val inverse = Matrix()
//                return if (matrix.invert(inverse))
//                    AndroidTransformationMatrix(inverse) else null
//            }
//
//    }
//
//    override fun createTransformationMatrix(): Graphics.TransformationMatrix =
//        AndroidTransformationMatrix()
//
//    private class AndroidGraphics(initialSize: Size) : Graphics {
//
//        override var transformationMatrix: Graphics.TransformationMatrix =
//            identityMatrix
//
//        override val platform: Platform get() = AndroidPlatform
//
//        override var size: Size = Size.OneOne
//            set(value) {
//                if (field != value) {
//                    field = value
//                    bitmap = Bitmap.createBitmap(
//                        size.width.roundToInt(),
//                        size.height.roundToInt(),
//                        bitmapConfig
//                    )
//                    canvas = Canvas(bitmap)
//                }
//            }
//
//        init {
//            this.size = initialSize
//        }
//
//        private val bitmapConfig = Bitmap.Config.ARGB_8888
//
//        // TODO make this private
//        var bitmap: Bitmap = Bitmap.createBitmap(1, 1, bitmapConfig)
//        private var canvas: Canvas = Canvas(bitmap)
//
//        private val paint = Paint().apply {
//            style = Paint.Style.STROKE
//            color = AndroidColor.BLACK
//        }
//
//        override var fill: Boolean = false
//            set(value) {
//                if (field != value) {
//                    field = value
//                    paint.style = if (value) Paint.Style.FILL else Paint.Style.STROKE
//                }
//            }
//
//        override var color: Color.Packed
//            get() = Color.packedInt(paint.color)
//            set(value) {
//                paint.color = value.rgba
//            }
//
//        override fun line(x1: Double, y1: Double, x2: Double, y2: Double) =
//            canvas.drawLine(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), paint)
//
//        override fun rectangle(x: Double, y: Double, w: Double, h: Double) =
//            canvas.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), paint)
//
//        override fun ellipse(x: Double, y: Double, w: Double, h: Double) =
//            canvas.drawOval(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), paint)
//
//        override fun path(path: Graphics.Path) =
//            canvas.drawPath((path as AndroidPath).path, paint)
//
//        override fun image(x: Double, y: Double, image: Image) =
//            canvas.drawBitmap((image as AndroidImage).bitmap, x.toFloat(), y.toFloat(), paint)
//
//        override fun toImage(): Image =
//            AndroidImage(Bitmap.createBitmap(bitmap))
//    }
//
//    private class AndroidImage(val bitmap: Bitmap) : Image {
//        override val platform: Platform get() = AndroidPlatform
//        override val size: Size by lazy { Size.of(bitmap.width, bitmap.height) }
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private class AndroidProcess(
//        val mainActivity: MainActivity,
//        override var dispatch: Event.Dispatch,
//        override val draw: Graphics.Draw,
//    ) : Process {
//
//        override val platform: Platform get() = AndroidPlatform
//
//        private val view = object : View(mainActivity) {
//            override fun onDraw(canvas: Canvas) {
//                super.onDraw(canvas)
//                this@AndroidProcess.draw(graphics)
//                canvas.drawBitmap(graphics.bitmap, 0f, 0f, null)
//                invalidate()
//            }
//        }
//
//        init {
//            mainActivity.setContentView(view)
//            view.setOnTouchListener { _, e ->
//                dispatch(Event.Mouse.Moved.happen(e.point))
//                true
//            }
////            isFocusable = true // make sure we get key events
//        }
//
////    override fun onKeyUp/Down(keyCode: Int, msg: KeyEvent): Boolean
//
//        override var title: String
//            get() = mainActivity.title as String
//            set(value) {
//                mainActivity.title = value
//            }
//
//        override val graphics = AndroidGraphics(Size.OneOne)
//
//        override var size: Size by graphics::size
//
//        override fun stop() {}
//
//        override fun screenshot(address: String) {
//            // TODO
//        }
//    }
//
//    private val MotionEvent.point get() = Point.of(x, y)
//}