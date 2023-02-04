package ir.smmh.math.relation

import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical
import ir.smmh.math.matrix.ArrayMatrix
import ir.smmh.math.matrix.Matrix
import ir.smmh.math.matrix.Matrix.Companion.forEach
import ir.smmh.math.settheory.ListPicker
import ir.smmh.math.settheory.Set
import ir.smmh.math.tuple.SmallTuple
import ir.smmh.math.tuple.Tuple
import ir.smmh.nile.BiDirectionalMap
import kotlin.random.Random
import ir.smmh.math.MathematicalObject as M


/**
 * A finite closed binary relation ([Relation.Binary.Homogeneous.Finite]) that uses
 * a square logical [matrix] to represent the relation between its elements.
 */
sealed class MatrixRelation<T : M> private constructor(private val map: BiDirectionalMap<T, Int>) :
    Relation.Binary.Homogeneous.Finite<T> {

    abstract val matrix: Matrix<Logical>

    override val tex by matrix::tex

    override fun isNonReferentiallyEqualTo(that: M): Knowable =
        if (that is MatrixRelation<*> && that.matrix == matrix) Logical.True else Knowable.Unknown

    override val holds by lazy {
        object : Set.Finite<Tuple.Binary.Uniform<T>> {
            override val debugText: String get() = "MatrixRelation.holds"
            override val tex: String get() = this@MatrixRelation.tex
            override val cardinality: Int by list::size
            override val overElements: Iterable<Tuple.Binary.Uniform<T>> by ::list
            override fun singletonOrNull(): Tuple.Binary.Uniform<T>? = list.firstOrNull()
            override fun contains(it: Tuple.Binary.Uniform<T>) = Logical.of(list.contains(it))
            override fun getPicker(random: Random) = ListPicker(list, random)
            override fun isNonReferentiallyEqualTo(that: M) = Knowable.Unknown
        }
    }

    override fun get(a: T, b: T): Logical = get(indexOf(a), indexOf(b))
    operator fun get(a: Int, b: Int): Logical = matrix[a, b]

    fun getAtIndex(index: Int): T = map.reverse.getValue(index)

    fun indexOf(element: T): Int = map.getValue(element)
    private val string: String by lazy { list.joinToString(", ", "{", "}") { (a, b) -> "$a->$b" } }
    override fun toString(): String = string
    private val list: List<Tuple.Binary.Uniform<T>> by lazy {
        ArrayList<Tuple.Binary.Uniform<T>>().apply {
            matrix.forEach { i, j ->
                if (matrix[i, j].toBoolean()) {
                    add(SmallTuple.Uniform.Couple(getAtIndex(i), getAtIndex(j)))
                }
            }
        }
    }

    private class Immutable<T : M>(
        map: BiDirectionalMap<T, Int>,
        override val matrix: Matrix<Logical>,
    ) : MatrixRelation<T>(map) {
        override val debugText: String get() = "MatrixRelation.Immutable"
    }

    class Mutable<T : M> private constructor(
        map: BiDirectionalMap<T, Int>,
        override val matrix: Matrix.Mutable<Logical>,
    ) : MatrixRelation<T>(map) {
        override val debugText: String get() = "MatrixRelation.Mutable"

        companion object {
            fun <T : M> Relation.Binary.Homogeneous.Finite<T>.toMutableMatrixRelation() =
                of(holds.overElements!!)

            fun <T : M> empty() =
                of(emptyList<Tuple.Binary.Uniform<T>>())

            fun <T : M> of(vararg binaryTuples: Tuple.Binary.Uniform<T>) =
                of(binaryTuples.asList())

            fun <T : M> of(binaryTuples: Iterable<Tuple.Binary.Uniform<T>>): MatrixRelation.Mutable<T> {
                val (map, matrix) = mapAndMatrixOf(binaryTuples)
                return MatrixRelation.Mutable(map, matrix)
            }
        }
    }

    fun transform(transformation: Transformation): MatrixRelation<T> =
        MatrixRelation.Immutable(map, transformation.execute(matrix, map.size))

    fun interface Transformation {

        fun beforeLoop(r: Matrix<Logical>, q: Matrix.Mutable<Logical>) = Unit
        fun afterLoop(r: Matrix<Logical>, q: Matrix.Mutable<Logical>) = Unit

        fun loopBody(r: Matrix<Logical>, q: Matrix.Mutable<Logical>, i: Int, j: Int, k: Int)

        fun execute(r: Matrix<Logical>, n: Int): Matrix<Logical> {
            val v = 0 until n
            val q = ArrayMatrix<Logical>(n, n, Logical.Structure.asRing)
            beforeLoop(r, q)
            for (i in v) for (j in v) for (k in v) loopBody(r, q, i, j, k)
            afterLoop(r, q)
            return q
        }

        fun <T : M> of(r: Relation.Binary.Homogeneous.Finite<T>): MatrixRelation<T> =
            (if (r is MatrixRelation<T>) r else of(r.holds.overElements!!)).transform(this)
    }

    companion object {
        fun <T : M> Relation.Binary.Homogeneous.Finite<T>.toMatrixRelation() =
            of(holds.overElements!!)

        fun <T : M> empty() =
            of(emptyList<Tuple.Binary.Uniform<T>>())

        fun <T : M> of(vararg binaryTuples: Tuple.Binary.Uniform<T>) =
            of(binaryTuples.asList())

        fun <T : M> of(binaryTuples: Iterable<Tuple.Binary.Uniform<T>>): MatrixRelation<T> {
            val (map, matrix) = mapAndMatrixOf(binaryTuples)
            return MatrixRelation.Immutable(map, matrix)
        }

        private fun <T : M> mapAndMatrixOf(binaryTuples: Iterable<Tuple.Binary.Uniform<T>>): Pair<BiDirectionalMap<T, Int>, Matrix.Mutable<Logical>> {
            val set = HashSet<T>().apply {
                for ((a, b) in binaryTuples) {
                    add(a)
                    add(b)
                }
            }
            require(set.isNotEmpty())
            val map = BiDirectionalMap.indexMapOf(set)
            val matrix = ArrayMatrix(set.size, set.size, Logical.Structure.asRing).also {
                for ((a, b) in binaryTuples)
                    it[map[a]!!, map[b]!!] = Logical.True
            }
            return map to matrix
        }
    }
}