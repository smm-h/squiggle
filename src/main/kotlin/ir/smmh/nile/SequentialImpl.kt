package ir.smmh.nile

import ir.smmh.nile.Sequential.AbstractMutableSequential

class SequentialImpl<T>(
    initialCapacity: Int = 10,
    changesToValues: Change = Change(),
    override val changesToSize: Change = Change(),
) :
    AbstractMutableSequential<T>(changesToValues),
    Sequential.Mutable.VariableSize<T> {
    private val list: MutableList<T>

    constructor(collection: Collection<T>, change: Change = Change()) : this(collection.size, change) {
        list.addAll(collection)
    }

    //    @JvmOverloads
    constructor(iterable: Iterable<T>, initialCapacity: Int = 20, change: Change = Change()) : this(initialCapacity, change) {
        for (element in iterable) list.add(element)
    }

    override fun clone(deepIfPossible: Boolean) = clone(deepIfPossible, Change())

    override fun clone(deepIfPossible: Boolean, changesToValues: Change): Sequential.Mutable.VariableSize<T> {
        // TODO deep clone
        return SequentialImpl(asList(), changesToValues)
    }

    override fun removeIndexFrom(toRemove: Int) {
        changesToSize.beforeChange()
        list.removeAt(toRemove)
        changesToSize.afterChange()
    }

    override fun append(toAppend: T) {
        changesToSize.beforeChange()
        list.add(toAppend)
        changesToSize.afterChange()
    }

    override fun setAtIndex(index: Int, toSet: T) {
        changesToValues.beforeChange()
        list[index] = toSet
        changesToValues.afterChange()
    }

    override fun getAtIndex(index: Int): T {
        return list[index]
    }

    override val size: Int
        get() {
            return list.size
        }

    override fun clear() {
        changesToSize.beforeChange()
        list.clear()
        changesToSize.afterChange()
    }

    override fun prepend(toPrepend: T) {
        changesToSize.beforeChange()
        list.add(0, toPrepend)
        changesToSize.afterChange()
    }

    init {
        list = ArrayList(initialCapacity)
    }
}