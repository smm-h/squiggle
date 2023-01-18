package ir.smmh.math.settheory

import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject
import kotlin.random.Random

class StoredSet<T : MathematicalObject>(elements: Iterable<T>) : AbstractSet<T>(), Set.Finite<T> {
    private val set = HashSet<T>().also { it.addAll(elements) }
    override val cardinality by set::size
    override val overElements: Iterable<T> = set
    override fun contains(it: T): Boolean = set.contains(it)
    override fun singletonOrNull() = set.firstOrNull()
    override fun isNonReferentiallyEqualTo(that: MathematicalObject) = that is StoredSet<*> && that.set == set
    override fun getPicker(random: Random): MathematicalCollection.Picker<T>? = ListPicker(this, random)
}