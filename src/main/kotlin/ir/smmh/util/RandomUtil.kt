package ir.smmh.util

import java.util.*

/**
 * This interface is used to generate random things like strings or numbers.
 */
object RandomUtil {
    /**
     * Generate a random string of lower case hex digits of given length.
     *
     * @param length Length of the generated string
     * @return A randomly generated string
     */
    fun generateRandomHex(length: Int): String {
        val array = CharArray(length)
        for (i in 0 until length) {
            val x = generate.nextInt(16)
            array[i] = (if (x < 10) '0'.code + x else 'a'.code + x - 10).toChar()
        }
        return String(array)
    }

    fun generateRandomIntArray(count: Int, bound: Int): IntArray? {
        val array = IntArray(count)
        for (i in 0 until count) array[i] = generate.nextInt(bound)
        return array
    }

    fun <T> chooseRandomly(choices: Set<T>): T? {
        val size = choices.size
        if (size == 0) throw UnsupportedOperationException("cannot choose randomly from empty set")
        var i = generate.nextInt(size)
        for (t in choices) if (i-- == 0) return t
        return null
    }

    val generate = Random()
}