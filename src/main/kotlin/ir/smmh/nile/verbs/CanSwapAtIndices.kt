package ir.smmh.nile.verbs

interface CanSwapAtIndices<T> : CanSetAtIndex<T>, CanGetAtIndex<T> {
    fun swap(i: Int, j: Int) {
        if (i != j) {
            val temp = getAtIndex(i)
            setAtIndex(i, getAtIndex(j))
            setAtIndex(j, temp)
        }
    }

    companion object {
        fun <T> swap(canSwapAtIndices: CanSwapAtIndices<T>, i: Int, j: Int) {
            canSwapAtIndices.swap(i, j)
        }
    }
}