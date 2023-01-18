package ir.smmh.math.tuple

import ir.smmh.math.MathematicalObject
import ir.smmh.nile.Change

class GenericFinitaryTupleFactory(
    override val changesToSize: Change,
) : Tuple.Factory<Tuple.Finitary> {

    private var idCounter = 0
    override var size = 0
        private set

    private val map: MutableMap<Any, Any> = HashMap()

    override fun create(vararg values: MathematicalObject): Tuple.Finitary {
        changesToSize.beforeChange()
        size++
        val tuple = TupleImpl(idCounter++, values)

        changesToSize.afterChange()
        return tuple
    }

    override fun destroy(it: Tuple.Finitary) {
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
        private val array: Array<out MathematicalObject>
    ) : AbstractFinitaryTuple() {
        override val length: Int get() = array.size
        override fun get(index: Int): MathematicalObject = array[index]
        override fun hashCode() = id
    }
}