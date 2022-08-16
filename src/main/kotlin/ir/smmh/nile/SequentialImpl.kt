package ir.smmh.nile

import ir.smmh.nile.Sequential.AbstractMutableSequential

class SequentialImpl<T>(initialCapacity: Int = 10, mut: Mut = Mut()) :
    AbstractMutableSequential<T>(mut),
    Sequential.Mutable.VariableSize<T> {
    private val list: MutableList<T>

    constructor(collection: Collection<T>, mut: Mut = Mut()) : this(collection.size, mut) {
        list.addAll(collection)
    }

//    @JvmOverloads
    constructor(iterable: Iterable<T>, initialCapacity: Int = 20, mut: Mut = Mut()) : this(initialCapacity, mut) {
        for (element in iterable) list.add(element)
    }

    override fun clone(deepIfPossible: Boolean) = clone(deepIfPossible, Mut())

    override fun clone(deepIfPossible: Boolean, mut: Mut): Sequential.Mutable.VariableSize<T> {
        // TODO deep clone
        return SequentialImpl(asList(), mut)
    }

    override fun removeIndexFrom(toRemove: Int) {
        mut.preMutate()
        list.removeAt(toRemove)
        mut.mutate()
    }

    override fun append(toAppend: T) {
        mut.preMutate()
        list.add(toAppend)
        mut.mutate()
    }

    override fun setAtIndex(index: Int, toSet: T) {
        mut.preMutate()
        list[index] = toSet
        mut.mutate()
    }

    override fun getAtIndex(index: Int): T {
        return list[index]
    }

    override val size: Int
        get() {
            return list.size
        }

    override fun clear() {
        mut.preMutate()
        list.clear()
        mut.mutate()
    }

    override fun prepend(toPrepend: T) {
        mut.preMutate()
        list.add(0, toPrepend)
        mut.mutate()
    }

    init {
        list = ArrayList(initialCapacity)
    }
}