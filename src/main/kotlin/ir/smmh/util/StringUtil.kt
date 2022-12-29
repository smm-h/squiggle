package ir.smmh.util

import ir.smmh.nile.Sequential

object StringUtil {
    fun indent(string: String?): String? {
        return shiftRight(string, 1, TAB)
    }

    fun shiftRight(string: String?, length: Int, ch: Char = SPACE): String? {
        val prefix = repeat(ch, length)
        val builder = StringBuilder(string!!.length * 2)
        var firstTime = true
        for (line in string.split(LINEBREAK).toTypedArray()) {
            if (firstTime) {
                firstTime = false
            } else {
                builder.append(LINEBREAK)
            }
            if (!line.isEmpty()) {
                builder.append(prefix)
                builder.append(line)
            }
        }
        return builder.toString()
    }

    fun repeat(ch: Char, count: Int): String {
        return repeat(Character.toString(ch), count)
    }

    fun valueOfSymbol(c: Char, radix: Byte): Byte {
        val v = valueOfSymbol(c)
        return if (v < radix) v else throw IllegalArgumentException("symbol: $c does not match radix: $radix")
    }

    fun symbolOfValue(value: Byte, radix: Byte): Char {
        return if (value < radix) symbolOfValue(value) else throw IllegalArgumentException(
            "value: $value does not match radix: $radix"
        )
    }

    /**
     * Assumes the given Unicode codepoint is either a digit or a letter and maps it
     * to its base-36 value. Both uppercase and lowercase characters are supported.
     * Any other codepoint will lead to unexpected results.
     */
    fun valueOfSymbol(c: Char): Byte {
        var value = c.code.toByte()

        value = (value - 48).toByte()
        // handles digits
        value.toByte()

        value = (value - 7).toByte()
        // handles uppercase letters
        if (value >= 17) value.toByte()

        value = (value - 32).toByte()
        // handles lowercase letters
        if (value >= 42) value.toByte()
        return value
    }

    /**
     * Converts {`0`, `1`, `2`, ... `9`, `10`,
     * `11`, ... `33`, `34`, `35`} as *byte* into
     * {`'0'`, `'1'`, `'2'`, ... `'9'`, `'A'`,
     * `'B'`, ... `'X'`, `'Y'`, `'Z'`} as *char*.
     */
    fun symbolOfValue(value: Byte): Char {
        return if (value < 10) // handles digits
            (value + 48).toChar() else if (value < RADIX_MAX) // handles letters
            (value + 55).toChar() else throw IllegalArgumentException("value: $value does not have a symbol")
    }

    fun stringOfValue(value: Int, radix: Byte = RADIX_DEC): String {
        var v = value
        val string = StringBuilder()
        while (v > 0) {
            string.insert(0, symbolOfValue((v % radix).toByte()))
            v /= radix.toInt()
        }
        return if (string.length == 0) "0" else string.toString()
    }
    /**
     * <table summary="Examples for values of a given string in various radices">
     * <tr>
     * <th>Input as `String`</th>
     * <th>Radix as `int`</th>
     * <th>Output as `Number`</th>
    </tr> *
     * <tr>
     * <td>"ffffff"</td>
     * <td>16</td>
     * <td>16777215</td>
    </tr> *
     * <tr>
     * <td>"11"</td>
     * <td>10</td>
     * <td>11</td>
    </tr> *
     * <tr>
     * <td>"11"</td>
     * <td>2</td>
     * <td>3</td>
    </tr> *
    </table> *
     */
    /**
     * <table summary="Examples for values of a given string in radix 10">
     * <tr>
     * <th>Input as `String`</th>
     * <th>Output as `Number`</th>
    </tr> *
     * <tr>
     * <td>"1234"</td>
     * <td>1234</td>
    </tr> *
     * <tr>
     * <td>"0"</td>
     * <td>0</td>
    </tr> *
     * <tr>
     * <td>""</td>
     * <td>0</td>
    </tr> *
    </table> *
     */
    fun valueOfString(string: String, radix: Byte = RADIX_DEC): Number? {
        require(!string.isEmpty()) { "empty string has no numeric value" }
        val point = string.indexOf(DECIMAL_POINT)
        val whole: Int
        whole = if (point == -1) string.length else point
        var value = 0
        for (i in 0 until whole) {
            value *= radix.toInt()
            value += valueOfSymbol(string[i]).toInt()
        }
        return if (point == -1) {

            // return int
            value
        } else {
            var subValue = 0.0
            for (i in string.length - 1 downTo point + 1) {
                subValue += valueOfSymbol(string[i]).toDouble()
                subValue /= radix.toDouble()
            }

            // return double
            subValue + value
        }
    }

    /**
     * Takes an input string, applies some filling to it, and returns it
     *
     * @param input     The input string
     * @param filler    The string by which filling is to be done
     * @param leftwards The direction in which the filling is to be done
     * @param length    Length of the final string
     * @param cut       Pass true if keeping the output length is more important
     * than displaying the entire input
     * @return The output string
     */
    fun fill(input: String, filler: String, leftwards: Boolean, length: Int, cut: Boolean): String {
        val n = input.length
        return if (n > length) {
            if (cut) if (leftwards) input.substring(n - length) else input.substring(0, length) else input
        } else {
            var addition = repeat(
                filler, Math.ceil(((length - n) / filler.length.toFloat()).toDouble())
                    .toInt()
            )
            if (addition.length > length) addition = addition.substring(0, length)
            if (leftwards) addition + input else input + addition
        }
    }

    fun codepointToText(codepoint: Int): String {
        return "U+" + fill(stringOfValue(codepoint, RADIX_HEX), "0", true, 4, false)
    }

    fun contains(string: String, c: Char): Boolean {
        for (i in 0 until string.length) if (string[i] == c) return true
        return false
    }

    fun count(string: String, c: Char): Int {
        var count = 0
        for (i in 0 until string.length) if (string[i] == c) count++
        return count
    }

    fun splitByCharacter(string: String, splitter: Char): Sequential<String?> {
        val n = count(string, splitter) + 1
        val array = arrayOfNulls<String>(n)
        var a: Int
        var b: Int
        a = 0
        for (i in 0 until n) {
            b = string.indexOf(splitter, a)
            if (b == -1) array[i] = string.substring(a) else array[i] = string.substring(a, b)
            a = b + 1
        }
        return Sequential.of<String?>(array)
    }

    fun splitByLength(string: String, limit: Int): Sequential<String> {
        val l = string.length
        val n = l / limit + if (l % limit > 0) 1 else 0
        val array = ArrayList<String>(n)
        var i = 0
        while (i < n) {
            val a = i * limit
            array.add(
                if (a > l - limit)
                    string.substring(a)
                else
                    string.substring(a, a + limit)
            )
            i += 1
        }
        return Sequential.of(array)
    }

    fun replaceCharacter(string: String?, c: Char, replaceWith: Char): String {
        val b = StringBuilder(string)
        val n = b.length
        for (i in 0 until n) {
            if (b[i] == c) {
                b.setCharAt(i, replaceWith)
            }
        }
        return b.toString()
    }

    fun removePrefix(string: String, prefix: String): String {
        return string.substring(prefix.length)
    }

    fun repeat(string: String?, count: Int): String {
        val builder = StringBuilder()
        for (i in 0 until count) {
            builder.append(string)
        }
        return builder.toString()
    }

    fun lastLeftmostConsecutiveWhitespaceIndex(s: String?): Int {
        val n = s!!.length
        var i = 0
        while (i < n && s[i].isWhitespace()) i++
        return i
    }

    fun firstRightmostConsecutiveWhitespaceIndex(s: String): Int {
        var i = s.length - 1
        while (i >= 0 && s[i].isWhitespace()) i--
        return i
    }

    fun isBlank(s: String?): Boolean {
        return lastLeftmostConsecutiveWhitespaceIndex(s) == s!!.length
        //        return s.isBlank();
    }

    fun strip(s: String): String {
        val k = lastLeftmostConsecutiveWhitespaceIndex(s)
        return if (k == s.length) "" else s.substring(k, firstRightmostConsecutiveWhitespaceIndex(s))
        //        return input.strip();
    }

    const val RADIX_MAX: Byte = 36
    const val RADIX_HEX: Byte = 16
    const val RADIX_DEC: Byte = 10
    const val RADIX_OCT: Byte = 8
    const val RADIX_BIN: Byte = 2
    const val DECIMAL_POINT = '.'
    const val LINEBREAK = "\n"
    const val SPACE = ' '
    const val TAB = '\t'

    fun StringBuilder.getStringAndClear(): String {
        val temp = toString()
        clear()
        return temp
    }

    fun spaceOut(string: String, extraSpace: Int, direction: Float): String {
        val i = (extraSpace * direction).toInt()
        return " ".repeat(i) + string + " ".repeat(extraSpace - i)
    }

    fun truncate(string: String, maxLength: Int, left: Double = 0.6): String =
        if (string.length <= maxLength) string else {
            val x = Math.ceil((maxLength - 4) * left).toInt()
            string.substring(0, x) + "..." + string.substring(string.length - (maxLength - 3 - x))
        }
}