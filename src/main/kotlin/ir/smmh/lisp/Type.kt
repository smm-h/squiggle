package ir.smmh.lisp

sealed class Type : Value {

    override val type: Type = _Type

    abstract fun isSubtypeOf(other: Type): Boolean

    sealed class Primitive : Type() {
        override fun isSubtypeOf(other: Type): Boolean = this == other || other == _Anything
    }

    object _Type : Primitive() {
        override val type: Type = this
    }

    object _Statement : Primitive()
    object _Nothing : Primitive()
    object _Anything : Primitive()
    object _Boolean : Primitive()
    object _Number : Primitive()
    object _String : Primitive()
    object _Object : Primitive()
    sealed class _Callable : Primitive() {
        abstract fun checkReturnType(type: Type): Boolean
        abstract fun checkArgumentTypes(types: List<Type>): Boolean
        class Simple(val returnType: Type, vararg argumentTypes: Type) : _Callable() {
            val argumentTypes: List<Type> = argumentTypes.toList()
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

        object Statements : _Callable() {
            override fun checkReturnType(type: Type): Boolean {
                return type == _Statement
            }

            override fun checkArgumentTypes(types: List<Type>): Boolean {
                types.forEach {
                    if (it != _Statement)
                        return false
                }
                return true
            }
        }
    }

    companion object {
        fun setAll(set: (String, Value) -> Unit) {
            set("Anything", _Anything)
            set("Statement", _Statement)
            set("Type", _Type)
            set("Boolean", _Boolean)
            set("Number", _Number)
            set("String", _String)
            set("Object", _Object)
        }
    }
}