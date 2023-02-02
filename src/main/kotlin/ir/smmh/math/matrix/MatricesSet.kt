package ir.smmh.math.matrix

import ir.smmh.math.InfinitelyIterable
import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject
import ir.smmh.math.abstractalgebra.RingLikeStructure
import ir.smmh.math.logic.Logical
import ir.smmh.math.settheory.Set
import kotlin.random.Random

class MatricesSet<T : MathematicalObject>(
    val rows: Int,
    val columns: Int,
    val ring: RingLikeStructure.SubtractionRing<T>,
) : Set.Infinite<Matrix<T>> {

    override val debugText: String = "MatrixSet($rows,$columns)"
    override fun isNonReferentiallyEqualTo(that: MathematicalObject) = Logical.False

    override fun contains(it: Matrix<T>): Boolean =
        it.rows == rows && it.columns == columns && it.ring == ring

    override val overElements: InfinitelyIterable<Matrix<T>>?
        get() {
            val picker = getPicker(Random)
            return if (picker == null) null else InfinitelyIterable<Matrix<T>> {
                InfinitelyIterable.Iterator(picker::pick)
            }
        }

    override fun getPicker(random: Random): MathematicalCollection.Picker<Matrix<T>>? {
        val fromDomain = ring.domain.getPicker(random)
        return if (fromDomain == null) null else MathematicalCollection.Picker<Matrix<T>> {
            FunctionMatrix.Memoized(rows, columns, ring) { _, _ -> fromDomain.pick() }
        }
    }
}