package ir.smmh.math.tuple

import ir.smmh.nile.Change
import ir.smmh.nile.verbs.CanClear
import ir.smmh.math.MathematicalObject as M

class GenericFinitaryTupleFactory(
    override val changesToSize: Change,
) : CanClear {

    private var idCounter = 0
    override var size = 0
        private set

    private val map: MutableMap<Any, Any> = HashMap()

    fun create(vararg values: M): Tuple.Finitary {
        changesToSize.beforeChange()
        size++
        val tuple = TupleImpl(idCounter++, values)

        changesToSize.afterChange()
        return tuple
    }

    fun destroy(it: Tuple.Finitary) {
        changesToSize.beforeChange()
        changesToSize.afterChange()
    }

    override fun clear() {
        if (isNotEmpty()) {
            changesToSize.beforeChange()
            map.clear()
            changesToSize.afterChange()
        }
    }

    private inner class TupleImpl(
        private val id: Int,
        private val array: Array<out M>
    ) : AbstractFinitaryTuple() {
        override val length: Int get() = array.size
        override fun get(index: Int): M = array[index]
        override fun hashCode() = id
    }
}