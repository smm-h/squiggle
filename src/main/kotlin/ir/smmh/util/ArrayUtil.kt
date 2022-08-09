package ir.smmh.util

object ArrayUtil {
    /**
     * Searches through the entire array to find an element that equals
     * the given value and then returns its index.
     *
     * @param array The array to search
     * @param value The value to search for
     * @param <T>   The type of the array
     * @return The index of the value, or -1 if it is not found
     */
    fun <T> getIndexOf(array: Array<T>, value: T): Int {
        return getIndexOf(array, value, 0, array.size)
    }

    /**
     * Searches through the array from from  (inclusive) to to (exclusive)
     * to find an element that equals the given value and then returns its
     * index.
     *
     * @param array The array to search
     * @param value The value to search for
     * @param from  The starting index to start the search from
     * @param to    The ending index to stop the search before
     * @param <T>   The type of the array
     * @return The index of the value, or -1 if it is not found
     */
    fun <T> getIndexOf(array: Array<T>, value: T, from: Int, to: Int): Int {
        for (i in from until to) if (array[i] == value) return i
        return -1
    }

    fun <T> makeIterator(array: Array<T>): Iterator<T> {
        return object : Iterator<T> {
            private var index = 0
            override fun hasNext(): Boolean {
                return index < array.size
            }

            override fun next(): T {
                return array[index++]
            }
        }
    }
}
