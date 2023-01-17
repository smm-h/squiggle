package ir.smmh.math.settheory

import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject
import kotlin.random.Random

class ListPicker<T : MathematicalObject>(
    set: Set.Finite<T>,
    private val random: Random = Random,
) : MathematicalCollection.Picker<T> {
    private val list = set.overElements?.toList()!!
    override fun pick(): T = list[random.nextInt(list.size)]

    companion object {
        fun <T : MathematicalObject> Set.Finite<T>.getListPicker(random: Random = Random) = ListPicker<T>(this, random)
    }
}