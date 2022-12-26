package ir.smmh.math.graph

import ir.smmh.math.matrix.Matrix
import ir.smmh.nile.Dirty
import ir.smmh.nile.Change

class MatrixDirectedWeightedGraph<V, W : Any>(
    val vertices: List<V>,
    val matrix: Matrix.Mutable<W>,
    override val nullWeight: W = matrix.structure.addition.identity!!,
    override val changesToEdges: Change = Change(),
    override val changesToWeights: Change = Change(),
) : Graph.CanChangeEdges<V>, Graph.Weighted.EdgesMutable<V, W> {

    override val directed = true

    override val numberOfVertices by vertices::size

    override fun overVertices(): Iterable<V> = vertices

    fun indexOf(v: V): Int =
        vertices.indexOf(v)

    fun areConnectedIndices(v1: Int, v2: Int): Boolean =
        matrix[v1, v2] != nullWeight

    override fun areConnected(v1: V, v2: V): Boolean =
        areConnectedIndices(indexOf(v1), indexOf(v2))

    override fun contains(vertex: V) = vertex in vertices
    override fun contains(edge: Graph.Edge<V>) = areConnected(edge.a, edge.b)

    private val edges: Map<Graph.Edge<V>, W> by Dirty(changesToEdges) {
        HashMap<Graph.Edge<V>, W>().apply {
            for (i in 0 until numberOfVertices) {
                for (j in 0 until numberOfVertices) {
                    val w = matrix[i, j]
                    if (w != nullWeight) {
                        put(HashGraph.HashDirectedEdge(vertices[i], vertices[j]), w)
                    }
                }
            }
        }
    }

    override var numberOfEdges = 0
        private set

    override fun overEdges() =
        edges.keys

    override fun overWeightedEdges(): Iterable<Map.Entry<Graph.Edge<V>, W>> =
        edges.entries

    override fun get(edge: Graph.Edge<V>): W =
        get(edge.a, edge.b)

    override fun get(v1: V, v2: V): W {
        val i = indexOf(v1)
        val j = indexOf(v2)
        return matrix[i, j]
    }

    override fun set(edge: Graph.Edge<V>, weight: W) =
        set(edge.a, edge.b, weight)

    override fun set(v1: V, v2: V, weight: W) {
        val i = indexOf(v1)
        val j = indexOf(v2)
        if (matrix[i, j] == nullWeight) {
            changesToWeights.beforeChange()
            matrix[i, j] = weight
            changesToWeights.afterChange()
        }
    }

    override fun addEdge(edge: Graph.Edge<V>, weight: W) =
        addEdge(edge.a, edge.b, weight)

    override fun addEdge(v1: V, v2: V, weight: W) {
        val i = indexOf(v1)
        val j = indexOf(v2)
        if (matrix[i, j] == nullWeight) {
            changesToEdges.beforeChange()
            matrix[i, j] = weight
            changesToEdges.afterChange()
        }
    }

    override fun removeEdge(edge: Graph.Edge<V>) =
        removeEdge(edge.a, edge.b)

    override fun removeEdge(v1: V, v2: V) {
        val i = indexOf(v1)
        val j = indexOf(v2)
        if (matrix[i, j] != nullWeight) {
            changesToEdges.beforeChange()
            matrix[i, j] = nullWeight
            changesToEdges.afterChange()
        }
    }

    override fun clearEdges() {
        if (numberOfEdges > 0) {
            changesToEdges.beforeChange()
            numberOfEdges = 0
            matrix.setAll(nullWeight)
            changesToEdges.afterChange()
        }
    }
}