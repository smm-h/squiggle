package ir.smmh.math.settheory

import ir.smmh.math.MathematicalCollection
import kotlin.random.Random
import ir.smmh.math.MathematicalObject as M

class ListPicker<T : M>(
    private val list: List<T>,
    private val random: Random = Random,
) : MathematicalCollection.Picker<T> {
    override fun pick(): T = list[random.nextInt(list.size)]
}