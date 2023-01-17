package ir.smmh.mage.core

import java.util.concurrent.CopyOnWriteArrayList

import ir.smmh.mage.core.Group as GroupInGeneral

interface Visual {

    var visible: Boolean
    fun draw(g: Graphics)

    abstract class Abstract : Visual {
        override var visible: Boolean = true
    }

    class Lambda(private val onDraw: (Graphics) -> Unit) : Abstract() {
        override fun draw(g: Graphics) = onDraw(g)
    }

    abstract class Group<T : Visual> : Abstract(), GroupInGeneral<T> {
        override fun draw(g: Graphics) =
            if (visible) forEach { it.draw(g) } else Unit

        class List<T : Visual> : Group<T>() {
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