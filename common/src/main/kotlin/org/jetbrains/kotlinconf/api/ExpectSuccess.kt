package org.jetbrains.kotlinconf.api

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.response.*
import io.ktor.http.*
import io.ktor.util.*

object ExpectSuccess : HttpClientFeature<Unit, ExpectSuccess> {
    override val key: AttributeKey<ExpectSuccess> = AttributeKey("ExpectSuccess")

    override fun prepare(block: Unit.() -> Unit): ExpectSuccess = this

    override fun install(feature: ExpectSuccess, scope: HttpClient) {
        scope.responsePipeline.intercept(HttpResponsePipeline.Receive) {
            val response = subject.response as HttpResponse
            if (!response.status.isSuccess()) throw ApiException(response)
            proceedWith(subject)
        }
    }
}

class ApiException(val response: HttpResponse) : Throwable()
