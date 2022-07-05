package com.shadowings.apodcompose.deps

import android.util.Log
import com.google.gson.Gson
import com.shadowings.apodcompose.redux.store
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import kotlinx.datetime.*

const val TIMEOUT_MS = 30_000L

/**
 * Functions used by the tails
 * @param log: logger method
 * @param httpClient: http client for the requests
 * @param getDate: returns the date with the given offset in days
 * @param formattedToday: return today in the format yyyy-mm-dd
 */
data class DepsState(
    val log: (message: String) -> Unit = { Log.d("apod", it) },
    val httpClient: HttpClient = HttpClient {
        defaultRequest {
            header("Connection", "close")
            header("Content-Type", "application/json")
        }
        install(ContentNegotiation) {
            Gson()
        }
        install(HttpCache)
        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT_MS
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    store.state.deps.log("httplog: $message")
                }
            }
            level = LogLevel.INFO
        }
    },
    val getDate: (deltaDays: Int) -> LocalDate = {
        (Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date).plus(
            it,
            DateTimeUnit.DAY
        )
    },
    val formattedToday: () -> String = {
        val date = getDate(0)
        "${date.year}-${date.month.number}-${date.dayOfMonth}"
    }
)