package ir.smmh.math.graph

import ir.smmh.nile.Change

sealed interface Graph<V> {

    val directed: Boolean

    val numberOfVertices: Int
    val numberOfEdges: Int
    fun overVertices(): Iterable<V>
    fun overEdges(): Iterable<Edge<V>>
    fun areConnected(v1: V, v2: V): Boolean
    operator fun contains(vertex: V): Boolean
    operator fun contains(edge: Edge<V>): Boolean

    sealed interface Edge<V> {
        val a: V
        val b: V
    }

    interface CanChangeVertices<V> : CanChangeEdges<V> {
        val changesToVertices: Change
        fun addVertex(vertex: V)
        fun removeVertex(vertex: V)
        fun clearVertices()
    }

    interface CanChangeEdges<V> : Graph<V> {
        val changesToEdges: Change
        fun addEdge(v1: V, v2: V)
        fun addEdge(edge: Edge<V>)
        fun removeEdge(v1: V, v2: V)
        fun removeEdge(edge: Edge<V>)
        fun clearEdges()
    }

    interface Weighted<V, W> : Graph<V> {

        fun overWeightedEdges(): Iterable<Map.Entry<Edge<V>, W>>
        operator fun get(v1: V, v2: V): W
        operator fun get(edge: Edge<V>): W

        val nullWeight: W

        interface EdgesMutable<V, W> : Weighted<V, W>, Graph.CanChangeEdges<V> {

            val changesToWeights: Change

            operator fun set(v1: V, v2: V, weight: W)
            operator fun set(edge: Edge<V>, weight: W)

            override fun addEdge(v1: V, v2: V) {
                throw Exception("specify weight")
            }

            override fun addEdge(edge: Edge<V>) {
                throw Exception("specify weight")
            }

            fun addEdge(v1: V, v2: V, weight: W)
            fun addEdge(edge: Edge<V>, weight: W)
        }
    }
}