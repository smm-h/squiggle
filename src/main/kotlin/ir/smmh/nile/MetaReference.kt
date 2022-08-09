package ir.smmh.nile

import java.lang.ref.Reference
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

sealed class MetaReference<T> {

    class Weak<T> : MetaReference<T>() {
        override fun createReference(value: T?): Reference<T?> {
            return WeakReference(value)
        }

        init {
            reference = createReference(null)
        }
    }

    abstract fun createReference(value: T?): Reference<T?>

    protected lateinit var reference: Reference<T?>

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        reference = createReference(value)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return reference.get()
    }
}