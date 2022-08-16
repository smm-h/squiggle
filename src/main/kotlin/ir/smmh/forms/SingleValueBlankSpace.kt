package ir.smmh.forms

import ir.smmh.forms.Form.BlankSpace
import ir.smmh.forms.Form.IncompleteFormException
import ir.smmh.nile.Sequential

class SingleValueBlankSpace : BlankSpace {
    private val title: String
    private val prefix: String
    private val suffix: String
    private val ifLeftBlank: String?

    constructor(title: String) {
        this.title = title
        prefix = ""
        suffix = ""
        ifLeftBlank = null
    }

    constructor(title: String, ifLeftBlank: String?) {
        this.title = title
        prefix = ""
        suffix = ""
        this.ifLeftBlank = ifLeftBlank
    }

    constructor(title: String, prefix: String, suffix: String) {
        this.title = title
        this.prefix = prefix
        this.suffix = suffix
        ifLeftBlank = null
    }

    constructor(title: String, prefix: String, suffix: String, ifLeftBlank: String?) {
        this.title = title
        this.prefix = prefix
        this.suffix = suffix
        this.ifLeftBlank = ifLeftBlank
    }

    override fun compose(values: Sequential<String>): String {
        return if (values.isEmpty()) {
            if (ifLeftBlank == null) throw IncompleteFormException(this, "cannot be left blank")
            ifLeftBlank
        } else {
            prefix + values.singleton + suffix
        }
    }

    override fun getTitle(): String {
        return title
    }

    override fun getMinimumCount(): Int {
        return if (ifLeftBlank == null) 1 else 0
    }

    override fun getMaximumCount(): Int {
        return 1
    }
}