package ir.smmh.math.settheory

import ir.smmh.math.logic.Logical
import kotlin.random.Random
import ir.smmh.math.MathematicalObject as M

object UniversalSet : Set.Infinite<M> {
    override val debugText = "U"
    override val tex = "{\\mathbb{U}}"
    override fun contains(it: M) = Logical.True
    override fun isNonReferentiallyEqualTo(that: M) = Logical.False
    override fun getPicker(random: Random) = null
    override val overElements = null
}