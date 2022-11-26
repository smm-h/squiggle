package ir.smmh.nile

/**
 * A [Chronometer] is anything that can measure the time elapsed between the
 * invocations of its [reset] and [stop] methods. It is useful for "stopwatch
 * micro-benchmarking", which is only meaningful with "enough" iterations. The
 * only inaccuracies in this kind of benchmarking is the unwanted OS or JVM
 * code such as GC or other optimizations that may run during our code, most of
 * which can be avoided by passing certain flags to the VM. One can also use JMH
 * or Google's Caliper.
 */
interface Chronometer {
    /**
     * Resets the chronometer.
     */
    fun reset()

    /**
     * @return The elapsed time since the last "reset", in milliseconds.
     */
    fun stop(): Long

    private class Impl(val now: () -> Long) : Chronometer {
        private var then = 0L
        override fun stop(): Long = now() - then
        override fun reset() {
            then = now()
        }
    }

    companion object {
        /**
         * More portable but less precise than [N]
         */
        fun M(): Chronometer = Impl(System::currentTimeMillis)

        /**
         * More precise but less portable than [M]
         */
        fun N(): Chronometer = Impl(System::nanoTime)
    }
}