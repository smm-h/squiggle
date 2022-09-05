package ir.smmh.lisp

sealed class Value {

    override fun toString(): String = "some value of $type"

    abstract val type: Type

    object _nothing : Value() {
        override val type: Type = Type._Nothing
        override fun toString(): String = "nothing"
    }

    sealed class _boolean : Value() {
        override val type: Type = Type._Boolean
    }

    object _true : _boolean() {
        override fun toString(): String = "true"
    }

    object _false : _boolean() {
        override fun toString(): String = "false"
    }

    class _number(val double: Double) : Value() {
        override val type: Type = Type._Number
        override fun toString(): String = double.toString()
    }

    class _string(val string: String) : Value() {
        override val type: Type = Type._String
        override fun toString(): String = "\"$string\""
    }

    class _undefined(val id: String) : Value() {
        override val type: Type = Type._Undefined
        override fun toString(): String = "'$id'"
    }

    class _object() : Value() {
        override val type: Type = Type._Object
        private val map: MutableMap<String, Value> = HashMap()
        override fun toString(): String = map.toString()
    }

    class _arguments(val args: List<Pair<_undefined, Type>>) : Value() {
        constructor (vararg pairs: Pair<_undefined, Type>) : this(pairs.toList())

        override val type: Type = Type._Arguments
        override fun toString(): String = args.joinToString(", ", "<", ">") { "${it.first.id}: ${it.second.name}" }
    }

    class f(override val type: Type._Callable = Type._Callable.Tail, val callable: Callable) : Value()

    companion object {
        val namedValues = Lisp.Customization.NamedValues { name ->
            name("pass", f { _nothing })
            name("true", _true)
            name("false", _false)
        }
    }
}