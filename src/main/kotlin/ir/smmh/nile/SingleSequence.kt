package ir.smmh.nile

import ir.smmh.nile.Sequential.AbstractMutableSequential

class SingleSequence<T>(override var singleton: T, change: Change = Change()) :
    AbstractMutableSequential<T>(change) {
    override fun setAtIndex(index: Int, toSet: T) {
        validateIndex(index)
        changesToValues.beforeChange()
        singleton = toSet
        changesToValues.afterChange()
    }

    override fun getAtIndex(index: Int): T {
        validateIndex(index)
        return singleton
    }

    override val size: Int = 1
}