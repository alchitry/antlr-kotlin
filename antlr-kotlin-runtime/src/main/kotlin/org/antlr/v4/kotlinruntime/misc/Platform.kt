package org.antlr.v4.kotlinruntime.misc

import java.io.File

object Platform {
    fun readFile(fileName: String): String {
        return File(fileName)
            .readLines()
            .joinToString("\n")
    }
}
