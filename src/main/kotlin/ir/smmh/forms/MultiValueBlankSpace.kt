package ir.smmh.forms

import ir.smmh.forms.Form.BlankSpace.ZeroOrMore
import ir.smmh.nile.Sequential
import java.util.*

class MultiValueBlankSpace : ZeroOrMore {
    private val beforeEach: String
    private val betweenEach: String
    private val afterEach: String
    private val beforeAll: String
    private val afterAll: String
    private val ifLeftBlank: String

    constructor(title: String, betweenEach: String, ifLeftBlank: String) : super(title) {
        beforeEach = ""
        this.betweenEach = betweenEach
        afterEach = ""
        beforeAll = ""
        afterAll = ""
        this.ifLeftBlank = ifLeftBlank
    }

    constructor(title: String, beforeEach: String, afterEach: String, ifLeftBlank: String) : super(title) {
        this.beforeEach = beforeEach
        betweenEach = ""
        this.afterEach = afterEach
        beforeAll = ""
        afterAll = ""
        this.ifLeftBlank = ifLeftBlank
    }

    constructor(title: String, beforeEach: String, betweenEach: String, afterEach: String, ifLeftBlank: String) : super(
        title
    ) {
        this.beforeEach = beforeEach
        this.betweenEach = betweenEach
        this.afterEach = afterEach
        beforeAll = ""
        afterAll = ""
        this.ifLeftBlank = ifLeftBlank
    }

    constructor(
        title: String,
        beforeEach: String,
        afterEach: String,
        beforeAll: String,
        afterAll: String,
        ifLeftBlank: String
    ) : super(title) {
        this.beforeEach = beforeEach
        betweenEach = ""
        this.afterEach = afterEach
        this.beforeAll = beforeAll
        this.afterAll = afterAll
        this.ifLeftBlank = ifLeftBlank
    }

    constructor(
        title: String,
        beforeEach: String,
        betweenEach: String,
        afterEach: String,
        beforeAll: String,
        afterAll: String,
        ifLeftBlank: String
    ) : super(title) {
        this.beforeEach = beforeEach
        this.betweenEach = betweenEach
        this.afterEach = afterEach
        this.beforeAll = beforeAll
        this.afterAll = afterAll
        this.ifLeftBlank = ifLeftBlank
    }

    override fun compose(sequential: Sequential<String>): String {
        return if (sequential.isEmpty()) {
            ifLeftBlank
        } else {
            val j = StringJoiner(betweenEach, beforeAll, afterAll)
            for (value in sequential.overValues)
                j.add(beforeEach + value + afterEach)
            j.toString()
        }
    }
}