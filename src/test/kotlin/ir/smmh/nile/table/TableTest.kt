package ir.smmh.nile.table

import ir.smmh.lingu.Language
import ir.smmh.lingu.Language.Companion.lateFileExt
import ir.smmh.markup.Html
import ir.smmh.markup.Html.defaultMetadata
import ir.smmh.markup.Markdown
import ir.smmh.markup.NoMarkup
import ir.smmh.util.FileUtil.touch
import ir.smmh.util.FileUtil.writeTo
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

object TableTest {
    @Test
    fun testBasics() {
        Table().apply {
            val c = addColumn<Int>("c")
            add { c[it] = 7 }
            add { c[it] = 6 }
            println(this)
        }
    }

    @Test
    fun testChanges() {
        var mutations = 0
        Table().apply {
            changesToSchema.afterChange.add { mutations += 100 }
            val c = addColumn<Int>("c")
            changesToSize.afterChange.add { mutations += 1 }
            add { c[it] = 7 }
            add { c[it] = 6 }
            val v = sortedByColumn(c) { it!! }
            v.changesToView.afterChange.add { mutations += 10 }
            v.shuffle()
        }
        assertEquals(112, mutations)
    }

    @Test
    fun testSv() {
        setOf("test", "customers").forEach { title ->
            val table = Table.fromTsv(File("res/tsv/$title.tsv"))
            setOf(NoMarkup, Markdown, Html).forEach { ml ->
//                println(table.toMarkupTable().toString(ml))
                val fileExt = if (ml is Language.HasFileExt) ml.fileExt else "txt"
                val filename = Language.HasFileExt.bindFileExt("gen/$lateFileExt/$title.$lateFileExt", fileExt)
                table.toMarkupTable().toDocument(title).generate(ml, defaultMetadata) writeTo touch(filename) // open ""
            }
        }
    }
}