package ir.smmh.nile

import ir.smmh.nile.Sequential.AbstractMutableSequential

class DoubleSequence<T>(first: T, second: T, change: Change = Change()) :
    AbstractMutableSequential<T>(change) {
    private var first: T
    private var second: T
    override fun setAtIndex(index: Int, toSet: T) {
        validateIndex(index)
        changesToValues.beforeChange()
        if (index == 0) first = toSet else second = toSet
        changesToValues.afterChange()
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