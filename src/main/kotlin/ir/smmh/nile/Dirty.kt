package ir.smmh.nile

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty

class Dirty<T>(val dirty: AtomicBoolean, val f: () -> T) {

    constructor(vararg changes: Change, f: () -> T) :
            this(AtomicBoolean().also { dirty -> for (it in changes) it.afterChange.add { dirty.set(true) } }, f)

    init {
        dirty.set(true)
    }

    private var reference = AtomicReference<T>()

    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (dirty.get()) {
            dirty.set(false)
            reference.set(f())
        }
        return reference.get()!!
    }
}