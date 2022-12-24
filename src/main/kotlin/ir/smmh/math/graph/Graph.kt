package ir.smmh.math.graph

import ir.smmh.nile.Mut

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

    interface VerticesMutable<V> : Graph<V> {
        val verticesMut: Mut
        fun addVertex(vertex: V)
        fun removeVertex(vertex: V)
        fun clearVertices()
    }

    interface EdgesMutable<V> : Graph<V> {
        val edgesMut: Mut
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

        interface EdgesMutable<V, W> : Weighted<V, W>, Graph.EdgesMutable<V> {

            val weightsMut: Mut

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