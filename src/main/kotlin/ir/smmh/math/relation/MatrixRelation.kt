package ir.smmh.math.relation

import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Knowable
import ir.smmh.math.matrix.LowLevelMatrix
import ir.smmh.math.matrix.Matrix
import ir.smmh.math.matrix.Matrix.Companion.forEach
import ir.smmh.math.settheory.ContainmentBasedSet
import ir.smmh.math.settheory.ListPicker
import ir.smmh.math.settheory.Set
import ir.smmh.math.tuple.SmallTuple
import ir.smmh.math.tuple.Tuple
import ir.smmh.nile.BiDirectionalMap
import kotlin.random.Random


/**
 * A finite closed binary relation ([Relation.Binary.Homogeneous.Finite]) that uses
 * a square logical [matrix] to represent the relation between its elements.
 */
sealed class MatrixRelation<T : MathematicalObject>
private constructor(private val map: BiDirectionalMap<T, Int>) :
    Relation.Binary.Homogeneous.Finite<T, Tuple.Binary.Uniform<T>> {

    abstract val matrix: Matrix<Boolean>

    override fun isNonReferentiallyEqualTo(that: MathematicalObject): Knowable =
        if (that is MatrixRelation<*> && that.matrix == matrix) Knowable.Known.True else Knowable.Unknown

    override val holds by lazy {
        object : Set.Finite.KnownCardinality<Tuple.Binary.Uniform<T>> {
            override val debugText: String get() = "MatrixRelation.holds"
            override val cardinality: Int by list::size
            override val overElements: Iterable<Tuple.Binary.Uniform<T>> by ::list
            override fun singletonOrNull(): Tuple.Binary.Uniform<T>? = list.firstOrNull()
            override fun contains(it: Tuple.Binary.Uniform<T>) = list.contains(it)
            override fun getPicker(random: Random) = ListPicker(list, random)
            override fun isNonReferentiallyEqualTo(that: MathematicalObject) = Knowable.Unknown
        }
    }

    override fun get(a: T, b: T): Boolean = get(indexOf(a), indexOf(b))
    operator fun get(a: Int, b: Int): Boolean = matrix[a, b]

    override val domain: Set.Finite.KnownCardinality<T> =
        ContainmentBasedSet.Finite.KnownCardinality<T>("MatrixRelation.domain", map.size) { map.containsKey(it) }
    // by lazy { StoredSet(map.keys) }

    fun getAtIndex(index: Int): T = map.reverse.getValue(index)

    fun indexOf(element: T): Int = map.getValue(element)
    private val string: String by lazy { list.joinToString(", ", "{", "}") { (a, b) -> "$a->$b" } }
    override fun toString(): String = string
    private val list: List<Tuple.Binary.Uniform<T>> by lazy {
        ArrayList<Tuple.Binary.Uniform<T>>().apply {
            matrix.forEach { i, j ->
                if (matrix[i, j]) {
                    add(SmallTuple.Uniform.Couple(getAtIndex(i), getAtIndex(j)))
                }
            }
        }
    }

    private class Immutable<T : MathematicalObject>(
        map: BiDirectionalMap<T, Int>,
        override val matrix: Matrix<Boolean>,
    ) : MatrixRelation<T>(map) {
        override val debugText: String get() = "MatrixRelation.Immutable"
    }

    class Mutable<T : MathematicalObject> private constructor(
        map: BiDirectionalMap<T, Int>,
        override val matrix: Matrix.Mutable<Boolean>,
    ) : MatrixRelation<T>(map) {
        override val debugText: String get() = "MatrixRelation.Mutable"

        companion object {
            fun <T : MathematicalObject> Relation.Binary.Homogeneous.Finite<T, Tuple.Binary.Uniform<T>>.toMutableMatrixRelation() =
                of(holds.overElements!!)

            fun <T : MathematicalObject> empty() =
                of(emptyList<Tuple.Binary.Uniform<T>>())

            fun <T : MathematicalObject> of(vararg binaryTuples: Tuple.Binary.Uniform<T>) =
                of(binaryTuples.asList())

            fun <T : MathematicalObject> of(binaryTuples: Iterable<Tuple.Binary.Uniform<T>>): MatrixRelation.Mutable<T> {
                val (map, matrix) = mapAndMatrixOf(binaryTuples)
                return MatrixRelation.Mutable(map, matrix)
            }
        }
    }

    fun transform(transformation: Transformation): MatrixRelation<T> =
        MatrixRelation.Immutable(map, transformation.execute(matrix, map.size))

    fun interface Transformation {

        fun beforeLoop(r: Matrix<Boolean>, q: Matrix.Mutable<Boolean>) = Unit
        fun afterLoop(r: Matrix<Boolean>, q: Matrix.Mutable<Boolean>) = Unit

        fun loopBody(r: Matrix<Boolean>, q: Matrix.Mutable<Boolean>, i: Int, j: Int, k: Int)

        fun execute(r: Matrix<Boolean>, n: Int): Matrix<Boolean> {
            val v = 0 until n
            val q = LowLevelMatrix.Boolean(n, n, null)
            beforeLoop(r, q)
            for (i in v) for (j in v) for (k in v) loopBody(r, q, i, j, k)
            afterLoop(r, q)
            return q
        }

        fun <T : MathematicalObject> of(r: Relation.Binary.Homogeneous.Finite<T, Tuple.Binary.Uniform<T>>): MatrixRelation<T> =
            (if (r is MatrixRelation<T>) r else of(r.holds.overElements!!)).transform(this)
    }

    companion object {
        fun <T : MathematicalObject, TT : Tuple.Binary.Uniform<T>> Relation.Binary.Homogeneous.Finite<T, TT>.toMatrixRelation() =
            of(holds.overElements!!)

        fun <T : MathematicalObject> empty() =
            of(emptyList<Tuple.Binary.Uniform<T>>())

        fun <T : MathematicalObject> of(vararg binaryTuples: Tuple.Binary.Uniform<T>) =
            of(binaryTuples.asList())

        fun <T : MathematicalObject> of(binaryTuples: Iterable<Tuple.Binary.Uniform<T>>): MatrixRelation<T> {
            val (map, matrix) = mapAndMatrixOf(binaryTuples)
            return MatrixRelation.Immutable(map, matrix)
        }

        private fun <T : MathematicalObject> mapAndMatrixOf(binaryTuples: Iterable<Tuple.Binary.Uniform<T>>): Pair<BiDirectionalMap<T, Int>, LowLevelMatrix.Boolean> {
            val set = HashSet<T>().apply {
                for ((a, b) in binaryTuples) {
                    add(a)
                    add(b)
                }
            }
            require(set.isNotEmpty())
            val map = BiDirectionalMap.indexMapOf(set)
            val matrix = LowLevelMatrix.Boolean(set.size, set.size, null).also {
                for ((a, b) in binaryTuples)
                    it[map[a]!!, map[b]!!] = true
            }
            return map to matrix
        }
    }
}