package ir.smmh.table

/**
 * A [SealableSchema] is a mutable [Table.Schema] that you can [seal] after you
 * are done adding columns to, so that its columns can no longer be modified.
 * Once [sealed], it cannot be unsealed. Always seal it before creating a table.
 */
interface SealableSchema<K : Any, V> : Schema.Mutable<K, V> {
    val sealed: Boolean
    fun seal()

    abstract class Delegated<K : Any, V>(private val delegate: SealableSchema<K, V> = ListSealableSchema()) :
        SealableSchema<K, V> by delegate
}