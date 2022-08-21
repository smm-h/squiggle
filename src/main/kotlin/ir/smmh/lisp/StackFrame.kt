package ir.smmh.lisp

class StackFrame(val parent: StackFrame?) {
    private val _this = Value._object()

    /**
     * Searches for a variable with the given name in the current frame, or the
     * nearest parent, and returns it.
     */
    operator fun get(name: String): Variable? {
        return _this.map[name] ?: parent?.get(name)
    }

    operator fun set(name: String, type: Type) {
        _this.map[name] = Variable(type)
    }

    /**
     * Procures a variable by either searching for an existing one in the frame
     * and its parents, or creating one in the frame, and then set the given
     * value to that variable.
     */
    operator fun set(name: String, value: Value) {
        (this[name] ?: Variable(ir.smmh.lisp.Type._Anything).also { _this.map[name] = it }).value = value
    }

    private val arguments = ArrayDeque<Value>()
    fun add(value: Value) {
        arguments.addLast(value)
    }

    fun evaluate(): Value? {
        val head = arguments.removeFirst()
        val headType = head.type
        if (headType == Type._Callable.Tail) {
            println("head cannot be tail")
            return null
        }
        if (headType is Type._Callable) {
            if (headType.checkArgumentTypes(arguments.map(Value::type))) {
                val value = (head as Value.f).callable.call(arguments)
                if (headType.checkReturnType(value.type)) {
                    return value
                } else {
                    println("return type mismatch")
                }
            } else {
                println("argument types mismatch")
            }
        } else {
            println("value is not callable: $head")
        }
        return null
    }
}