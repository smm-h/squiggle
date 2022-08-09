package ir.smmh.nile.table

import ir.smmh.markup.NoMarkup
import ir.smmh.nile.Cache
import ir.smmh.nile.Mut
import kotlin.random.Random

abstract class TableView(rows: List<Int>, columns: List<Tabular.Column<*>>) : Tabular.View {
    protected val rows: MutableList<Int> = rows.toMutableList()
    protected val columns: MutableList<Tabular.Column<*>> = columns.toMutableList()
    override val size: Int get() = rows.size
    override fun getAtIndex(index: Int): Int = rows[index]
    override fun iterator(): Iterator<Int> = rows.iterator()
    override fun containsValue(toCheck: Int): Boolean = rows.contains(toCheck)
    override fun overColumns(): Iterable<Tabular.Column<*>> = columns
    override fun findNullableColumnByName(name: String): Tabular.Column<*>? = columnsCache(name)

    private val columnsCache: (String) -> Tabular.Column<*>? =
        Cache.Nullable { name -> overColumns().first { it.name == name } }

    override fun view(mut: Mut): Tabular.View.Mutable =
        Mutable(this, rows, columns, mut)

    override fun reversed(mut: Mut): Tabular.View.Mutable =
        Mutable(this, rows.reversed(), columns, mut)

    override fun shuffled(mut: Mut): Tabular.View.Mutable =
        Mutable(this, rows.shuffled(), columns, mut)

    override fun shuffled(random: Random, mut: Mut): Tabular.View.Mutable =
        Mutable(this, rows.shuffled(random), columns, mut)

    // @formatter:off
    override fun sortedBy(ascending: Boolean, mut: Mut, sortingFunction: (Int) -> Int): Tabular.View.Mutable =
        Mutable(
            this, (
                    if (ascending) rows.sortedBy(sortingFunction)
                    else rows.sortedByDescending(sortingFunction)), columns, mut
        )
    // @formatter:on

    override fun filteredBy(mut: Mut, predicate: (Int) -> Boolean): Tabular.View.Mutable =
        Mutable(this, rows.filter(predicate), columns, mut)

    override fun <T> filteredByColumn(column: Tabular.Column<T>, data: T?, mut: Mut): Tabular.View.Mutable =
        Mutable(this, rows.filter { column[it] == data }, columns.minusElement(column), mut)

    class Mutable(
        override val core: Tabular,
        rows: List<Int>,
        columns: List<Tabular.Column<*>>,
        override val viewMut: Mut = Mut(),
    ) :
        TableView(rows, columns), Tabular.View.Mutable {

        override fun reverse() {
            viewMut.preMutate()
            rows.reverse()
            viewMut.mutate()
        }

        override fun shuffle() {
            viewMut.preMutate()
            rows.shuffle()
            viewMut.mutate()
        }

        override fun shuffle(random: Random) {
            viewMut.preMutate()
            rows.shuffle(random)
            viewMut.mutate()
        }

        override fun sortBy(ascending: Boolean, sortingFunction: (Int) -> Int) {
            viewMut.preMutate()
            if (ascending) rows.sortBy(sortingFunction)
            else rows.sortByDescending(sortingFunction)
            viewMut.mutate()
        }

        override fun filterBy(predicate: (Int) -> Boolean) {
            viewMut.preMutate()
            rows.filter(predicate)
            viewMut.mutate()
        }

        override fun <T> filterByColumn(column: Tabular.Column<T>, data: T?) {
            viewMut.preMutate()
            rows.filter { column[it] == data }
            columns.remove(column)
            viewMut.mutate()
        }
    }

    override fun toString(): String = toMarkupTable().toString(NoMarkup)
    override fun updateable(mut: Mut): Tabular.View.Updateable = Updateable.Mutable(this, mut)

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

        class Mutable(core: TableView, override val instructionsMut: Mut) : Updateable(core, null),
            Tabular.View.Updateable.Mutable {
            override fun instruct(vararg instructions: (Tabular.View.Mutable) -> Unit) {
                instructionsMut.preMutate()
                instructions.forEach {
                    this.instructionList.add(it)
                    it(view)
                }
                instructionsMut.mutate()
            }

            override fun toImmutable() = Immutable(core) { update() }
        }
    }
}