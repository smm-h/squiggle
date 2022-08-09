package ir.smmh.util

interface Setting {
    var enabled: Boolean
    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
    }

    fun toggle() {
        enabled = !enabled
    }

    companion object {
        fun create(initialValue: Boolean = false): Setting =
            Impl(initialValue)
    }

    private class Impl(override var enabled: Boolean) : Setting
}