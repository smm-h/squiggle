package ir.smmh.nile.table

import ir.smmh.markup.NoMarkup
import ir.smmh.nile.Change
import kotlin.random.Random

abstract class TableView(rows: List<Int>, columns: List<Tabular.Column<*>>) : Tabular.View {
    protected val rows: MutableList<Int> = rows.toMutableList()
    protected val columns: MutableList<Tabular.Column<*>> = columns.toMutableList()
    override val size: Int get() = rows.size
    override fun getAtIndex(index: Int): Int = rows[index]
    override fun iterator(): Iterator<Int> = rows.iterator()
    override fun containsValue(toCheck: Int): Boolean = rows.contains(toCheck)
    override fun overColumns(): Iterable<Tabular.Column<*>> = columns
    override fun findNullableColumnByName(name: String): Tabular.Column<*>? = columns.firstOrNull { it.name == name }

    override fun view(change: Change): Tabular.View.Mutable =
        Mutable(this, rows, columns, change)

    override fun reversed(change: Change): Tabular.View.Mutable =
        Mutable(this, rows.reversed(), columns, change)

    override fun shuffled(change: Change): Tabular.View.Mutable =
        Mutable(this, rows.shuffled(), columns, change)

    override fun shuffled(random: Random, change: Change): Tabular.View.Mutable =
        Mutable(this, rows.shuffled(random), columns, change)

    // @formatter:off
    override fun sortedBy(ascending: Boolean, change: Change, sortingFunction: (Int) -> Int): Tabular.View.Mutable =
        Mutable(
            this, (
                    if (ascending) rows.sortedBy(sortingFunction)
                    else rows.sortedByDescending(sortingFunction)), columns, change
        )
    // @formatter:on

    override fun filteredBy(change: Change, predicate: (Int) -> Boolean): Tabular.View.Mutable =
        Mutable(this, rows.filter(predicate), columns, change)

    override fun <T> filteredByColumn(column: Tabular.Column<T>, data: T?, change: Change): Tabular.View.Mutable =
        Mutable(this, rows.filter { column[it] == data }, columns.minusElement(column), change)

    class Mutable(
        override val core: Tabular,
        rows: List<Int>,
        columns: List<Tabular.Column<*>>,
        override val changesToView: Change = Change(),
    ) :
        TableView(rows, columns), Tabular.View.Mutable {

        override fun reverse() {
            changesToView.beforeChange()
            rows.reverse()
            changesToView.afterChange()
        }

        override fun shuffle() {
            changesToView.beforeChange()
            rows.shuffle()
            changesToView.afterChange()
        }

        override fun shuffle(random: Random) {
            changesToView.beforeChange()
            rows.shuffle(random)
            changesToView.afterChange()
        }

        override fun sortBy(ascending: Boolean, sortingFunction: (Int) -> Int) {
            changesToView.beforeChange()
            if (ascending) rows.sortBy(sortingFunction)
            else rows.sortByDescending(sortingFunction)
            changesToView.afterChange()
        }

        override fun filterBy(predicate: (Int) -> Boolean) {
            changesToView.beforeChange()
            rows.filter(predicate)
            changesToView.afterChange()
        }

        override fun <T> filterByColumn(column: Tabular.Column<T>, data: T?) {
            changesToView.beforeChange()
            rows.filter { column[it] == data }
            columns.remove(column)
            changesToView.afterChange()
        }
    }

    override fun toString(): String = toMarkupTable().toString(NoMarkup)
    override fun updateable(change: Change): Tabular.View.Updateable = Updateable.Mutable(this, change)

    sealed class Updateable(override val core: TableView, instruction: ((Tabular.View.Mutable) -> Unit)?) :
        Tabular.View.Updateable {

        override var view: Tabular.View.Mutable = core.view()
        protected val instructionList: MutableList<(Tabular.View.Mutable) -> Unit> = ArrayList()

        init {
            if (instruction != null) instructionList.add(instruction)
        }

        override fun reset() {
            view = core.view()
        }

        override fun update() {
            reset()
            instructionList.forEach { it(view) }
        }

        class Immutable(core: TableView, instruction: ((Tabular.View.Mutable) -> Unit)?) :
            Updateable(core, instruction)

        class Mutable(core: TableView, override val changesToInstructions: Change) : Updateable(core, null),
            Tabular.View.Updateable.Mutable {
            override fun instruct(vararg instructions: (Tabular.View.Mutable) -> Unit) {
                changesToInstructions.beforeChange()
                instructions.forEach {
                    this.instructionList.add(it)
                    it(view)
                }
                changesToInstructions.afterChange()
            }

            override fun toImmutable() = Immutable(core) { update() }
        }
    }
}