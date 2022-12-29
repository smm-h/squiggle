package ir.smmh.nitron

import ir.smmh.nitron.Nitron.Type.Primitive.STRING
import ir.smmh.nitron.Nitron.Value
import ir.smmh.table.NamedSchema
import ir.smmh.table.StringTable
import java.io.File

fun main() {
//    val ml = Html
    val filename = "customers"
    val table = StringTable.tsv(File("res/tsv/$filename.tsv"))

    @Suppress("UNCHECKED_CAST")
    (table.schema as NamedSchema<Int, String>).apply {
        removeElementFrom(findColumnByName("CustomerID")!!)
        val CustomerName = findColumnByName("CustomerName")!!
        val ContactName = findColumnByName("ContactName")!!
        val Address = findColumnByName("Address")!!
        val City = findColumnByName("City")!!
        val PostalCode = findColumnByName("PostalCode")!!
        val Country = findColumnByName("Country")!!

//        table.toMarkupTable().toDocument(filename).generate(ml, defaultMetadata) writeTo
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
                keySet.overValues.forEach { k ->
                    Customers.let {
                        it.create().apply {
                            set("name", Value.of(CustomerName[k]))
                            set("contact", Value.of(ContactName[k]))
                            set("address", Value.of(Address[k]))
                            set("city", Value.of(City[k]))
                            set("postalCode", Value.of(PostalCode[k]))
                            set("country", Value.of(Country[k]))
                        }
                    }
                }
            }

//            println(Customers.toBunch().toTable())
            CLI.goToMind(this)
            CLI.start()
        }

//        println(table)
    }
}