package ir.smmh.math.settheory

import ir.smmh.math.MathematicalObject
import kotlin.random.Random

object UniversalSet : Set.Infinite<MathematicalObject> {
    override val debugText = "U"
    override fun contains(it: MathematicalObject) = true
    override fun isNonReferentiallyEqualTo(that: MathematicalObject) = false
    override fun getPicker(random: Random) = null
}