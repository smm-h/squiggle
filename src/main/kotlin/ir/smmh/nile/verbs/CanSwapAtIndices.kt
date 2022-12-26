package ir.smmh.nile.verbs

interface CanSwapAtIndices<T> : CanSetAtIndex<T>, CanGetAtIndex<T> {
    fun swap(i: Int, j: Int) {
        if (i != j) {
            changesToValues.beforeChange()
            uncheckedSwap(i, j)
            changesToValues.afterChange()
        }
    }

    private fun uncheckedSwap(i: Int, j: Int) {
        val temp = getAtIndex(i)
        setAtIndex(i, getAtIndex(j))
        setAtIndex(j, temp)
    }

    fun reverseInplace() {
        val n = size - 1
        val h = size / 2
        var i = 0
        changesToValues.beforeChange()
        while (i < h) {
            uncheckedSwap(i, n - i)
            i++
        }
        changesToValues.afterChange()
    }

    companion object {
        fun <T> swap(canSwapAtIndices: CanSwapAtIndices<T>, i: Int, j: Int) {
            canSwapAtIndices.swap(i, j)
        }
    }
}