package ir.smmh.math.function

import ir.smmh.math.numbers.Numbers.Natural
import ir.smmh.math.numbers.Numbers.Natural.Companion.pair
import ir.smmh.math.settheory.FiniteNaturalsSet
import ir.smmh.math.settheory.Set
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// https://en.wikipedia.org/wiki/Function_composition#Examples
// TODO test second example as well
internal class FunctionCompositionTest {
    @Test
    fun testFiniteComposition() {
        val D: Set.Finite<Natural> = FiniteNaturalsSet(10)
        val f = MapFunction.of(D, mapOf(1 pair 1, 2 pair 3, 3 pair 1, 4 pair 2))
        val g = MapFunction.of(D, mapOf(1 pair 2, 2 pair 3, 3 pair 1, 4 pair 2))
        val e = MapFunction.of(D, mapOf(1 pair 2, 2 pair 1, 3 pair 2, 4 pair 3))
        val h = FunctionComposition.of(f, g).calculate()
        assertEquals(e.image(), h.image())
    }
}