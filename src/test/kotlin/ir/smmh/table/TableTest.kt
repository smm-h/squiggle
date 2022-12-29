package ir.smmh.table

import ir.smmh.lingu.Language
import ir.smmh.lingu.Language.Companion.lateFileExt
import ir.smmh.markup.Html
import ir.smmh.markup.Html.defaultMetadata
import ir.smmh.markup.Markdown
import ir.smmh.markup.NoMarkup
import ir.smmh.markup.TableBuilder.Companion.toMarkupTable
import ir.smmh.util.FileUtil.touch
import ir.smmh.util.FileUtil.writeTo
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

object TableTest {
    @Test
    fun testBasics() {
        val schema = ListSealableSchema<Int, Int>()
        val column = schema.createColumnIn<Int>()
        IntKeyedTable(schema).apply {
            add { column[it] = 7 }
            add { column[it] = 6 }
            println(this)
        }
    }

    @Test
    fun testChanges() {
        var changes = 0
        val schema = ListSealableSchema<Int, Int>()
        val column = schema.createColumnIn<Int>()
        IntKeyedTable(schema).apply {
            //schema.changesToSize.afterChange.add { changes += 100 }
            changesToSize
                .afterChange.add { changes += 1 }
            add { column[it] = 7 }
            add { column[it] = 6 }
//            keySet.sortedByColumn(column) { it!! }
//            keySet.changesToOrder
//                .afterChange.add { changes += 10 }
//            keySet.shuffle()
        }
        assertEquals(2, changes)
    }

    @Test
    fun testSv() {
        setOf("test", "customers").forEach { title ->
            val table = StringTable.tsv(File("res/tsv/$title.tsv"))
            setOf(NoMarkup, Markdown, Html).forEach { ml ->
                val fileExt = if (ml is Language.HasFileExt) ml.fileExt else "txt"
                val filename = Language.HasFileExt.bindFileExt("gen/$lateFileExt/$title.$lateFileExt", fileExt)
                table.toMarkupTable().toDocument(title).generate(ml, defaultMetadata) writeTo touch(filename) // open ""
            }
        }
    }
}