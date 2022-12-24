package ir.smmh.math.graph

import ir.smmh.nile.Mut
import ir.smmh.nile.verbs.CanClone

sealed class HashGraph<V>(
    directed: Boolean,
    protected val vertices: MutableSet<V>,
    protected val edges: MutableSet<Graph.Edge<V>>,
) : Graph<V>, CanClone<HashGraph<V>> {

    override val directed: Boolean = directed

    protected fun edgeOf(a: V, b: V): Graph.Edge<V> =
        if (directed) HashDirectedEdge(a, b) else HashUndirectedEdge(a, b)

    class HashUndirectedEdge<V>(
        override val a: V,
        override val b: V,
    ) : Graph.Edge<V> {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Graph.Edge<*>) return false
            return ((a == other.a && b == other.b) || (a == other.b && b == other.a))
        }

        override fun hashCode(): Int {
            val A = a.hashCode()
            val B = b.hashCode()
            return 31 * Math.min(A, B) + Math.max(A, B)
        }

        override fun toString() = "($a<->$b)"
    }

    class HashDirectedEdge<V>(
        override val a: V,
        override val b: V,
    ) : Graph.Edge<V> {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Graph.Edge<*>) return false
            return a == other.a && b == other.b
        }

        override fun hashCode(): Int {
            val A = a.hashCode()
            val B = b.hashCode()
            return 31 * A + B
        }

        override fun toString() = "($a-->$b)"
    }

    override val numberOfVertices: Int by vertices::size
    override val numberOfEdges: Int by edges::size
    override fun overVertices(): Iterable<V> = vertices
    override fun overEdges(): Iterable<Graph.Edge<V>> = edges
    override fun areConnected(v1: V, v2: V): Boolean = edgeOf(v1, v2) in edges
    override fun contains(vertex: V): Boolean = vertex in vertices
    override fun contains(edge: Graph.Edge<V>): Boolean = edge in edges

    class Immutable<V>(
        directed: Boolean = false,
        vertices: Collection<V> = emptySet(),
        edges: Collection<Graph.Edge<V>> = emptySet(),
    ) : HashGraph<V>(directed, vertices.toMutableSet(), edges.toMutableSet()) {

        override fun specificThis() = this
        override fun clone(deepIfPossible: Boolean) =
            HashGraph.Immutable<V>(directed, vertices, edges)
    }

    class Mutable<V> private constructor(
        directed: Boolean = false,
        vertices: Collection<V> = emptySet(),
        edges: Collection<Graph.Edge<V>> = emptySet(),
        override val verticesMut: Mut = Mut(),
        override val edgesMut: Mut = Mut(),
    ) : HashGraph<V>(directed, vertices.toMutableSet(), edges.toMutableSet()),
        Graph.VerticesMutable<V>, Graph.EdgesMutable<V> {

        override fun specificThis() = this
        override fun clone(deepIfPossible: Boolean) =
            HashGraph.Mutable<V>(directed, HashSet(vertices), HashSet(edges), Mut(), Mut())

        override fun addVertex(vertex: V) {
            if (vertex !in vertices) {
                verticesMut.preMutate()
                vertices.add(vertex)
                verticesMut.mutate()
            }
        }

        override fun removeVertex(vertex: V) {
            if (vertex in vertices) {
                verticesMut.preMutate()
                vertices.remove(vertex)
                edges.removeIf { it.a == vertex || it.b == vertex }
                verticesMut.mutate()
            }
        }

        override fun clearVertices() {
            if (vertices.isNotEmpty()) {
                verticesMut.preMutate()
                vertices.clear()
                clearEdges()
                verticesMut.mutate()
            }
        }

        override fun addEdge(v1: V, v2: V) =
            addEdge(edgeOf(v1, v2))

        override fun addEdge(edge: Graph.Edge<V>) {
            if (edge !in edges) {
                edgesMut.preMutate()
                edges.add(edge)
                edgesMut.mutate()
            }
        }

        override fun removeEdge(v1: V, v2: V) =
            removeEdge(edgeOf(v1, v2))

        override fun removeEdge(edge: Graph.Edge<V>) {
            if (edge in edges) {
                edgesMut.preMutate()
                edges.remove(edge)
                edgesMut.mutate()
            }
        }

        override fun clearEdges() {
            if (edges.isNotEmpty()) {
                edgesMut.preMutate()
                edges.clear()
                edgesMut.mutate()
            }
        }
    }
}