package org.jetbrains.kotlinconf.api

import io.ktor.client.HttpClient
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.HttpResponsePipeline
import io.ktor.http.isSuccess
import io.ktor.util.AttributeKey

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
