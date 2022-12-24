package ir.smmh.math.graph

import java.util.*

class DirectedUnweightedEdge<T>(
    override val a: T,
    override val b: T,
) : Graph.Edge.Directed<T> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Graph.Edge.Directed<*>) return false
        return a == other.a && b == other.b
    }

    override fun hashCode(): Int = Objects.hash(a, b)
    override fun toString() = "($a-->$b)"
}