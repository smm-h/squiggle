package ir.smmh.math.settheory

import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Knowable
import ir.smmh.math.numbers.BuiltinNumberType
import ir.smmh.math.numbers.Numbers
import ir.smmh.math.numbers.Numbers.Integer
import ir.smmh.math.numbers.Numbers.ZERO
import kotlin.random.Random

class FiniteIntegersSet(override val cardinality: Int) : AbstractSet<Integer>(), Set.Finite<Integer> {
    private val integerCardinality = BuiltinNumberType.IntInteger(cardinality)
    override val overElements: Iterable<Integer> by lazy { (0 until cardinality).map(BuiltinNumberType::IntInteger) }
    override fun contains(it: Integer): Boolean = it >= ZERO && it < integerCardinality
    override fun singletonOrNull() = if (cardinality == 1) Numbers.ZERO else null
    override fun isNonReferentiallyEqualTo(that: MathematicalObject): Knowable =
        if (that is FiniteIntegersSet && that.cardinality == cardinality) Knowable.Known.True else Knowable.Unknown

    override fun getPicker(random: Random) =
        MathematicalCollection.Picker<Integer> { BuiltinNumberType.IntInteger(random.nextInt(cardinality)) }
}