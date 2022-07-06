package com.shadowings.apodcompose.home

import android.util.Log
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
            val (_, httpClient, getDate, _, fromCache, toCache) = state.deps
            handleInit(httpClient, getDate, fromCache, toCache)
        }
        else -> listOf()
    }
}

private suspend fun handleInit(
    httpClient: HttpClient,
    getDate: (deltaDays: Int) -> LocalDate,
    fromCache: (key: String) -> String?,
    toCache: (key: String, content: String) -> Unit
): List<Action> {
    val date = getDate(-9)
    val keyDate = "${date.year}-${date.month.number}-${date.dayOfMonth}"
    fromCache(keyDate)?.let {
        val list =
            Gson().fromJson(it, Array<ApodModel>::class.java).toList()
                .sortedByDescending { apod -> apod.date }
        return listOf(
            HomeActions.DataRetrieved(list)
        )
    }
    val url: String =
        "https://api.nasa.gov/planetary/apod?" +
                "api_key=l1Gq4scQZ6HjE17FT77oGIQWYNcOVZ99PmOQo5st" +
                "&thumbs=true&" +
                "start_date=${keyDate}"
    val body: String = httpClient.get(url).body()
    try {
        toCache(keyDate, body)
    } catch (e: Exception) {
        Log.w("apod", e)
    }
    val list =
        Gson().fromJson(body, Array<ApodModel>::class.java).toList().sortedByDescending { it.date }
    return listOf(
        HomeActions.DataRetrieved(list)
    )
}