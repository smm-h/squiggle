package ir.smmh.mage.platforms

import ir.smmh.mage.core.*
import ir.smmh.mage.core.Event.Companion.happen
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Toolkit
import java.awt.event.*
import java.awt.geom.AffineTransform
import java.awt.geom.NoninvertibleTransformException
import java.awt.geom.Path2D
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.system.exitProcess


object SwingPlatform : Platform {

    override val screenSize: Size = dimensionToSize(Toolkit.getDefaultToolkit().screenSize)

    private fun dimensionToSize(d: Dimension) = Size.of(d.width, d.height)

    override fun createProcess(dispatch: Event.Dispatch, draw: Graphics.Draw): Process =
        SwingProcess(dispatch, draw)

    private class SwingProcess(
        override val dispatch: Event.Dispatch,
        override val draw: Graphics.Draw,
    ) : Process {

        override val platform: Platform = SwingPlatform

        private val frame = JFrame()
        private val panel = object : JPanel(null) {
            override fun paint(g: java.awt.Graphics) {
                super.paint(g)
                g.color = java.awt.Color.BLUE
                g.fillRect(0, 0, size.width, size.height)
                this@SwingProcess.draw(this@SwingProcess.graphics)
                g.drawImage(this@SwingProcess.graphics.image, 0, 0, null)
                repaint()
            }
        }

        override var title: String
            get() = frame.title
            set(value) {
                if (value != title) {
                    frame.title = value
                }
            }
        override var size: Size = Size.OneOne
            set(value) {
                if (field != value && value.isAcceptable()) {
                    field = value
                    val i = frame.insets
                    val w = value.width.toInt() + i.left + i.right
                    val h = value.height.toInt() + i.top + i.bottom
                    frame.size = Dimension(w, h)
                    frame.setLocation(
                        ((screenSize.width - w) / 2).toInt(),
                        ((screenSize.height - h) / 2).toInt(),
                    )
                    graphics.size = value
                }
            }

        private fun Size.isAcceptable() = width > 0 && height > 0

        override val graphics = SwingGraphics(Size.OneOne)

        init {
            frame.isResizable = false
            frame.addWindowListener(object : WindowListener {
                override fun windowOpened(e: WindowEvent) = dispatch(Event.Window.Opened.happen())
                override fun windowClosing(e: WindowEvent) = dispatch(Event.Window.CloseButton.happen())
                override fun windowClosed(e: WindowEvent) = Unit
                override fun windowIconified(e: WindowEvent) = dispatch(Event.Window.Iconified.happen())
                override fun windowDeiconified(e: WindowEvent) = dispatch(Event.Window.Deiconified.happen())
                override fun windowActivated(e: WindowEvent) = dispatch(Event.Window.Activated.happen())
                override fun windowDeactivated(e: WindowEvent) = dispatch(Event.Window.Deactivated.happen())
            })
            frame.addWindowFocusListener(object : WindowFocusListener {
                override fun windowGainedFocus(e: WindowEvent) = dispatch(Event.Window.GainedFocus.happen())
                override fun windowLostFocus(e: WindowEvent) = dispatch(Event.Window.LostFocus.happen())
            })
            @Suppress("ObjectLiteralToLambda")
            frame.addWindowStateListener(object : WindowStateListener {
                override fun windowStateChanged(e: WindowEvent) = dispatch(Event.Window.StateChanged.happen())
            })
            frame.addKeyListener(object : KeyListener {
                override fun keyTyped(e: KeyEvent) =
                    dispatch(Event.Key.Typed(KeyEvent.getKeyText(e.keyCode)).happenVia(e))

                override fun keyPressed(e: KeyEvent) =
                    dispatch(Event.Key.Pressed(KeyEvent.getKeyText(e.keyCode)).happenVia(e))

                override fun keyReleased(e: KeyEvent) =
                    dispatch(Event.Key.Released(KeyEvent.getKeyText(e.keyCode)).happenVia(e))
            })
            panel.addMouseListener(object : MouseListener {
                override fun mouseClicked(e: MouseEvent) =
                    dispatch(Event.Mouse.Button.Clicked.of(e.button).happen(e.mousePoint))

                override fun mousePressed(e: MouseEvent) =
                    dispatch(Event.Mouse.Button.Pressed.of(e.button).happen(e.mousePoint))

                override fun mouseReleased(e: MouseEvent) =
                    dispatch(Event.Mouse.Button.Released.of(e.button).happen(e.mousePoint))

                override fun mouseEntered(e: MouseEvent) = Unit
                override fun mouseExited(e: MouseEvent) = Unit
            })
            panel.addMouseMotionListener(object : MouseMotionListener {
                override fun mouseDragged(e: MouseEvent) = Unit
                override fun mouseMoved(e: MouseEvent) = dispatch(Event.Mouse.Moved.happen(e.mousePoint))
            })
            frame.addMouseWheelListener { e ->
                val rotation = e.preciseWheelRotation
                val wheel = if (rotation < 0) Event.Mouse.Wheel.Up else Event.Mouse.Wheel.Down
                dispatch(wheel.happen(abs(rotation)))
            }
            frame.add(panel)
            frame.isVisible = true
        }

        override fun stop() {
            frame.isVisible = false
            exitProcess(0)
        }

        override fun screenshot(address: String) {
            ImageIO.write(graphics.image, "png", File(address))
        }
    }

    fun createImage(image: java.awt.Image): Image = SwingImage(image)

    private class SwingImage(val image: java.awt.Image) : ir.smmh.mage.core.Image {
        override val platform = SwingPlatform
        override val size: Size by lazy { Size.of(image.getWidth(null), image.getHeight(null)) }
    }

    override fun createGraphics(size: Size): Graphics = SwingGraphics(size)

    private class SwingGraphics(
        initialSize: Size,
        val imageType: Int = BufferedImage.TYPE_INT_ARGB,
    ) : Graphics {

        override val platform = SwingPlatform

        var image = BufferedImage(1, 1, imageType)
        private var graphics: Graphics2D = image.graphics as Graphics2D

        override var size: Size
            get() = Size.of(image.getWidth(null), image.getHeight(null))
            set(value) {
                image = BufferedImage(
                    value.width.roundToInt(),
                    value.height.roundToInt(),
                    imageType
                )
                graphics = image.graphics as Graphics2D
            }

        init {
            size = initialSize
        }

        override val identityMatrix: Graphics.TransformationMatrix = createTransformationMatrix()
        override var transformationMatrix: Graphics.TransformationMatrix = createTransformationMatrix()
            set(value) {
                field = value
                graphics.transform = (value as SwingTransformationMatrix).affineTransform
            }

        override var fill: Boolean = false
        override var color: Color.Packed
            get() = Color.packedInt(graphics.color.rgb)
            set(value) {
                graphics.color = java.awt.Color(value.rgba, true)
            }

        override fun line(x1: Double, y1: Double, x2: Double, y2: Double) =
            graphics.drawLine(x1.roundToInt(), y1.roundToInt(), x2.roundToInt(), y2.roundToInt())

        override fun rectangle(x: Double, y: Double, w: Double, h: Double) = if (fill)
            graphics.fillRect(x.roundToInt(), y.roundToInt(), w.roundToInt(), h.roundToInt()) else
            graphics.drawRect(x.roundToInt(), y.roundToInt(), w.roundToInt(), h.roundToInt())

        override fun ellipse(x: Double, y: Double, w: Double, h: Double) = if (fill)
            graphics.fillOval(x.roundToInt(), y.roundToInt(), w.roundToInt(), h.roundToInt()) else
            graphics.drawOval(x.roundToInt(), y.roundToInt(), w.roundToInt(), h.roundToInt())

        override fun path(path: Graphics.Path) {
            path as SwingPath
            if (fill)
                graphics.fill(path.path) else
                graphics.draw(path.path)
        }

        override fun image(x: Double, y: Double, image: Image) {
            graphics.drawImage((image as SwingImage).image, x.roundToInt(), y.roundToInt(), null)
        }

        override fun toImage(): ir.smmh.mage.core.Image {
            return SwingImage(image) // TODO recreate image
        }
    }

    private val MouseEvent.mousePoint: Point
        get() = Point.of(x.toDouble(), y.toDouble())

    private fun Event.Key.happenVia(e: KeyEvent) =
        happen(Event.Key.Data(e.keyCode, e.keyChar, translateKeyLocation(e.keyLocation)))

    private fun translateKeyLocation(location: Int): Event.Key.Location? = when (location) {
        KeyEvent.KEY_LOCATION_STANDARD -> Event.Key.Location.STANDARD
        KeyEvent.KEY_LOCATION_LEFT -> Event.Key.Location.LEFT
        KeyEvent.KEY_LOCATION_RIGHT -> Event.Key.Location.RIGHT
        KeyEvent.KEY_LOCATION_NUMPAD -> Event.Key.Location.NUMPAD
        else -> null
    }

    private fun Point2D.toPoint(): Point =
        Point.of(x, y)

    override fun createPath(): Graphics.Path =
        SwingPath()

    private class SwingPath(val path: Path2D = Path2D.Double()) : Graphics.Path {

        override val platform = SwingPlatform

        override var x: Double
            get() = path.currentPoint.x
            set(value) {
                path.moveTo(value, y)
            }

        override var y: Double
            get() = path.currentPoint.y
            set(value) {
                path.moveTo(x, value)
            }

        override fun move(x: Double, y: Double) =
            path.moveTo(x, y)

        override fun line(x: Double, y: Double) =
            path.lineTo(x, y)

        override fun quadratic(x1: Double, y1: Double, x2: Double, y2: Double) =
            path.quadTo(x1, y1, x2, y2)

        override fun bezier(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double) =
            path.curveTo(x1, y1, x2, y2, x3, y3)

        override fun transform(transformationMatrix: Graphics.TransformationMatrix) =
            path.transform((transformationMatrix as SwingTransformationMatrix).affineTransform)
    }

    override fun createTransformationMatrix(): Graphics.TransformationMatrix =
        SwingTransformationMatrix()

    private class SwingTransformationMatrix(val affineTransform: AffineTransform = AffineTransform()) :
        Graphics.TransformationMatrix {

        override val platform = SwingPlatform

        override val translationX: Double
            get() = affineTransform.translateX

        override val scaleX: Double
            get() = affineTransform.scaleX

        override val shearX: Double
            get() = affineTransform.shearX

        override val translationY: Double
            get() = affineTransform.translateY

        override val scaleY: Double
            get() = affineTransform.scaleY

        override val shearY: Double
            get() = affineTransform.shearY

        override fun translate(x: Double, y: Double) =
            affineTransform.translate(x, y)

        override fun scale(x: Double, y: Double) =
            affineTransform.scale(x, y)

        override fun shear(x: Double, y: Double) =
            affineTransform.shear(x, y)

        override fun rotate(radians: Double) =
            affineTransform.rotate(radians)

        override val inverse: Graphics.TransformationMatrix?
            get() = try {
                SwingTransformationMatrix(affineTransform.createInverse())
            } catch (_: NoninvertibleTransformException) {
                null
            }
    }
}