package ir.smmh.math.graph

import ir.smmh.nile.Mut
import ir.smmh.nile.verbs.CanClone

sealed class DirectedHashGraph<T>(
    protected val vertices: MutableSet<T>,
    protected val edges: MutableSet<DirectedUnweightedEdge<T>>,
) : Graph.Directed<T>, CanClone<DirectedHashGraph<T>> {

    override val numberOfVertices: Int by vertices::size
    override val numberOfEdges: Int by edges::size
    override fun overVertices(): Iterable<T> = vertices
    override fun overEdges(): Iterable<Graph.Edge.Directed<T>> = edges
    override fun areConnected(v1: T, v2: T): Boolean = DirectedUnweightedEdge(v1, v2) in edges
    override fun contains(vertex: T): Boolean = vertex in vertices
    override fun contains(edge: Graph.Edge.Directed<T>): Boolean = edge in edges

    class Immutable<T> internal constructor(
        vertices: MutableSet<T>,
        edges: MutableSet<DirectedUnweightedEdge<T>>,
    ) : DirectedHashGraph<T>(vertices, edges) {

        constructor() : this(HashSet(), HashSet())

        override fun specificThis() = this
        override fun clone(deepIfPossible: Boolean) =
            DirectedHashGraph.Immutable<T>(HashSet(vertices), HashSet(edges))
    }

    class Mutable<T> private constructor(
        vertices: MutableSet<T>,
        edges: MutableSet<DirectedUnweightedEdge<T>>,
        override val mut: Mut,
    ) : DirectedHashGraph<T>(vertices, edges), Graph.Mutable<T> {

        constructor(mut: Mut = Mut()) : this(HashSet(), HashSet(), mut)

        override fun specificThis() = this
        override fun clone(deepIfPossible: Boolean) =
            DirectedHashGraph.Mutable<T>(HashSet(vertices), HashSet(edges), Mut())

        override fun add(vertex: T) {
            if (vertex !in vertices) {
                mut.preMutate()
                vertices.add(vertex)
                mut.mutate()
            }
        }

        override fun remove(vertex: T) {
            if (vertex in vertices) {
                mut.preMutate()
                vertices.remove(vertex)
                edges.removeIf { it.a == vertex || it.b == vertex }
                mut.mutate()
            }
        }

        override fun connect(v1: T, v2: T) {
            val edge = DirectedUnweightedEdge(v1, v2)
            if (edge !in edges) {
                mut.preMutate()
                edges.add(edge)
                mut.mutate()
            }
        }

        override fun disconnect(v1: T, v2: T) {
            val edge = DirectedUnweightedEdge(v1, v2)
            if (edge in edges) {
                mut.preMutate()
                edges.remove(edge)
                mut.mutate()
            }
        }

        override fun clear() {
            if (vertices.isNotEmpty()) {
                mut.preMutate()
                vertices.clear()
                edges.clear()
                mut.mutate()
            }
        }

        override fun clearEdges() {
            if (edges.isNotEmpty()) {
                mut.preMutate()
                edges.clear()
                mut.mutate()
            }
        }
    }
}