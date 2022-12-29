package ir.smmh.markup

import ir.smmh.util.StringUtil

enum class TextDirection(val direction: Float) {
    LEFT_TO_RIGHT(0F),
    RIGHT_TO_LEFT(1F),
    CENTERED(0.5F);

    fun spaceOut(string: String, extraSpace: Int): String =
        StringUtil.spaceOut(string, extraSpace, direction)
}