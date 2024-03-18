package org.jetbrains.kotlinconf.utils

import io.ktor.util.date.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

/**
 * According to mask: "yyyy-MM-dd'T'HH:mm:ss"
 */
object GMTDateSerializer : KSerializer<GMTDate> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: GMTDate) {
        with(value) {
            val monthPart = "${month.ordinal + 1}".padStart(2, '0')
            val dayString = dayOfMonth.toString(2)
            val hoursString = hours.toString(2)
            val minutesString = minutes.toString(2)
            val secondsString = seconds.toString(2)

            val result = "$year-$monthPart-${dayString}T$hoursString:$minutesString:$secondsString"
            encoder.encodeString(result)
        }
    }

    override fun deserialize(decoder: Decoder): GMTDate {
        val value = decoder.decodeString()
        with(value) {
            val year = substring(0, 4).toInt()
            val month = substring(5, 7).toInt()
            val day = substring(8, 10).toInt()

            val hour = substring(11, 13).toInt()
            val minute = substring(14, 16).toInt()
            val second = substring(17, 19).toInt()

            return GMTDate(second, minute, hour, day, Month.from(month - 1), year)
        }
    }
}

internal fun GMTDate.time(): String = "${hours.toString(2)}:${minutes.toString(2)}"

internal fun GMTDate.dayAndMonth(): String = "${month.value} $dayOfMonth"

private fun Int.toString(minSize: Int): String = "$this".padStart(minSize, '0')
