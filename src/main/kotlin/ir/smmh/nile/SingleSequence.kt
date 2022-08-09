package ir.smmh.nile

import ir.smmh.nile.Sequential.AbstractMutableSequential

class SingleSequence<T>(override var singleton: T, mut: Mut = Mut()) :
    AbstractMutableSequential<T>(mut) {
    override fun setAtIndex(index: Int, toSet: T) {
        validateIndex(index)
        mut.preMutate()
        singleton = toSet
        mut.mutate()
    }

    override fun getAtIndex(index: Int): T {
        validateIndex(index)
        return singleton
    }

    override val size: Int = 1
}