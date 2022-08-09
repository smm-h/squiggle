package ir.smmh.util

import kotlin.reflect.KClass

object ReflectUtil {
    public val KClass<*>.intendedName: String
        get() = qualifiedName!!.split('.').filter { it[0].isUpperCase() }.joinToString(".")
}