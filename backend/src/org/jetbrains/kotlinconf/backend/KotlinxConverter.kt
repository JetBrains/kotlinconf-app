package org.jetbrains.kotlinconf.backend

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.io.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*


class KotlinxConverter() : ContentConverter {
    override suspend fun convertForSend(
        context: PipelineContext<Any, ApplicationCall>,
        contentType: ContentType,
        value: Any
    ): Any? {
        val text = JSON.stringify(value::class.serializer() as SerializationStrategy<Any>, value)
        return TextContent(text, contentType.withCharset(context.call.suitableCharset()))
    }

    override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
        val request = context.subject
        val channel = request.value as? ByteReadChannel ?: return null
        val type = request.type
        val text = channel.readRemaining().readText()
        return JSON.parse(type.serializer(), text)
    }
}