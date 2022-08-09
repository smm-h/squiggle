package ir.smmh.autodoc

import ir.smmh.markup.Markup
import ir.smmh.util.FileUtil.open

fun main() {
//    Class.forName("").kotlin
    listOf(
        AutoDoc::class,
//        Nitron::class,
//        NiLex::class,
//        Language::class,
        Markup::class,
//        FatOr::class,
//        Blockchain::class,
//        SequentialImpl::class,
    ).forEach {
        AutoDoc.withHtml.getFile(it) open ""
    }
}