package ir.smmh.mage.core

/**
 * A [Process] is an instance of an [App] running on a [Platform]. It listens
 * for events and reports them using `dispatch`. It also maintains graphics and
 * displays it on the screen.
 */
interface Process : PlatformSpecific {

    val dispatch: Event.Dispatch
    val draw: Graphics.Draw

    /**
     * Visible caption of the program.
     */
    var title: String

    /**
     * Size of the canvas.
     */
    var size: Size

    /**
     * The canvas on which all drawing takes place.
     */
    val graphics: Graphics

    /**
     * Make it invisible and exit process.
     */
    fun stop()

    fun screenshot(address: String)
}