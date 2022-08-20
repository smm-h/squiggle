package ir.smmh.lisp

sealed interface Value {

    val type: Type

    object _nothing : Value {
        override val type: Type = Type._Nothing
    }

    class _statement(val runnable: Statement) : Value {
        override val type: Type = Type._Statement
    }

    enum class _boolean : Value {
        TRUE, FALSE;

        override val type: Type = Type._Boolean
    }

    class _number(val double: Double) : Value {
        override val type: Type = Type._Number
    }

    class _string(val string: String) : Value {
        override val type: Type = Type._String
    }

    class _object() : Value {
        override val type: Type = Type._Object
        val map: MutableMap<String, Variable> = HashMap()
    }

    class _callable(override val type: Type._Callable, val callable: Callable) : Value

    companion object {
        fun setAll(set: (String, Value) -> Unit) {
            set("pass", _statement {})
            set("true", _boolean.TRUE)
            set("false", _boolean.FALSE)
        }
    }
}