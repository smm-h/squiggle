package ir.smmh.math.abstractalgebra

import ir.smmh.math.abstractalgebra.GroupLike.Properties.DIVISIBLE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.INVERTIBLE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.REGULAR
import ir.smmh.math.abstractalgebra.GroupLike.Properties.UNITAL

interface Property {
    val adjective: String
    val noun: String

    interface Directional : Property {
        val left: Side
        val right: Side

        interface Side : Property {
            val full: Directional
        }
    }

    class Holder(vararg properties: Property) {
        private val set: MutableSet<Property> = HashSet()

        init {
            for (p in properties) add(p)
        }

        private fun add(property: Property) {
            if (property in set) return

            set.add(property)

            // full implies either half
            if (property is Property.Directional) {
                add(property.left)
                add(property.right)
            }

            // both halves imply full
            if (property is Property.Directional.Side) {
                val full = property.full
                if (full.left in this && full.right in this) add(full)
            }

            // INVERTIBLE implies REGULAR
            if (property == INVERTIBLE)
                add(REGULAR)

            // UNITAL and DIVISIBLE implies INVERTIBLE
            if (property == UNITAL || property == DIVISIBLE)
                if (UNITAL in this && DIVISIBLE in this)
                    add(INVERTIBLE)
        }

        // fun has(vararg properties: Property): Boolean {for (property in properties) if (property !in this) return false; return true}

        operator fun contains(property: Property): Boolean = property in set

        companion object {
            val empty = Holder()
        }
    }
}