@file:Suppress("FunctionName", "PropertyName")

package ir.smmh.mage.core

import ir.smmh.nile.Cache

/*
 * create/destroy
 * room enter/leave (rooms)
 * path start/end (paths)
 */
@Suppress("unused")
open class Event<T : Any>(val address: String) {

    fun interface Dispatch : (Happened<*>) -> Unit

    data class Happened<T : Any>(val event: Event<T>, val context: T)

    fun <S : T> happen(context: S) = Happened(this, context)

    final override fun toString() = address

    companion object {

        fun Event<Unit>.happen() = happen(Unit)

        /**
         * Triggers at a specific time
         */
//        fun at() TODO

        /**
         * Triggers after `n` updates
         */
//        fun after(n: Int) TODO

        /**
         * Triggers whenever a given condition returns true
         *
         * For example: outside boundaries, out of health, etc.
         */
//        fun condition(condition: () -> Boolean) TODO
    }

    sealed class Window(address: String) : Event<Unit>("Window.$address") {
        object CloseButton : Window("CloseButton")
        object Opened : Window("Opened")
        object Iconified : Window("Iconified")
        object Deiconified : Window("Deiconified")
        object Activated : Window("Activated")
        object Deactivated : Window("Deactivated")
        object GainedFocus : Window("GainedFocus")
        object LostFocus : Window("LostFocus")
        object StateChanged : Window("StateChanged")
    }

    class Key private constructor(address: String, name: String) : Event<Key.Data>("Key.$address.$name") {
        companion object {
            val Typed = Cache(preprocessKey = String::uppercase) { Key("Typed", it) }
            val Pressed = Cache(preprocessKey = String::uppercase) { Key("Pressed", it) }
            val Released = Cache(preprocessKey = String::uppercase) { Key("Released", it) }
        }

        data class Data(val code: Int, val char: Char, val location: Location?)

        enum class Location {
            STANDARD, LEFT, RIGHT, NUMPAD
        }
    }

    sealed class Mouse(address: String) : Event<Point>("Mouse.$address") {
        sealed class Button(button: String, action: String) : Mouse("B$button.$action") {
            sealed class Clicked(button: String) : Button(button, "Clicked") {
                object Left : Clicked("Left")
                object Middle : Clicked("Middle")
                object Right : Clicked("Right")
                companion object {
                    fun of(button: Int) = when (button) {
                        1 -> Left
                        2 -> Middle
                        3 -> Right
                        else -> throw Exception()
                    }
                }
            }

            sealed class Pressed(button: String) : Button(button, "Pressed") {
                object Left : Pressed("Left")
                object Middle : Pressed("Middle")
                object Right : Pressed("Right")
                companion object {
                    fun of(button: Int) = when (button) {
                        1 -> Left
                        2 -> Middle
                        3 -> Right
                        else -> throw Exception()
                    }
                }
            }

            sealed class Released(button: String) : Button(button, "Released") {
                object Left : Released("Left")
                object Middle : Released("Middle")
                object Right : Released("Right")
                companion object {
                    fun of(button: Int) = when (button) {
                        1 -> Left
                        2 -> Middle
                        3 -> Right
                        else -> throw Exception()
                    }
                }
            }
        }

        object Moved : Mouse("Moved")

        sealed class Wheel(address: String) : Event<Double>("Mouse.Wheel.$address") {
            object Up : Wheel("Up")
            object Down : Wheel("Down")
        }
    }
}




