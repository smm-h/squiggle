package ir.smmh.nitron

import ir.smmh.nitron.Nitron.Type.Primitive.*
import ir.smmh.nitron.Nitron.Value
import ir.smmh.nitron.Nitron.named
import ir.smmh.serialization.json.Json
import java.io.File

fun main() {
    Nitron.Mind("conscription").apply {

        val Country = imagine("Country").apply {
            this has (STRING named "name")
            this has (BOOLEAN named "compulsoryMilitary")
            this has (NUMBER named "population")
            this has (STRING named "notes")
        }

        (Json.parse(File("res/data.json").readText()) as Json.Array).forEach {
            it as Json.Object
            Country.create().apply {
                set("name", Value.of(it["country"] as String))
                set("compulsoryMilitary", Value.of(it["compulsoryMilitary"] as Boolean))
                set("notes", Value.of(it["note"] as String))
                set("population", Value.of((it["pop2021"] as String).toDouble()))
            }
        }

//        generateFile(Html) open ""
        CLI.start()
    }
}