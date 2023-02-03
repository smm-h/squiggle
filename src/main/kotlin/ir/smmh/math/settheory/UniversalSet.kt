package ir.smmh.math.settheory

import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Logical
import kotlin.random.Random

object UniversalSet : Set.Infinite<MathematicalObject> {
    override val debugText = "U"
    override val tex = "{\\mathbb{U}}"
    override fun contains(it: MathematicalObject) = Logical.True
    override fun isNonReferentiallyEqualTo(that: MathematicalObject) = Logical.False
    override fun getPicker(random: Random) = null
    override val overElements = null
}