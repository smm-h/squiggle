package ir.smmh.nitron

import ir.smmh.nile.table.Table
import ir.smmh.nitron.Nitron.Type.Primitive.STRING
import ir.smmh.nitron.Nitron.Value
import java.io.File

fun main() {
//    val ml = Html
    val filename = "customers"
    val table = Table.fromTsv(File("res/tsv/$filename.tsv"))

    table.removeColumn("CustomerID")

//    table.toMarkupTable().toDocument(filename).generate(ml, defaultMetadata) writeTo
//            touch(ml.bindFileExt("gen/${lateFileExt}/$filename.${lateFileExt}")) open ""

    Nitron.Mind(filename).apply {
        val Customers = imagine("Customer").apply {
            has("name", STRING)
            has("contact", STRING)
            has("address", STRING)
            has("city", STRING)
            has("postalCode", STRING)
            has("country", STRING)
        }

        table.apply {
            val CustomerName = findColumnByName("CustomerName")
            val ContactName = findColumnByName("ContactName")
            val Address = findColumnByName("Address")
            val City = findColumnByName("City")
            val PostalCode = findColumnByName("PostalCode")
            val Country = findColumnByName("Country")

            forEach { k ->
                Customers.let {
                    it.create().apply {
                        set("name", Value.of(CustomerName[k] as String))
                        set("contact", Value.of(ContactName[k] as String))
                        set("address", Value.of(Address[k] as String))
                        set("city", Value.of(City[k] as String))
                        set("postalCode", Value.of(PostalCode[k] as String))
                        set("country", Value.of(Country[k] as String))
                    }
                }
            }
        }

//        println(Customers.toBunch().toTable())
        CLI.goToMind(this)
        CLI.start()
    }

//    println(table)
}