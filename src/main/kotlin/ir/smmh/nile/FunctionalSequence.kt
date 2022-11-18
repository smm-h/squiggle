package ir.smmh.nile

class FunctionalSequence<T>(
    override val size: Int,
    val f: (Int) -> T,
) : Sequential.AbstractSequential<T>() {
    override fun getAtIndex(index: Int): T = f(index)
}