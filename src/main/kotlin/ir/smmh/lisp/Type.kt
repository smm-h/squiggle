package ir.smmh.lisp

import ir.smmh.lisp.Value.*
import ir.smmh.nile.Named

sealed class Type : Named, Value {

    override fun toString(): String = name

    override val type: Type = _Type

    abstract fun isSubtypeOf(other: Type): Boolean

    sealed class Primitive(override val name: String) : Type() {
        override fun isSubtypeOf(other: Type): Boolean = this == other || other == _Anything
    }

    object _Type : Primitive("Type") {
        override val type: Type = this
    }

    object _Nothing : Primitive("Nothing")
    object _Anything : Primitive("Anything")
    object _Boolean : Primitive("Boolean")
    object _Number : Primitive("Number")
    object _String : Primitive("String")
    object _Identifier : Primitive("Identifier")
    object _Object : Primitive("Object")
    sealed class _Callable : Type() {
        override fun isSubtypeOf(other: Type): Boolean = this == other || other == _Anything
        abstract fun checkReturnType(type: Type): Boolean
        abstract fun checkArgumentTypes(types: List<Type>): Boolean
        class F(val returnType: Type, vararg argumentTypes: Type) : _Callable() {
            val argumentTypes: List<Type> = argumentTypes.toList()

            override val name: String = "(${argumentTypes.joinToString()}) -> ${returnType}"

            override fun checkReturnType(type: Type): Boolean {
                return type.isSubtypeOf(returnType)
            }

            override fun checkArgumentTypes(types: List<Type>): Boolean {
                if (types.size != argumentTypes.size) return false
                (0 until types.size).forEach { i ->
                    if (!types[i].isSubtypeOf(argumentTypes[i]))
                        return false
                }
                return true
            }
        }

        /**
         * "Tail" is the only uncallable callable. `() -> Anything`
         */
        object Tail : _Callable() {
            override val name: String = "Tail"
            override fun checkReturnType(type: Type): Boolean = false // true
            override fun checkArgumentTypes(types: List<Type>): Boolean = false // types.isEmpty()
        }

        /**
         * `(Callable*) -> (() -> Anything)`
         */
        object Block : _Callable() {
            override val name: String = "Block"
            override fun checkReturnType(type: Type): Boolean = type == Tail
            override fun checkArgumentTypes(types: List<Type>): Boolean {
                types.forEach {
                    if (it !is _Callable)
                        return false
                }
                return true
            }
        }

        companion object {

            val _block = f(Block) {
                f { _ ->
                    it.forEach { (it as f).callable.call(emptyList()) }
                    _nothing
                }
            }

            fun setAll(set: (String, Value) -> Unit) {

                val _if = f(F(Tail, _Boolean, Tail, Tail)) {
                    f { _ ->
                        if (it[0] as _boolean == _boolean.TRUE)
                            (it[1] as f).callable.call(emptyList())
                        else
                            (it[2] as f).callable.call(emptyList())
                    }
                }

                val _for = f(F(Tail, _Identifier, _Number, F(Tail, _Number))) {
                    f { _ ->
                        val id = (it[0] as _identifier).id
                        val n = (it[1] as _number).double.toInt()
                        for (i in 0 until n)
                            (it[2] as f).callable.call(listOf(_number(i.toDouble())))
                        _nothing
                    }
                }

                val _print = f(F(Tail, _String)) {
                    f { _ ->
                        println((it[0] as _string).string)
                        _nothing
                    }
                }

                // set("block", _block)
                set("if", _if)
                set("for", _for)
                set("print", _print)
            }
        }
    }

    companion object {
        fun setAll(set: (String, Value) -> Unit) {
            set("Anything", _Anything)
            set("Type", _Type)
            set("Boolean", _Boolean)
            set("Number", _Number)
            set("String", _String)
            set("Identifier", _Identifier)
            set("Object", _Object)
        }
    }
}