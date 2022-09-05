package ir.smmh.lisp

import ir.smmh.nile.Named
import kotlin.reflect.KClass

sealed class Type : Named, Value() {

    override fun toString(): String = "type $name"

    override val type: Type = _Type

    abstract fun isSubtypeOf(other: Type): Boolean

    sealed interface TypeCheckResult {

        private object Incorrect : TypeCheckResult

        private sealed interface Single : TypeCheckResult {
            val it: Type
        }

        private class Typical(override val it: Type, val other: Type) : Single {
            override fun toString() =
                "$it is not a subtype of $other"
        }

        private class Classic<T : Type>(override val it: Type, val other: KClass<T>) : Single {
            override fun toString() =
                "$it is not a subtype of ${other.simpleName}"
        }

        private sealed interface Multiple : TypeCheckResult

        private class IncorrectTypes(val them: Map<Int, TypeCheckResult>) : Multiple {
            override fun toString() =
                "$them"
        }

        private class IncorrectSize(val got: Int, val expected: Int) : Multiple {
            override fun toString() =
                "expected $expected arguments, got $got"
        }

        interface TypeChecking {
            fun add(index: Int, result: TypeCheckResult?)
        }

        private class TypeCheckingImpl : TypeChecking {
            private val map = HashMap<Int, TypeCheckResult>()
            override fun add(index: Int, result: TypeCheckResult?) {
                if (result != null) map[index] = result
            }

            fun done(): TypeCheckResult? = if (map.isEmpty()) null else IncorrectTypes(map)
        }

        companion object {

            val incorrect: TypeCheckResult = Incorrect

            fun typical(it: Type, other: Type): TypeCheckResult? =
                if (it.isSubtypeOf(other)) null else Typical(it, other)

            fun <T : Type> classic2(it: Type, other: KClass<T>): TypeCheckResult =
                Classic(it, other)

            inline fun <reified T : Type> classic(it: Type, other: KClass<T>): TypeCheckResult? =
                if (it is T) null else classic2(it, other)

            fun exact(it: Type, other: Type): TypeCheckResult? =
                if (it == other) null else typical(it, other)

            fun size(got: Int, expected: Int): TypeCheckResult? =
                if (got == expected) null else IncorrectSize(got, expected)

            fun check(block: TypeChecking.() -> Unit): TypeCheckResult? =
                TypeCheckingImpl().apply(block).done()
        }
    }

    sealed class Primitive(override val name: String) : Type() {
        override fun isSubtypeOf(other: Type): Boolean =
            this == other || other == _Anything
    }

    object _Type : Primitive("Type") {
        override val type: Type = this
    }

    object _Nothing : Primitive("Nothing")
    object _Anything : Primitive("Anything")
    object _Boolean : Primitive("Boolean")
    object _Number : Primitive("Number")
    object _String : Primitive("String")
    object _Undefined : Primitive("Undefined")
    object _Object : Primitive("Object")
    object _Arguments : Primitive("Arguments")

    sealed class _Callable : Type() {

        override fun toString(): String = "callable type $name"

        abstract fun checkReturnType(type: Type): TypeCheckResult?
        abstract fun checkArgumentTypes(types: List<Type>): TypeCheckResult?

        override fun isSubtypeOf(other: Type): Boolean =
            this == other || other is _Callable || other == _Anything

        class F(val returnType: Type, val argumentTypes: List<Type>) : _Callable() {

            constructor(returnType: Type, vararg argumentTypes: Type) : this(returnType, argumentTypes.toList())

            override val name: String = "(${argumentTypes.joinToString()}) -> ${returnType}"

            override fun checkReturnType(type: Type): TypeCheckResult? =
                TypeCheckResult.typical(this, returnType)

            override fun checkArgumentTypes(types: List<Type>): TypeCheckResult? {
                val size = TypeCheckResult.size(types.size, argumentTypes.size)
                if (size != null) return size
                return TypeCheckResult.check {
                    types.forEachIndexed { i, it ->
                        add(i, TypeCheckResult.typical(it, argumentTypes[i]))
                    }
                }
            }
        }

        /**
         * "Tail" is the only uncallable callable. `() -> Anything`
         */
        object Tail : _Callable() {
            override val name: String = "Tail"
            override fun checkReturnType(type: Type) = TypeCheckResult.incorrect // true
            override fun checkArgumentTypes(types: List<Type>) = TypeCheckResult.incorrect // types.isEmpty()
            override fun toString(): String = "uncallable type $name"
        }

        /**
         * `(Callable*) -> (() -> Anything)`
         */
        object Block : _Callable() {
            override val name: String = "Block"
            override fun checkReturnType(type: Type) = TypeCheckResult.exact(type, Block)
            override fun checkArgumentTypes(types: List<Type>): TypeCheckResult? {
                return TypeCheckResult.check {
                    types.forEachIndexed { i, it ->
                        add(i, TypeCheckResult.classic(it, _Callable::class))
                    }
                }
            }
        }

        private object ArgumentsMaker : _Callable() {
            override val name: String =
                "ArgumentsMaker"

            override fun checkReturnType(type: Type) =
                TypeCheckResult.exact(type, _Arguments)

            override fun checkArgumentTypes(types: List<Type>): TypeCheckResult? {
                return TypeCheckResult.check {
                    types.chunked(2).forEachIndexed { i, it ->
                        add(i * 2, TypeCheckResult.classic(it[0], _Undefined::class))
                        add(i * 2 + 1, TypeCheckResult.classic(it[1], _Type::class))
                    }
                }
            }
        }

        object Lambda : _Callable() {
            override val name: String =
                "Lambda"

            override fun checkReturnType(type: Type) =
                TypeCheckResult.classic(type, F::class)

            override fun checkArgumentTypes(types: List<Type>): TypeCheckResult? {
                val size = TypeCheckResult.size(types.size, 2)
                if (size != null) return size
                return TypeCheckResult.check {
                    add(0, TypeCheckResult.classic(types[0], _Arguments::class))
                    add(1, TypeCheckResult.classic(types[1], Block::class))
                }
            }
        }

        companion object {
            val namedValues = Lisp.Customization.NamedValues { name ->
                name("block", f(Block) {
                    f(Block) { _ ->
                        it.forEach { (it as f).callable.call(emptyList()) }
                        _nothing
                    }
                })
                name("if", f(F(Tail, _Boolean, Tail, Tail)) {
                    f { _ ->
                        if (it[0] as _boolean == _true)
                            (it[1] as f).callable.call(emptyList())
                        else
                            (it[2] as f).callable.call(emptyList())
                    }
                })
                name("for", f(F(Tail, _Undefined, _Number, F(Tail, _Number))) {
                    f { _ ->
                        val id = (it[0] as _undefined).id
                        val n = (it[1] as _number).double.toInt()
                        for (i in 0 until n)
                            (it[2] as f).callable.call(listOf(_number(i.toDouble())))
                        _nothing
                    }
                })
                name("print", f(F(Tail, _String)) {
                    f { _ ->
                        println((it[0] as _string).string)
                        _nothing
                    }
                })
                name("lambda", f(Lambda) {
                    val head = it[0] as _arguments
                    val body = it[1] as f
//                    val args = head.args.map { it.first.id }
                    // TODO return type
                    f(F(_Undefined, head.args.map { it.second })) {
                        body.callable.call(emptyList())
                    }
                })
                name("argumentsMaker", f(ArgumentsMaker) {
                    val list = ArrayList<Pair<_undefined, Type>>()
                    it.chunked(2).forEach {
                        list += it[0] as _undefined to it[1] as Type
                    }
                    _arguments(list)
                })
            }
        }
    }

    companion object {
        val namedValues = Lisp.Customization.NamedValues { name ->
            name("Anything", _Anything)
            name("Type", _Type)
            name("Boolean", _Boolean)
            name("Number", _Number)
            name("String", _String)
            name("Undefined", _Undefined)
            name("Object", _Object)
            name("Arguments", _Arguments)
        }
    }
}