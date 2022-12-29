package ir.smmh.nile.verbs

import ir.smmh.nile.Change
import ir.smmh.nile.HasSize

interface CanChangeSize : HasSize {
    val changesToSize: Change
}