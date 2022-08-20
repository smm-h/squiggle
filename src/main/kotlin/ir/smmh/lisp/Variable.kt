package ir.smmh.lisp

data class Variable(val type: Type) {
    var value: Value = Value._nothing
        set(v: Value) {
            if (v.type.isSubtypeOf(type)) {
                field = v
            } else {
                println("setting failed")
            }
        }
}