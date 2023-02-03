package ir.smmh.math.settheory

import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical
import ir.smmh.math.numbers.Numbers.Natural
import ir.smmh.math.numbers.Numbers.ZERO
import kotlin.random.Random

class FiniteNaturalsSet(override val cardinality: Int) : AbstractSet<Natural>(), Set.Finite<Natural> {
    private val integerCardinality = Natural.of(cardinality)
    override val overElements: Iterable<Natural> by lazy { (0 until cardinality).map(Natural::of) }
    override fun contains(it: Natural) = Logical.of(it >= ZERO && it < integerCardinality)
    override fun singletonOrNull() = if (cardinality == 1) ZERO else null
    override fun isNonReferentiallyEqualTo(that: MathematicalObject): Knowable =
        if (that is FiniteNaturalsSet && that.cardinality == cardinality) Logical.True else Knowable.Unknown

    override fun getPicker(random: Random) =
        MathematicalCollection.Picker<Natural> { Natural.of(random.nextInt(cardinality)) }
}