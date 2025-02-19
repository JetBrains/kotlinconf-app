import org.jetbrains.kotlinconf.backend.dropAfter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StringExtTest {

    @Test
    fun `dropAfter should drop everything after the chunk`() {
        val input = "This is a test string. Part 1"
        val chunk = ". Part"
        val expected = "This is a test string"
        assertEquals(expected, input.dropAfter(chunk))
    }

    @Test
    fun `dropAfter should return the original string if the chunk is not found`() {
        val input = "This is a test string"
        val chunk = ". Part"
        assertEquals(input, input.dropAfter(chunk))
    }

    @Test
    fun `dropAfter should handle empty string`() {
        val input = ""
        val chunk = ". Part"
        assertEquals("", input.dropAfter(chunk))
    }

    @Test
    fun `dropAfter should handle empty chunk`() {
        val input = "This is a test string"
        val chunk = ""
        assertEquals("", input.dropAfter(chunk))
    }

    @Test
    fun `dropAfter should handle chunk at the beginning`() {
        val input = ". Part 1 This is a test string"
        val chunk = ". Part"
        val expected = ""
        assertEquals(expected, input.dropAfter(chunk))
    }

    @Test
    fun `dropAfter should handle chunk at the end`() {
        val input = "This is a test string. Part"
        val chunk = ". Part"
        val expected = "This is a test string"
        assertEquals(expected, input.dropAfter(chunk))
    }

    @Test
    fun `dropAfter should handle multiple occurrences of the chunk`() {
        val input = "This is a test string. Part 1. Part 2"
        val chunk = ". Part"
        val expected = "This is a test string"
        assertEquals(expected, input.dropAfter(chunk))
    }
}