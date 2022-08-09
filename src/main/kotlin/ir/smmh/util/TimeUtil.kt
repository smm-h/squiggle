package ir.smmh.util

import java.util.*

object TimeUtil {
    /**
     * @return Current time in Unix time
     */
    fun now(): Long {
        return System.currentTimeMillis() / 1000L
    }

    fun toString(time: Long): String? {
        return Date(time * 1000).toString()
    }
}
