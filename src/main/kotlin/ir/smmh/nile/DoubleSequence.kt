package ir.smmh.nile

import ir.smmh.nile.Sequential.AbstractMutableSequential

class DoubleSequence<T>(first: T, second: T, mut: Mut = Mut()) :
    AbstractMutableSequential<T>(mut) {
    private var first: T
    private var second: T
    override fun setAtIndex(index: Int, toSet: T) {
        validateIndex(index)
        mut.preMutate()
        if (index == 0) first = toSet else second = toSet
        mut.mutate()
    }

    override fun getAtIndex(index: Int): T {
        validateIndex(index)
        return if (index == 0) first else second
    }

    override val size: Int
        get() {
            return 2
        }

    init {
        this.first = first
        this.second = second
    }
}