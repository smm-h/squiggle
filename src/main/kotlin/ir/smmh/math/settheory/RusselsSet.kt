package ir.smmh.math.settheory

import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical
import kotlin.random.Random

/**
 * [RusselsSet] or `R` is "the set of all sets that do not contain themselves."
 *
 * Russel's paradox asks: "does `R` contain itself?" There is no answer.
 *
 * [Wikipedia](https://en.wikipedia.org/wiki/Russell%27s_paradox)
 */
sealed class RusselsSet : Set.Infinite<Set<*>> {

    override val debugText: String = "Russel's Set"
    override val overElements = null
    override fun getPicker(random: Random) = null
    override fun isNonReferentiallyEqualTo(that: MathematicalObject): Knowable =
        if (that is RusselsSet) Logical.True else Logical.False

    /**
     * If it does, then it should not, because it is a set of all sets that do
     * not contain themselves, but it does contain itself.
     */
    private object ContainsItself : RusselsSet() {
        override fun contains(it: Set<*>) = if (it == this) Logical.True else doesNotContain(it)
    }

    /**
     * If it does not, then it should, because it is a set that does not contain
     * itself and also a set of all sets that do not contain themselves.
     */
    private object DoesNotContainItself : RusselsSet() {
        override fun contains(it: Set<*>) = if (it == this) Logical.False else doesNotContain(it)
    }

    /**
     * If left unspecified, there will be no answer because the method will
     * become recursive with no base case and will result in a
     * [StackOverflowError]
     */
    private object Unspecified : RusselsSet() {
        override fun contains(it: Set<*>) = doesNotContain(it)
    }
}