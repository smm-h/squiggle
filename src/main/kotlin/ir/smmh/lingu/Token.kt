package ir.smmh.lingu

import ir.smmh.lingu.TokenizationUtil.visualizeWhitespace
import ir.smmh.nile.Named

data class Token(val data: String, val type: Token.Type, val position: Int) {
    interface Type : Named

    override fun toString() = (if (data.isEmpty()) "()" else if (data.isBlank()) visualizeWhitespace(data)
    else "($data)") + " as ${type.name} @$position" + if (data.length > 1) "-${position + data.length}" else ""
}