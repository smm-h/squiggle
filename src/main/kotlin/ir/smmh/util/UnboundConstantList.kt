package ir.smmh.util

class UnboundConstantList<T>(val constant: T) : AbstractList<T>() {
    override val size: Int get() = -1
    override fun get(index: Int): T = constant
}