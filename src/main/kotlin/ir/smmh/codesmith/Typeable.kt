package codesmith

import ir.smmh.nile.verbs.CanClear

interface Typeable : CanClear {
    override val size: Int
        get() = length

    var caret: Int
    val length: Int
    val string: String
    fun type(character: Char)
    fun pressBackspace()
    fun pressDelete() {
        moveCaret(+1)
        pressBackspace()
    }

    fun pressEnter() {
        type('\n')
    }

    fun pressSpace() {
        type(' ')
    }

    fun pressEscape() {
        clearCaret()
    }

    fun moveCaret(relativePosition: Int) {
        caret += relativePosition
    }

    fun clearCaret() {
        caret = -1
    }
}