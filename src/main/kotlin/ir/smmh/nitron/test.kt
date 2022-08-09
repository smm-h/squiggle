package ir.smmh.nitron

import ir.smmh.nitron.Nitron.Type.Primitive.*
import ir.smmh.nitron.Nitron.Value
import ir.smmh.nitron.Nitron.named

@Suppress("LocalVariableName")
fun main() {


    Nitron.Mind("test").apply {

        val Physical = imagine("Physical").apply {
            this has (NUMBER named "x")
            this has (NUMBER named "y")
            this has (NUMBER named "z")
            this has (NUMBER named "weight")
        }

        val Organism = imagine("Organism").apply {
            this `is` Physical
            this has (BOOLEAN named "alive")
            this has (STRING named "birth")
            this has (STRING named "death")
        }

        val Person = imagine("Person").apply {
            this has (STRING named "firstName")
            this has (STRING named "lastName")
            this has (STRING named "address")
            this has (STRING named "phoneNumber")
        }

        val Human = imagine("Human").apply {
            this `is` Organism
            this `is` Person
            this has (NUMBER named "height")
        }

        Human.create().apply {
            set("firstName", Value.of("سید محمد مهدی"))
            set("lastName", Value.of("حسینی"))
            set("birth", Value.of("1997, December 15th"))
            set("alive", Value.of(true))
            set("phoneNumber", Value.of("+98 936 115 4657"))
            set("height", Value.of(185))
        }

//        generateFile(Html) open ""
        CLI.start()
    }
}
