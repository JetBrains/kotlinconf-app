package org.jetbrains.kotlinconf.model

import io.ktor.util.date.GMTDate
import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import org.jetbrains.kotlinconf.parseDate
import org.jetbrains.kotlinconf.parseToString

object GMTDateSerializer : KSerializer<GMTDate> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl("io.ktor.util.date.GMTDate")

    override fun deserialize(input: Decoder): GMTDate = input.decodeString().parseDate()

    override fun serialize(output: Encoder, obj: GMTDate) {
        output.encodeString(obj.parseToString())
    }
}