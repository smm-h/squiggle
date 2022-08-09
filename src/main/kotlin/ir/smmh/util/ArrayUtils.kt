package ir.smmh.util

object ArrayUtils {
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> arrayOf(n: Int, f: (Int) -> T): Array<T> =
        arrayOfNulls<T>(n).apply { (0 until n).forEach { this[it] = f(it) } } as Array<T>
}