package ir.smmh.math.ordertheory

import ir.smmh.math.MathematicalException

class StrictOrderViolationException(val a: Any, val b: Any) :
    MathematicalException("$a cannot be simultaneously greater than and less than $b")