package ir.smmh.math.tuple

import ir.smmh.math.MathematicalObject

class MutableArrayTuple<T : MathematicalObject>(dimensions: Int) : Tuple.Uniform.Finitary<T> {
    private val array = Array<Any?>(dimensions) { null }
    override val length by array::size
    override fun get(index: Int): T = @Suppress("UNCHECKED_CAST") (array[index] as T)
    operator fun set(index: Int, value: T) {
        array[index] = value
    }
    override fun hashCode() = array.hashCode()
}