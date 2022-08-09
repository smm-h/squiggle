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
//import ir.smmh.mage.core.Point
//import ir.smmh.mage.core.Utils.radiansToDegrees
//import kotlin.math.roundToInt
//
//object AndroidPlatform : Platform {
//    override fun createProcess(dispatch: Event.Dispatch, draw: Graphics.Draw): Process = mainActivity?.let {
//        AndroidProcess(it, dispatch, draw)
//    } ?: throw RuntimeException("MainActivity is null")
//
//    override fun createPath(): Graphics.Path =
//        AndroidPath()
//
//    private class AndroidPath(val path: Path = Path()) : Graphics.Path {
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
//        override fun bezier(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double) =
//            path.cubicTo(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), x3.toFloat(), y3.toFloat())
//
//        override fun transform(transformationMatrix: Graphics.TransformationMatrix) =
//            path.transform((transformationMatrix as AndroidTransformationMatrix).matrix)
//
//        override fun createTransformationMatrix(): Graphics.TransformationMatrix =
//            AndroidTransformationMatrix()
//
//    }
//
//    private class AndroidTransformationMatrix(val matrix: Matrix = Matrix()) :
//        Graphics.TransformationMatrix {
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
//            matrix.preRotate(radians.radiansToDegrees().toFloat())
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
//    @SuppressLint("ClickableViewAccessibility")
//    private class AndroidProcess(
//        val mainActivity: MainActivity,
//        override var dispatch: Event.Dispatch,
//        override val draw: Graphics.Draw,
//    ) : Process, Graphics {
//
//        override val platform: Platform = AndroidPlatform
//
//        private val view = object : View(mainActivity) {
//            override fun onDraw(canvas: Canvas) {
//                super.onDraw(canvas)
//                this@AndroidProcess.draw(graphics)
//                canvas.drawBitmap(bitmap, 0f, 0f, null)
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
//        override val graphics: Graphics = this
//
//        override fun stop() {}
//
//        private val bitmapConfig = Bitmap.Config.ARGB_8888
//        private var bitmap: Bitmap = Bitmap.createBitmap(1, 1, bitmapConfig)
//        private var canvas: Canvas = Canvas(bitmap)
//
//        private val paint = Paint().apply {
//            style = Paint.Style.STROKE
//            color = android.graphics.Color.BLACK
//        }
//
//        override fun createPath(): Graphics.Path =
//            AndroidPlatform.createPath()
//
//        override fun createTransformationMatrix(): Graphics.TransformationMatrix =
//            AndroidPlatform.createTransformationMatrix()
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
//    }
//
//    private val MotionEvent.point get() = Point.of(x, y)
//}