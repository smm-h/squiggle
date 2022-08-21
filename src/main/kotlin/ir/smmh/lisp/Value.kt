package ir.smmh.lisp

sealed interface Value {

    val type: Type

    object _nothing : Value {
        override val type: Type = Type._Nothing
        override fun toString(): String = "nothing~"
    }

    enum class _boolean : Value {
        TRUE, FALSE;

        override val type: Type = Type._Boolean
    }

    class _number(val double: Double) : Value {
        override val type: Type = Type._Number
        override fun toString(): String = "number~$double"
    }

    class _string(val string: String) : Value {
        override val type: Type = Type._String
        override fun toString(): String = "string~$string"
    }

    class _identifier(val id: String) : Value {
        override val type: Type = Type._Identifier
        override fun toString(): String = "identifier~$id"
    }

    class _object() : Value {
        override val type: Type = Type._Object
        val map: MutableMap<String, Variable> = HashMap()
    }

    class f(override val type: Type._Callable = Type._Callable.Tail, val callable: Callable) : Value

    companion object {
        fun setAll(set: (String, Value) -> Unit) {
            set("pass", f { _nothing })
            set("true", _boolean.TRUE)
            set("false", _boolean.FALSE)
        }
    }
}