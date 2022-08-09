package ir.smmh.serialization.json

import ir.smmh.lingu.Code
import ir.smmh.markup.Html
import ir.smmh.markup.Html.defaultMetadata
import ir.smmh.markup.Markup
import ir.smmh.util.FileUtil.open
import ir.smmh.util.FileUtil.touch
import ir.smmh.util.FileUtil.writeTo
import java.io.File

fun main() {
    (Markup.Document("json-syntax-highlighting-test") {
        heading("Syntax Highlighting Test") {
            heading("Basics") {
                codeBlock(JsonTest.getBasics().toCode())
            }
            heading("Enums") {
                codeBlock(JsonTest.getEnum().toCode())
            }
            heading("Parsing sample") {
                codeBlock(JsonTest.sample)
                codeBlock(Json.code(JsonTest.sample))
            }
            heading("From File") {
                val filename = "B:/Users/smmmcii/AppData/Roaming/.minecraft/launcher_profiles_.json"
                paragraph(link("File", filename))
                codeBlock(Code(File(filename)))
            }
        }
    }.generate(Html, defaultMetadata) writeTo touch("gen/html/syntax-highlighting/json-test.html")) open ""
}