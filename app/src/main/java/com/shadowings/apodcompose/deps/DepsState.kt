package com.shadowings.apodcompose.deps

import android.util.Log
import com.google.gson.Gson
import com.shadowings.apodcompose.redux.store
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import kotlinx.datetime.*

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
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
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