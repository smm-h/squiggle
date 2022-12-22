package ir.smmh.util

// https://en.wikipedia.org/wiki/Calculator_spelling
object CalculatorSpeller {
    private val map = mapOf(
        '0' to '0',
        '1' to '1',
        '6' to '9',
        '8' to '8',
        '9' to '6',
        'O' to '0',
        'I' to '1',
        'l' to '1',
        'L' to '7',
        'Z' to '2',
        'E' to '3',
        'h' to '4',
        'S' to '5',
        'g' to '6',
        'B' to '8',
        'G' to '9',
    )

    enum class SpellingWay {
        NO_WAY,
        IF_NO_SPACES,
        IF_UPPERCASE,
        IF_NO_SPACES_AND_UPPERCASE,
        AS_IS,
    }

    fun howCanItBeSpelled(text: String): SpellingWay {
        val charSet = HashSet<Char>().apply {
            text.forEach { add(it) }
        }
        val hasSpaces = charSet.remove(' ')
        return if (map.keys.containsAll(charSet))
            if (hasSpaces) SpellingWay.IF_NO_SPACES
            else SpellingWay.AS_IS
        else if (map.keys.containsAll(charSet.uppercase()))
            if (hasSpaces) SpellingWay.IF_NO_SPACES_AND_UPPERCASE
            else SpellingWay.IF_UPPERCASE
        else SpellingWay.NO_WAY
    }

    private fun Set<Char>.uppercase(): Set<Char> = HashSet<Char>().also { set ->
        forEach { set.add(it.uppercaseChar()) }
    }

    fun spell(text: String): String {
        val s = when (howCanItBeSpelled(text)) {
            SpellingWay.NO_WAY -> throw Exception("It cannot be spelled")
            SpellingWay.IF_NO_SPACES -> text.replace(" ", "")
            SpellingWay.IF_UPPERCASE -> text.uppercase()
            SpellingWay.IF_NO_SPACES_AND_UPPERCASE -> text.uppercase().replace(" ", "")
            SpellingWay.AS_IS -> text
        }.reversed()
        return StringBuilder(s.length).apply { s.forEach { append(map[it]) } }.toString()
    }
}

fun main() {
    println(CalculatorSpeller.spell("Boobies"))
}