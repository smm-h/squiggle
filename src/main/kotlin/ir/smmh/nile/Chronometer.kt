package ir.smmh.nile

import java.time.Clock
import java.time.Instant

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
    fun stop(): Double

    abstract class ByInstant : Chronometer {
        private var then: Instant? = null

        abstract fun getInstant(): Instant

        override fun reset() {
            then = getInstant()
        }

        override fun stop(): Double =
            (then!!.toEpochMilli() - getInstant().toEpochMilli()) / 10e6
    }

    class ByClockInstant(private val clock: Clock) : ByInstant() {
        override fun getInstant(): Instant = clock.instant()
    }

    class BySystemInstant : ByInstant() {
        override fun getInstant(): Instant = Instant.now()
    }

    /**
     * More portable but less precise than [ByNanoTime]
     */
    class ByCurrentTimeMillis : Chronometer {
        private var m = 0.0
        override fun reset() {
            m = System.currentTimeMillis().toDouble()
        }

        override fun stop(): Double =
            System.currentTimeMillis() - m
    }

    /**
     * More precise but less portable than [ByCurrentTimeMillis]
     */
    class ByNanoTime : Chronometer {
        private var n = 0.0
        override fun reset() {
            n = System.nanoTime().toDouble()
        }

        override fun stop(): Double =
            (System.nanoTime() - n) / 10e6
    }
}