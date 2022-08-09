package ir.smmh.nitron

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language.Companion.lateFileExt
import ir.smmh.markup.Html
import ir.smmh.markup.Html.defaultMetadata
import ir.smmh.markup.Markup
import ir.smmh.serialization.json.Json
import ir.smmh.util.FileUtil
import ir.smmh.util.FileUtil.open
import ir.smmh.util.FileUtil.writeTo
import java.io.File

fun main() {
    val mindName = "Test"
    val sl = Json
    val ml = Html
    val sfn = "nitron/${lateFileExt}/$mindName.${lateFileExt}"
    val mfn = "nitron/serialization/$mindName.${lateFileExt}"

    Markup.Document {
        heading("Serialization of Mind: $mindName") {
            codeBlock(Code(File(sl.bindFileExt(sfn)), sl))
        }
    }.generate(ml, defaultMetadata) writeTo FileUtil.touch(ml.bindFileExt(mfn)) open ""
}