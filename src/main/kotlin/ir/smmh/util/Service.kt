package ir.smmh.util

abstract class Service(val pace: Long = 100L) : Thread() {
    override fun run() {
        do {
            try {
                sleep(pace.coerceAtLeast(5L))
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        } while (!serve())
    }

    /**
     * return true to stop the service loop, and false to continue
     */
    // TODO reverse method
    abstract fun serve(): Boolean
}