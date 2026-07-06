// ABOUTME: Builds a CSV from table rows and triggers a browser download.
// ABOUTME: Prepends a UTF-8 BOM so Excel opens non-ASCII feedback text correctly.
package org.jetbrains.kotlinconf.admin

import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

fun downloadCsv(filename: String, rows: List<List<String>>) {
    val text = rows.joinToString("\r\n") { row -> row.joinToString(",") { csvEscape(it) } }
    val blob = Blob(arrayOf<dynamic>("﻿$text"), BlobPropertyBag(type = "text/csv;charset=utf-8"))
    val url = URL.createObjectURL(blob)
    val anchor = document.createElement("a") as HTMLAnchorElement
    anchor.href = url
    anchor.setAttribute("download", filename)
    document.body?.appendChild(anchor)
    anchor.click()
    document.body?.removeChild(anchor)
    URL.revokeObjectURL(url)
}

private fun csvEscape(value: String): String = if (
    value.any { it == '"' || it == ',' || it == '\n' || it == '\r' }
) {
    "\"" + value.replace("\"", "\"\"") + "\""
} else {
    value
}
