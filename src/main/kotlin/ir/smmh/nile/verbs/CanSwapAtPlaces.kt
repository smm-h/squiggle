package ir.smmh.nile.verbs

interface CanSwapAtPlaces<P, T> : CanSetAtPlace<P, T>, CanGetAtPlace<P, T> {
    fun swap(i: P, j: P) {
        val temp = getAtPlace(i)
        setAtPlace(i, getAtPlace(j))
        setAtPlace(j, temp)
    }

    companion object {
        fun <P, T> swap(canSwapAtPlaces: CanSwapAtPlaces<P, T>, i: P, j: P) {
            canSwapAtPlaces.swap(i, j)
        }
    }
}