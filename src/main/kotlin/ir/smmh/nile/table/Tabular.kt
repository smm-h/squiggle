package ir.smmh.nile.table

import ir.smmh.markup.Markup
import ir.smmh.nile.Multitude
import ir.smmh.nile.Change
import ir.smmh.nile.Named
import ir.smmh.nile.verbs.*
import kotlin.random.Random

/**
 * Anything like a table, with columns and rows, whose rows are identified
 * using integer keys
 */
interface Tabular : Multitude, Iterable<Int>, CanGetAtIndex<Int>, CanContainValue<Int> {
    // TODO CanClone

    fun toMarkupTable(block: (Markup.Table.Builder.() -> Unit)? = null) = Markup.Table.Builder().run {
        if (block != null) apply(block)
        build(this@Tabular)
    }

    /**
     * A column, using which you can get the data for any row
     */
    interface Column<T> : Iterable<T?>, CanGetAtIndex<T?>, CanContainValue<T>, Named {
        operator fun contains(value: T) = containsValue(value)
        operator fun get(key: Int) = getAtIndex(key)

        /**
         * A mutable column, using which you can set as well as get the data for
         * any row
         */
        interface Mutable<T> : Column<T>, CanSwapAtIndices<T?>, CanClear, CanRemoveAt {
            operator fun set(key: Int, value: T?) = setAtIndex(key, value)
        }
    }

    /**
     * Use this to iterate over the columns of this table
     */
    fun overColumns(): Iterable<Column<*>>

    /**
     * @return A column in this table with the given name
     * @throws NullPointerException if no column with that name exists
     */
    fun findColumnByName(name: String): Column<*> =
        findNullableColumnByName(name)!!

    /**
     * @return A column in this table with the given name, or null if no such
     * column exists
     */
    fun findNullableColumnByName(name: String): Column<*>?

    /**
     * @return A column in this table with the given name
     * @throws NullPointerException if no column with that name exists
     */
    operator fun get(name: String): Column<*> =
        findColumnByName(name)

    /**
     *
     */
    interface Mutable : Tabular, CanClear, CanRemoveAt, CanRemoveElementFrom<Int>, CanAddTo<(Int) -> Unit> {

        /**
         * Use this to iterate over the mutable columns of this table
         */
        override fun overColumns(): Iterable<Column.Mutable<*>>

        /**
         * @return A mutable column in this table with the given name
         * @throws NullPointerException if no column with that name exists
         */
        override fun findColumnByName(name: String): Column.Mutable<*> =
            findNullableColumnByName(name)!!

        /**
         * @return A column in this table with the given name, or null if no such
         * column exists
         */
        override fun findNullableColumnByName(name: String): Column.Mutable<*>?

        /**
         * @return A mutable column in this table with the given name
         * @throws NullPointerException if no column with that name exists
         */
        override operator fun get(name: String): Column.Mutable<*> =
            findColumnByName(name)

        operator fun plusAssign(toAdd: (Int) -> Unit) =
            add(toAdd)

    }

    interface MutableSchema : Tabular {
        val changesToSchema: Change
        fun <T> addColumn(name: String): Column.Mutable<T>
    }

    /**
     * A table whose cell data exists in another table called the core table,
     * and it only keeps a subset of the rows and the columns of the core table.
     * Views allow operations such as sorting and filtering.
     */
    sealed interface View : Tabular {

        /**
         * The immediate table that stores the cell data for this view
         */
        val core: Tabular

        val ultimateCore: Tabular
            get() {
                var c: Tabular = core
                while (c is View) c = c.core
                return c
            }

        /**
         * @return A View with the same rows and columns as this one
         */
        fun view(change: Change = Change()): Mutable

        /**
         * @return A new View equal to this one, except the keys are in reverse
         */
        fun reversed(change: Change = Change()): Mutable

        /**
         * @return A new View equal to this one, except the keys are shuffled
         */
        fun shuffled(change: Change = Change()): Mutable

        /**
         * @return A new View equal to this one, except the keys are shuffled
         * using the given Random
         */
        fun shuffled(random: Random, change: Change = Change()): Mutable

        /**
         * @return A new View equal to this one, except the keys are sorted in
         * the order they were added
         */
        fun sortedByKey(ascending: Boolean = true, change: Change = Change()): Mutable =
            sortedBy(ascending, change) { it }

        /**
         * @return A new View equal to this one, except the keys are sorted in
         * the order described by the given function
         */
        fun sortedBy(ascending: Boolean = true, change: Change = Change(), sortingFunction: (Int) -> Int): Mutable

        /**
         * @return A new View equal to this one, except the keys are sorted so
         * that the cells in a certain column match the order described by the
         * given function
         */
        fun <T> sortedByColumn(
            column: Tabular.Column<T>,
            ascending: Boolean = true,
            sortingFunction: (T?) -> Int
        ) = sortedBy(ascending) { sortingFunction(column[it]) }

        /**
         * @return A new View equal to this one, except it only contains the
         * keys that satisfy the given predicate
         */
        fun filteredBy(change: Change = Change(), predicate: (Int) -> Boolean): Mutable

        /**
         * @return A new View equal to this one, except it only contains the
         * keys whose data in the given column (by its name) are equal (==) to
         * the given data
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> filteredByColumn(columnName: String, data: T?, change: Change = Change()): Mutable =
            filteredByColumn(findColumnByName(columnName) as Column<in T>, data, change)

        /**
         * @return A new View equal to this one, except it only contains the
         * keys whose data in the given column are equal (==) to the given data
         */
        fun <T> filteredByColumn(column: Column<T>, data: T?, change: Change = Change()): Mutable

        /**
         * A View whose operations are executed in-place
         */
        interface Mutable : View {

            val changesToView: Change

            /**
             * Reverses the order of the keys in this table, in place
             */
            fun reverse()

            /**
             * Shuffles the order of the keys in this table, in place
             */
            fun shuffle()

            /**
             * Shuffles the order of the keys in this table using the given
             * random, in place
             */
            fun shuffle(random: Random)

            /**
             * Re-orders the order of the keys in this table to the order in
             * which they were added, in place
             */
            fun sortByKey(ascending: Boolean = true) = sortBy(ascending) { it }

            /**
             * Re-orders the order of the keys in this table to match the order
             * described by the given function, in place
             */
            fun sortBy(ascending: Boolean = true, sortingFunction: (Int) -> Int)

            /**
             * Re-orders the order of the keys in this table so that the cells
             * in a certain column match the order described by the given
             * function, in place
             */
            fun <T> sortByColumn(
                column: Tabular.Column<T>,
                ascending: Boolean = true,
                sortingFunction: (T?) -> Int
            ) = sortBy(ascending) { sortingFunction(column[it]) }

            /**
             * Removes all the rows that do not match the given predicate from
             * this table, in place
             */
            fun filterBy(predicate: (Int) -> Boolean)

            /**
             * Removes all the rows except the ones whose data in the given
             * column (by its name) are equal (==) to the given data
             */
            @Suppress("UNCHECKED_CAST")
            fun <T> filterByColumn(columnName: String, data: T?) =
                filterByColumn(findColumnByName(columnName) as Column<in T>, data)

            /**
             * Removes all the rows except the ones whose data in the given
             * column are equal (==) to the given data
             */
            fun <T> filterByColumn(column: Column<T>, data: T?)
        }

        /**
         * @return an update-able View that is equal to this one
         */
        fun updateable(change: Change = Change()): Updateable

        /**
         * A View that stores the operations done to it so it can update them
         * from the core table using the update method
         */
        sealed interface Updateable {
            val core: Tabular
            val view: View
            fun reset()
            fun update()
            interface Mutable : Updateable {
                val changesToInstructions: Change
                fun instruct(vararg instructions: (Tabular.View.Mutable) -> Unit)
                fun toImmutable(): Updateable
            }
        }
    }
}