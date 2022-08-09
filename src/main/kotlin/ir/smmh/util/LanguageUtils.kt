package ir.smmh.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object LanguageUtils {
    fun <T> late(nullGetter: () -> T) = object : ReadWriteProperty<Any, T> {

        private var field: T? = null

        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            if (field == null) field = nullGetter()
            return field!!
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            field = value
        }
    }
}