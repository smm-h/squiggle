package ir.smmh.nile.verbs

import ir.smmh.nile.Multitude

interface CanClear : Multitude.VariableSize {
    fun clear()
}