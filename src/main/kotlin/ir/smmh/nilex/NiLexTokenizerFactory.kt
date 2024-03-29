package ir.smmh.nilex

import ir.smmh.lingu.Code
import ir.smmh.nile.Order
import ir.smmh.serialization.json.Json
import java.io.File

class NiLexTokenizerFactory : () -> NiLexTokenizer {

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
        NiLexLanguage[code]?.overSubValues()?.forEach { plus(it as Json.Object) }
    }

    override fun invoke() = NiLexTokenizer().apply {
//        val array = Json.Array.Mutable.empty()
        while (order.isNotEmpty()) {
            val definition = order.poll()
//            array.add(definition)
            define(definition)
        }
//        println(array.serialization)
        define(seal)
    }

    companion object {
        private val seal = Json.Object.of("kind" to "seal")

        fun create(string: String): NiLexTokenizer =
            NiLexTokenizerFactory().apply { load(NiLexLanguage.code(string)) }()

        fun create(file: File): NiLexTokenizer =
            NiLexTokenizerFactory().apply { load(NiLexLanguage.code(file)) }()

    }
}