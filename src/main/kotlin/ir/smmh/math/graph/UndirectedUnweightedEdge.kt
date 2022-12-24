package ir.smmh.math.graph

class UndirectedUnweightedEdge<T>(
    override val a: T,
    override val b: T,
) : Graph.Edge<T> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Graph.Edge<*>) return false
        return (a == other.a && b == other.b) || (a == other.b && b == other.a)
    }

    override fun hashCode(): Int {
        val A = a.hashCode()
        val B = b.hashCode()
        return 31 * Math.min(A, B) + Math.max(A, B)
    }

    override fun toString() = "($a<->$b)"
}