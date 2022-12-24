package ir.smmh.math.graph

import ir.smmh.nile.Mut

sealed interface Graph<T> {

    val numberOfVertices: Int
    val numberOfEdges: Int
    fun overVertices(): Iterable<T>
    fun overEdges(): Iterable<Edge<T>>
    fun areConnected(v1: T, v2: T): Boolean
    operator fun contains(vertex: T): Boolean
    operator fun contains(edge: Edge<T>): Boolean

    sealed interface Edge<T> {
        val a: T
        val b: T

        interface Directed<T> : Edge<T>
        interface Weighted<T, W> : Edge<T> {
            val weight: W
        }
    }

    interface Directed<T> : Graph<T> {
        override fun overEdges(): Iterable<Edge.Directed<T>>
        override fun contains(edge: Edge<T>) = contains(edge as Edge.Directed<T>)
        fun contains(edge: Edge.Directed<T>): Boolean
    }

    interface Weighted<T, W> : Graph<T> {
        override fun overEdges(): Iterable<Edge.Weighted<T, W>>
        override fun contains(edge: Edge<T>) = @Suppress("UNCHECKED_CAST") contains(edge as Edge.Weighted<T, W>)
        fun contains(edge: Edge.Weighted<T, W>): Boolean
    }

    interface Mutable<T> : Graph<T>, Mut.Able {
        fun add(vertex: T)
        fun remove(vertex: T)
        fun connect(v1: T, v2: T)
        fun disconnect(v1: T, v2: T)
        fun clear()
        fun clearEdges()
    }
}