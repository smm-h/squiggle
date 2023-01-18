package ir.smmh.math.settheory

import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject
import kotlin.random.Random

class ListPicker<T : MathematicalObject>(
    private val list: List<T>,
    private val random: Random = Random,
) : MathematicalCollection.Picker<T> {
    override fun pick(): T = list[random.nextInt(list.size)]

    companion object {
        fun <T : MathematicalObject> Set.Finite<T>.getListPicker(random: Random = Random) =
            ListPicker<T>(overElements!!.toList(), random)
    }
}