package ir.smmh.forms

import ir.smmh.forms.Form.BlankSpace
import ir.smmh.forms.Form.IncompleteFormException
import ir.smmh.nile.Sequential

class SingleValueBlankSpace(
    override val title: String,
    private val prefix: String = "",
    private val suffix: String = "",
    private val ifLeftBlank: String? = null,
) : BlankSpace {

    override val minimumCount = if (ifLeftBlank == null) 1 else 0
    override val maximumCount = 1

    override fun compose(sequential: Sequential<String>): String {
        return if (sequential.isEmpty()) {
            if (ifLeftBlank == null) throw IncompleteFormException(this, "cannot be left blank")
            ifLeftBlank
        } else {
            prefix + sequential.singleton + suffix
        }
    }
}