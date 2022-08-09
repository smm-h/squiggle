package ir.smmh.nilex

import ir.smmh.lingu.Code
import ir.smmh.nile.Order
import ir.smmh.serialization.json.Json

class NiLexFactory(val name: String = "NiLex") : () -> NiLexTokenizer {

    private val order: Order<Json.Object> = Order.by {
        when (it["kind"] as String) {
            "streak" -> 1
            "kept" -> 2
            "verbatim" -> 3
            else -> 4
        }
    }

    operator fun plus(definition: Json.Object) = this.apply {
        order.enter(definition)
    }

    fun load(code: Code) {
        NiLexLanguage[code].overSubValues().forEach { plus(it as Json.Object) }
    }

    override fun invoke() = NiLexTokenizer(name).apply {
        val array = Json.Array.Mutable.empty()
        while (order.isNotEmpty()) {
            val definition = order.poll()
            array.add(definition)
            define(definition)
        }
//        println(array.serialization)
        define(seal)
    }

    companion object {
        private val seal = Json.Object.of("kind" to "seal")

        fun verbatim(data: Any?) = Json.Object.of(
            "kind" to "verbatim",
            "data" to data,
        )

        fun streak(name: String?, charSet: Any?, ignore: Boolean? = false) = Json.Object.of(
            "kind" to "streak",
            "name" to name,
            "char-set" to charSet,
            "ignore" to ignore,
        )

        fun kept(name: String?, opener: String?, closer: String?, ignore: Boolean? = false) = Json.Object.of(
            "kind" to "kept",
            "name" to name,
            "opener" to opener,
            "closer" to closer,
            "ignore" to ignore,
        )
    }

}