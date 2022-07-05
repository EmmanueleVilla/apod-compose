package com.shadowings.apodcompose.home

import com.google.gson.Gson
import com.shadowings.apodcompose.redux.Action
import com.shadowings.apodcompose.redux.AppState
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

internal suspend fun homeTales(action: Action, state: AppState): List<Action> {
    return when (action) {
        is HomeActions.Init -> {
            val (_, httpClient, getDate) = state.deps
            handleInit(httpClient, getDate)
        }
        else -> listOf()
    }
}

private suspend fun handleInit(
    httpClient: HttpClient,
    getDate: (deltaDays: Int) -> LocalDate
): List<Action> {
    val date = getDate(-29)
    val url: String =
        "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY&thumbs=true&start_date=" +
                "${date.year}-${date.month.number}-${date.dayOfMonth}"
    val body: String = httpClient.get(url).body()
    val list = Gson().fromJson(body, Array<ApodPreview>::class.java).toList()
    return listOf(
        HomeActions.DataRetrieved(list)
    )
}
