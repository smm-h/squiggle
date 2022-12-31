package ir.smmh.mage.core

import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.floor

import ir.smmh.mage.core.Group as GroupGeneral

abstract class Temporal {

    abstract class AndVisual : Temporal(), Visual {
        override var visible: Boolean = true

        abstract class Group<T> : AndVisual(), GroupGeneral<T> where T : Temporal, T : Visual {

            override fun update() =
                if (enabled) forEach { it.progress() } else Unit

            override fun draw(g: Graphics) =
                if (visible) forEach { it.draw(g) } else Unit

            class List<T> : Group<T>() where T : Temporal, T : Visual {

                private val list: MutableList<T> = CopyOnWriteArrayList()
                override fun iterator(): Iterator<T> = list.iterator()

                override fun add(it: T) {
                    list.add(it)
                }

                override fun remove(it: T) {
                    list.add(it)
                }

                override fun clear() {
                    list.clear()
                }
            }
        }
    }

    class Lambda(private val onUpdate: () -> Unit) : Temporal() {
        override fun update() = onUpdate()
    }

    var enabled: Boolean = true

    /**
     * Age speed cannot be negative
     */
    var ageSpeed: Double = 1.0
        set(value) {
            if (value >= 0) field = value
        }
    var age: Double = 0.0
        private set

    /**
     * Progress passes the time and updates the object a number of times.
     */
    fun progress() {
        if (enabled) {
            val nextAge = age + ageSpeed
            repeat(countIntegers(age, nextAge)) { update() }
            age = nextAge
        }
    }

    /**
     * Update should only be called from progress, and never directly.
     */
    abstract fun update()

    abstract class Group<T : Temporal> : Temporal(), GroupGeneral<T> {

        override fun update() =
            if (enabled) forEach { it.progress() } else Unit

        class List<T : Temporal> : Group<T>() {

            private val list: MutableList<T> = CopyOnWriteArrayList()
            override fun iterator(): Iterator<T> = list.iterator()

            override fun add(it: T) {
                list.add(it)
            }

            override fun remove(it: T) {
                list.add(it)
            }

            override fun clear() {
                list.clear()
            }
        }
    }

    companion object {
        private fun countIntegers(from: Double, to: Double): Int {
            return floor(to).toInt() - floor(from).toInt()
        }
    }


}