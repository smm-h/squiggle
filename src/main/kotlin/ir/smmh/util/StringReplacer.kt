package ir.smmh.util

import ir.smmh.nile.Order
import ir.smmh.nile.Order.Companion.longestFirst
import kotlin.math.min

/**
 * Extremely efficient string replacer
 */
class StringReplacer(val orderBy: (String) -> Int = longestFirst) : (String) -> String {

    constructor(map: Map<String, String>, orderBy: (String) -> Int = longestFirst) : this(orderBy) {
        for ((oldValue, newValue) in map.entries) add(oldValue, newValue)
    }

    constructor(vararg pairs: Pair<String, String>, orderBy: (String) -> Int = longestFirst) : this(orderBy) {
        for (pair in pairs) add(pair.first, pair.second)
    }

    private val oldValuesByTheirFirstChar: MutableMap<Char, Order<String>> = HashMap()
    private val replacements: MutableMap<String, String> = HashMap()
    private var oldValueMinLength = 0

    operator fun plus(pair: Pair<String, String>) = add(pair.first, pair.second)

    override fun invoke(string: String): String = replace(string)

    fun add(oldValue: String, newValue: String): StringReplacer {
        if (oldValue.isEmpty()) throw IllegalArgumentException("oldValue cannot be empty")
        oldValuesByTheirFirstChar.computeIfAbsent(oldValue[0]) { Order.by(orderBy) }.enter(oldValue)
        replacements[oldValue] = newValue
        oldValueMinLength = min(oldValueMinLength, oldValue.length)
        return this
    }

    fun count(string: String): Int {
        if (string.isEmpty()) return 0
        val length = string.length
        if (length < oldValueMinLength) return 0
        var count = 0
        var index = 0
        while (index < length) {
            val char = string[index]
            val oldValues = oldValuesByTheirFirstChar[char]
            if (oldValues != null) {
                for (oldValue in oldValues) {
                    if (string.substring(index, index + oldValue.length) == oldValue) {
                        count++
                        index += oldValue.length - 1
                        break
                    }
                }
            }
            index++
        }
        return count
    }

    fun calculateCapacityFor(string: String): Int {
        if (string.isEmpty()) return 0
        val length = string.length
        if (length < oldValueMinLength) return length
        var capacity = string.length
        var index = 0
        while (index < length) {
            val char = string[index]
            val oldValues = oldValuesByTheirFirstChar[char]
            if (oldValues != null) {
                for (oldValue in oldValues) {
                    if (string.substring(index, index + oldValue.length) == oldValue) {
                        val newValue = replacements[oldValue]!!
                        capacity += newValue.length - oldValue.length
                        index += oldValue.length - 1
                        break
                    }
                }
            }
            index++
        }
        return capacity
    }

    fun replace(string: String, calculateCapacity: Boolean = true): String {
        if (string.isEmpty()) return ""
        val length = string.length
        if (length < oldValueMinLength) return string
        val builder = StringBuilder(if (calculateCapacity) calculateCapacityFor(string) else length)
        var index = 0
        while (index < length) {
            val char = string[index]
            val oldValues = oldValuesByTheirFirstChar[char]
            var notReplaced = true
            if (oldValues != null) {
                for (oldValue in oldValues) {
                    if (string.substring(index, index + oldValue.length) == oldValue) {
                        val newValue = replacements[oldValue]!!
                        builder.append(newValue)
                        index += oldValue.length - 1
                        notReplaced = false
                        break
                    }
                }
            }
            if (notReplaced) builder.append(char)
            index++
        }
        return builder.toString()
    }
}