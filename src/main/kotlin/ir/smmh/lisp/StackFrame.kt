package ir.smmh.lisp

class StackFrame(val parent: StackFrame?) {
    private val _this = Value._object()

    operator fun get(name: String): Variable {
        return _this.map[name] ?: parent?.get(name) ?: Variable(Type._Anything).also { _this.map[name] = it }
    }

    operator fun set(name: String, type: Type) {
        _this.map[name] = Variable(type)
    }

    operator fun set(name: String, value: Value) {
        val variable: Variable = get(name)
        variable.value = value
    }

    private val arguments = ArrayDeque<Value>()
    fun add(value: Value) {
        arguments.addLast(value)
    }

    fun evaluate(): Value? {
        val first = arguments.removeFirst()
        val firstType = first.type
        if (firstType is Type._Callable) {
            if (firstType.checkArgumentTypes(arguments.map(Value::type))) {
                val value = (first as Value._callable).callable.call(arguments)
                if (firstType.checkReturnType(value.type)) {
                    return value
                } else {
                    println("return type mismatch")
                }
            } else {
                println("argument types mismatch")
            }
        } else {
            println("value is not callable")
        }
        return null
    }
}