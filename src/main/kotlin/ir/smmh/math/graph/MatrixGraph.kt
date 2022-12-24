package ir.smmh.math.graph

import ir.smmh.math.matrix.LowLevelMatrix
import ir.smmh.math.matrix.Matrix

class MatrixGraph<T>(val vertices: List<T>) : Graph.Directed<T>, Graph.Weighted<T, Double> {

    override val numberOfVertices by vertices::size
    override var numberOfEdges = 0
        private set

    private val matrix: Matrix.Mutable<Double> =
        LowLevelMatrix.Double(numberOfVertices, numberOfVertices) { _, _ -> 0.0 }

    override fun overVertices(): Iterable<T> = vertices
    override fun overEdges(): Iterable<DirectedWeightedEdge<T, Double>> {
        val edges: MutableList<DirectedWeightedEdge<T, Double>> = ArrayList()
        for (i in 0 until numberOfVertices) {
            for (j in 0 until numberOfVertices) {
                val w = matrix[i, j]
                if (w != 0.0) {
                    edges.add(DirectedWeightedEdge(vertices[i], vertices[j], w))
                }
            }
        }
        return edges
    }

    fun indexOf(v: T): Int =
        vertices.indexOf(v)

    fun areConnectedIndices(v1: Int, v2: Int): Boolean =
        matrix[v1, v2] != 0.0

    override fun areConnected(v1: T, v2: T): Boolean =
        areConnectedIndices(indexOf(v1), indexOf(v2))

    override fun contains(vertex: T) = vertex in vertices
    override fun contains(edge: Graph.Edge<T>) = areConnected(edge.a, edge.b)
    override fun contains(edge: Graph.Edge.Directed<T>) = areConnected(edge.a, edge.b)
    override fun contains(edge: Graph.Edge.Weighted<T, Double>) = areConnected(edge.a, edge.b)
}