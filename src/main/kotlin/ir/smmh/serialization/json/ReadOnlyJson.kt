package ir.smmh.serialization.json

import ir.smmh.nile.Sequential

interface ReadOnlyJson {
    fun toObject(): Json.Object
    fun has(key: String): Boolean
    fun getNullable(key: String): Any?
    fun get(key: String): Any = getNullable(key)!!
    fun <T> getNullableSequential(key: String, transform: (Any?) -> T): Sequential<T>?
    fun <T> getSequential(key: String, transform: (Any?) -> T): Sequential<T> = getNullableSequential(key, transform)!!
    fun <T> getNullable2DSequential(key: String, transform: (Any?) -> T): Sequential<Sequential<T>>?
    fun <T> get2DSequential(key: String, transform: (Any?) -> T): Sequential<Sequential<T>> =
        getNullable2DSequential(key, transform)!!

    private open class Impl(private val obj: Json.Object) : ReadOnlyJson {

        override fun toString() = obj.toString()

        override fun toObject() = obj

        override fun has(key: String) = key in obj

        override fun getNullable(key: String): Any? = obj[key]

        override fun <T> getNullableSequential(key: String, transform: (Any?) -> T) =
            Sequential.of((obj[key] as Json.Array).map(transform))

        override fun <T> getNullable2DSequential(key: String, transform: (Any?) -> T): Sequential<Sequential<T>>? =
            Sequential.of((obj[key] as Json.Array).map { Sequential.of((it as Json.Array).map(transform)) })

    }
}