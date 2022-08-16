package ir.smmh.nile.verbs

interface CanSwapAtIndices<T> : CanSetAtIndex<T>, CanGetAtIndex<T> {
    fun swap(i: Int, j: Int) {
        if (i != j) {
            mut.preMutate()
            uncheckedSwap(i, j)
            mut.mutate()
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
        mut.preMutate()
        while (i < h) {
            uncheckedSwap(i, n - i)
            i++
        }
        mut.mutate()
    }

    companion object {
        fun <T> swap(canSwapAtIndices: CanSwapAtIndices<T>, i: Int, j: Int) {
            canSwapAtIndices.swap(i, j)
        }
    }
}