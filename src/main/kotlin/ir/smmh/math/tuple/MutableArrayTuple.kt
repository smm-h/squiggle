package ir.smmh.math.tuple

import ir.smmh.math.MathematicalObject as M

class MutableArrayTuple<T : M>(dimensions: Int) : AbstractFinitaryTuple(), Tuple.Uniform.Finitary<T> {
    private val array = Array<Any?>(dimensions) { null }
    override fun hashCode() = array.hashCode()
    override val length by array::size
    override fun get(index: Int): T = @Suppress("UNCHECKED_CAST") (array[index] as T)
    operator fun set(index: Int, value: T) {
        array[index] = value
    }
}