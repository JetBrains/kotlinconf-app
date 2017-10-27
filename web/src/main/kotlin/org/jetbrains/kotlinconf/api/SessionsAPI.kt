package org.jetbrains.kotlinconf.api

import org.jetbrains.kotlinconf.SessionModel
import org.jetbrains.kotlinconf.data.AllData
import org.jetbrains.kotlinconf.data.Session
import org.w3c.dom.WebSocket
import kotlin.browser.window
import kotlin.js.JSON
import kotlinx.serialization.json.JSON as KJSON

class SessionsAPI(private val baseUrl: String, private val baseWsUrl: String) {
    private suspend fun fetchAll(): AllData {
        val rawData = httpGet("$baseUrl/all")
        return KJSON.parse<AllData>(rawData)
    }

    suspend fun fetchSessions(): List<Session> = fetchAll().sessions ?: emptyList()

    suspend fun fetchSession(id: String): SessionModel? {
        val all = fetchAll()
        return SessionModel.forSession(all, id)
    }

    fun subscribeToVotes(id: String, callback: (Votes?) -> Unit): VotesSubscription {
        var currentWs: WebSocket? = null
        var closing = false
        fun connect() {
            if (closing) return
            println("Connected to websocket")
            val ws = WebSocket("$baseWsUrl/sessions/$id/votes")
            ws.onopen = {
                currentWs = ws
                ws.onmessage = { event ->
                    callback(JSON.parse(event.asDynamic().data.unsafeCast<String>()))
                }
                ws.onclose = {
                    println("Disconnected from websocket")
                    window.setTimeout({ connect() }, 1000)
                }
                null
            }
            ws.onerror = {
                window.setTimeout({ connect() }, 1000)
            }
        }
        connect()

        return object : VotesSubscription {
            override fun close() {
                println("Connection closed")
                closing = true
                currentWs?.close()
            }
        }
    }
}


