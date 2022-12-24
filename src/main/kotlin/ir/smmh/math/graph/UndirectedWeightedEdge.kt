package ir.smmh.math.graph

class UndirectedWeightedEdge<T, W>(
    override val a: T,
    override val b: T,
    override val weight: W,
) : Graph.Edge.Weighted<T, W> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Graph.Edge.Weighted<*, *>) return false
        return ((a == other.a && b == other.b) || (a == other.b && b == other.a)) && weight == other.weight
    }

    override fun hashCode(): Int {
        val A = a.hashCode()
        val B = b.hashCode()
        return 31 * Math.min(A, B) + Math.max(A, B)
    }

    override fun toString() = "($a<-($weight)->$b)"
}