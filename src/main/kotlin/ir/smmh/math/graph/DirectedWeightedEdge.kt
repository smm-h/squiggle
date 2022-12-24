package ir.smmh.math.graph

import java.util.*

class DirectedWeightedEdge<T, W>(
    override val a: T,
    override val b: T,
    override val weight: W,
) : Graph.Edge.Directed<T>, Graph.Edge.Weighted<T, W> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Graph.Edge.Weighted<*, *>) return false
        return a == other.a && b == other.b && weight == other.weight
    }

    override fun hashCode(): Int = Objects.hash(a, b)
    override fun toString() = "($a--($weight)->$b)"
}