package org.jetbrains.kotlinconf

import io.ktor.util.date.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.*

/**
 * According to mask: "yyyy-MM-dd'T'HH:mm:ss"
 */
@Serializer(forClass = GMTDate::class)
internal object GMTDateSerializer : KSerializer<GMTDate> {
    override val descriptor: SerialDescriptor =
        StringDescriptor.withName("GMTDateDefaultSerializer")

    override fun serialize(encoder: Encoder, obj: GMTDate) {
        with(obj) {
            val monthPart = "${month.ordinal + 1}".padStart(2, '0')
            val dayString = dayOfMonth.toString(2)
            val hoursString = hours.toString(2)
            val minutesString = minutes.toString(2)
            val secondsString = seconds.toString(2)

            val value = "$year-$monthPart-${dayString}T$hoursString:$minutesString:$secondsString"
            encoder.encodeString(value)
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

internal fun GMTDate.dayAndMonth(): String = "$dayOfMonth ${month.value}"

private fun Int.toString(minSize: Int): String = "$this".padStart(minSize, '0')
