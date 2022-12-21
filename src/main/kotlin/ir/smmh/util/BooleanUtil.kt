package ir.smmh.util

object BooleanUtil {
    fun Boolean.negateIf(condition: Boolean) = if (condition) !this else this
    fun Boolean.toOnOff(): String = if (this) "on" else "off"
    fun Boolean.toYesNo(): String = if (this) "yes" else "no"
}