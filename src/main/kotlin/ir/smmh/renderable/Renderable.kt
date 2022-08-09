package ir.smmh.renderable

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.mage.core.Graphics

// TODO aspects for Language?
interface Renderable : Language {
    fun render(code: Code): Graphics

    companion object {
        val Code.canBeRendered: Boolean get() = language is Renderable
        fun Code.beRendered(): Graphics? {
            val it = language
            return if (it is Renderable) it.render(this) else null
        }
    }
}