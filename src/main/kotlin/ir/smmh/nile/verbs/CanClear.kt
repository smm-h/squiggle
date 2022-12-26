package ir.smmh.nile.verbs

import ir.smmh.nile.CanChangeSize

interface CanClear : CanChangeSize {
    fun clear()
}