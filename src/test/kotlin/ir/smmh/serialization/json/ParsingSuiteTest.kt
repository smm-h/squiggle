package ir.smmh.serialization.json

import ir.smmh.markup.Html
import ir.smmh.markup.Html.attributes
import ir.smmh.markup.Html.defaultMetadata
import ir.smmh.markup.Markup
import ir.smmh.markup.truncate
import ir.smmh.nile.table.Table
import ir.smmh.util.FileUtil.open
import ir.smmh.util.FileUtil.touch
import ir.smmh.util.FileUtil.writeTo
import java.io.File

fun main() {

    val path = "F:/Timeline/3/JSONTestSuite-master/test_parsing"

    Markup.Document("results") {

        var total = 0
        var succeeded = 0

        val table = Table().run {
            val files = addColumn<File>("file")
            val texts = addColumn<String>("contents")
            val serializations = addColumn<String>("serialization")
            val deserializations = addColumn<Any?>("deserialization")
            val errors = addColumn<Throwable?>("error")
            val status = addColumn<Status>("status")

            File(path).walkTopDown().forEach { file ->
                if (file.isFile) {
                    this += {
                        val text = file.readText()
                        files[it] = file
                        texts[it] = text
                        try {
                            val value = Json.parse(text)
                            if ("structure_500_nested_arrays" in file.name) {
                                serializations[it] = "Was too big, omitted manually"
                            } else {
                                try {
                                    val sh = Html.SyntaxHighlighting.Impl("json")
                                    value.highlight(sh, 0)
                                    serializations[it] = sh.compile(true, 40)
                                } catch (e: Json.Exception) {
                                    throw e
                                } catch (e: Exception) {
                                    throw Json.Exception("syntax highlighting engine failed: $e")
                                }
                            }
                            deserializations[it] = value.deserialization
                        } catch (throwable: Throwable) {
                            errors[it] = throwable
                        }
                        val error = errors[it]
                        status[it] = when (file.name[0]) {
                            'y' -> {
                                when (error) {
                                    null -> Status.EXPECTED_RESULT
                                    is Json.Exception -> Status.SHOULD_HAVE_PASSED
                                    else -> Status.CRASH
                                }
                            }
                            'n' -> {
                                when (error) {
                                    null -> Status.SHOULD_HAVE_FAILED
                                    is Json.Exception -> Status.EXPECTED_RESULT
                                    else -> Status.CRASH
                                }
                            }
                            'i' -> {
                                when (error) {
                                    null -> Status.IMPLEMENTATION_PASS
                                    is Json.Exception -> Status.IMPLEMENTATION_FAIL
                                    else -> Status.CRASH
                                }
                            }
                            else -> Status.NONE
                        }
                    }
                }
            }

            val v = view()

//            v.filterBy { !((columnStatus[it]?.isTerrible) ?: false) }
            v.sortByColumn(errors) { it?.message?.hashCode() ?: -1 }
            v.sortByColumn(status) { it?.ordinal ?: -1 }

            status.forEach {
                if (it != null) {
                    if (it != Status.IMPLEMENTATION_PASS && it != Status.IMPLEMENTATION_FAIL) {
                        total++
                        if (it == Status.EXPECTED_RESULT) succeeded++
                    }
                }
            }

            v.toMarkupTable {
                val orange = attributes("style" to "color:orange")
                val red = attributes("style" to "color:red")

                makeFragment(files) { link(it.name, it.toURI().toString()) }
                makeFragment(texts) { code(it.truncate(64)) }
                makeFragment(serializations) { atom(it, false) } // code(it.truncate(64))
                makeFragment(deserializations) { code(it.toString().truncate(64)) }
                makeFragment(errors) { span(it.toString(), if (it is Json.Exception) orange else red) }
                makeFragment(status) { atom(it.message) }
                makeHyperdata(status) { "style=\"background-color:${it.color}\"" }

            }
        }
        heading("Results") {
            paragraph("Success rate: $succeeded/$total = ${100.0 * succeeded / total}%")
            addSection(table)
        }
        (Html.compile(this, defaultMetadata) writeTo touch("gen/html/json4kotlin-results.html")) open ""
    }
}

enum class Status(val message: String, val color: String, val isTerrible: Boolean = false) {
    EXPECTED_RESULT("expected result", "#CCFFCC"),
    SHOULD_HAVE_PASSED("parsing should have succeeded but failed", "#CC6600", true),
    SHOULD_HAVE_FAILED("parsing should have failed but succeeded", "#FFCC33"),
    IMPLEMENTATION_PASS("result undefined, parsing succeeded", "#66CCFF"),
    IMPLEMENTATION_FAIL("result undefined, parsing failed", "#0066FF"),
    CRASH("parser crashed", "#FF3333", true),
    TIMEOUT("timeout", "#666666", true),
    NONE("no status", "black", true),
}
