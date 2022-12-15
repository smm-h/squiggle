package ir.smmh.math.plotting

import ir.smmh.mage.core.*

class Plotter(platform: Platform) : BasicApp(platform) {

    fun pixelCoordinatesOf(real: Point): Point = Point.of(pixelXOf(real.x), pixelYOf(real.y))
    fun realCoordinatesOf(pixel: Point): Point = Point.of(realXOf(pixel.x), realYOf(pixel.y))

    fun pixelXOf(realX: Double): Double = +realX * u.x + o.x
    fun pixelYOf(realY: Double): Double = -realY * u.y + o.y

    fun realXOf(pixelX: Double): Double = +(pixelX - o.x) / u.x
    fun realYOf(pixelY: Double): Double = -(pixelY - o.y) / u.y

    fun Graphics.plot(precision: Int = 1, function: (Double) -> Double) {
        var x1 = 0.0
        var y1 = pixelYOf(function(realXOf(x1)))
        var x2: Double
        var y2: Double
        for (x in (0..size.width.toInt()) step precision) {
            x2 = x1
            y2 = y1
            x1 = x.toDouble()
            y1 = pixelYOf(function(realXOf(x1)))
            line(x1, y1, x2, y2)
        }
    }

    var defaultColor: Color.Packed = Color.Named.Black
    var defaultPrecision: Int = 1

    fun addPlot(
        color: Color.Packed = defaultColor,
        precision: Int = defaultPrecision,
        function: (Double) -> Double
    ) = addVisual { g ->
        g.color = color
        g.plot(precision, function)
    }

    /**
     * The highest ratio of the screen length to be filled with axes, on each dimension
     */
    var axisThreshold = 0.075

    /**
     * From 0-100; the maximum level of transparency on the [axisColorRange]
     */
    var axisMaxTranparency = 10 // 60 for dark mode

    /**
     * A [Color.Ranges100.TransparentGray] whose values are used to color the axes
     */
    var axisColorRange = Color.Ranges100.TransparentGray

    private fun axisTransparency(x: Double): Int =
        ((axisThreshold - x.coerceIn(0.0, axisThreshold)) / axisThreshold * axisMaxTranparency).toInt()

    fun Graphics.axisX(x: Double) {
        val pixelX = pixelXOf(x)
        line(pixelX, 0.0, pixelX, size.height)
    }

    fun Graphics.axisY(y: Double) {
        val pixelY = pixelYOf(y)
        line(0.0, pixelY, size.width, pixelY)
    }

    /**
     * The center of the screen
     */
    val center: Point get() = Point.of(size.width / 2, size.height / 2)

    /**
     * The value that changes with panning. Use [pan] to change this immediately
     */
    var offset = Point.origin

    fun pan(newOffset: Point, immediately: Boolean = false) {
        offset = newOffset
        if (immediately) o = offset
    }

    fun panCenter(immediately: Boolean = false) =
        pan(center, immediately)

    /**
     * The size of each unit cell; it changes when zooming in or out. Use [zoom]
     * to change this immediately.
     */
    var unit = Vector.towards(16.0, 16.0)

    fun zoom(factor: Double, immediately: Boolean = false) {
        val c = center
        offset = (offset - c) * factor + c
        unit *= factor
        if (immediately) {
            u = unit
            o = offset
        }
    }

    var offsetRate = 2.0
    var unitRate = 2.0

    var zoomFactor = 1.5

    /**
     * The value approaching [offset] at the rate of [offsetRate]
     */
    private var o: Point = offset

    /**
     * The value approaching [unit] at the rate of [unitRate]
     */
    private var u = unit

    private var pressedAt: Point? = null

    init {
        addSetup {

            // back color
            backColor = Color.Named.White

            // axes
            addVisual {

                it.color = Color.Named.Gray

                it.axisX(0.0)
                it.axisY(0.0)

                val x1 = Math.ceil(-realXOf(0.0)).toInt()
                val x2 = Math.ceil(+realXOf(size.width)).toInt()
                val xt = axisTransparency((x1 + x2) / size.width)
                if (xt > 0) {
                    it.color = Color.Ranges100.TransparentGray[xt]
                    for (x in IntRange(1, x1)) it.axisX(-x.toDouble())
                    for (x in IntRange(1, x2)) it.axisX(+x.toDouble())
                }

                val y1 = Math.ceil(-realYOf(size.height)).toInt()
                val y2 = Math.ceil(+realYOf(0.0)).toInt()
                val yt = axisTransparency((y1 + y2) / size.height)
                if (yt > 0) {
                    it.color = axisColorRange[yt]
                    for (y in IntRange(1, y1)) it.axisY(-y.toDouble())
                    for (y in IntRange(1, y2)) it.axisY(+y.toDouble())
                }
            }

            // pan and zoom
            on(Event.Mouse.Button.Pressed.Left) { pressedAt = o - it }
            on(Event.Mouse.Button.Released.Left) { pressedAt = null }
            on(Event.Key.Pressed("NUMPAD +")) { zoom(zoomFactor) }
            on(Event.Key.Pressed("NUMPAD -")) { zoom(1 / zoomFactor) }
            on(Event.Key.Pressed("SPACE")) { panCenter() }
            addTemporal {
                u = (u * unitRate + unit) / (unitRate + 1)
                o = (o * offsetRate + offset) / (offsetRate + 1)
                pressedAt?.let { offset = it + mousePoint }
            }
        }
    }
}