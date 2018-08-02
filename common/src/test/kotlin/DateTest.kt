package org.jetbrains.kotlinconf

import io.ktor.util.date.*
import kotlin.test.*

class DateTest {
    @Test
    fun testParse() {
        val date = parseDate("2017-10-24T13:31:19")
        assertEquals(2017, date.year)
        assertEquals(Month.OCTOBER, date.month)
        assertEquals(24, date.dayOfMonth)
        assertEquals(13, date.hours)
        assertEquals(31, date.minutes)
    }
}
