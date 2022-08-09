package ir.smmh.markup

fun Boolean.toOnOff(): String = if (this) "on" else "off"

fun Boolean.toYesNo(): String = if (this) "yes" else "no"

operator fun Int.times(string: String) = string.repeat(this)

enum class TextDirection { LTR, RTL, CENTERED }

enum class SortOrder { ASCENDING, DESCENDING }

fun String.spaceOut(direction: TextDirection, extraSpace: Int): String {
    return when (direction) {
        TextDirection.LTR -> this + extraSpace * " "
        TextDirection.RTL -> extraSpace * " " + this
        TextDirection.CENTERED -> {
            val i = extraSpace / 2
            i * " " + this + (extraSpace - i) * " "
        }
    }
}

fun String.truncate(maxLength: Int, left: Double = 0.6): String {
    return if (length <= maxLength) this else {
        val x = Math.ceil((maxLength - 4) * left).toInt()
        substring(0, x) + "..." + substring(length - (maxLength - 3 - x))
    }
}
