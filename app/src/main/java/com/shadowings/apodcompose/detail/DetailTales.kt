package com.shadowings.apodcompose.detail

import com.google.gson.Gson
import com.shadowings.apodcompose.home.ApodModel
import com.shadowings.apodcompose.redux.Action
import com.shadowings.apodcompose.redux.AppState
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

internal suspend fun detailTales(action: Action, state: AppState): List<Action> {
    return when (action) {
        is DetailActions.Init -> {
            val (_, httpClient) = state.deps
            handleInit(state.home.apods, httpClient, action.date)
        }
        else -> listOf()
    }
}

private suspend fun handleInit(
    apods: List<ApodModel>,
    httpClient: HttpClient,
    date: String
): List<Action> {
    // If I'm opening the detail from the home, I already have the value saved there
    apods.firstOrNull { it.date == date }?.let {
        return listOf(
            DetailActions.DataRetrieved(it)
        )
    }

    // If it's a deep link, I need to download it
    val url: String =
        "https://api.nasa.gov/planetary/apod?" +
                "api_key=l1Gq4scQZ6HjE17FT77oGIQWYNcOVZ99PmOQo5st" +
                "&thumbs=true&" +
                "date=$date"
    val body: String = httpClient.get(url).body()
    val apod = Gson().fromJson(body, ApodModel::class.java)
    return listOf(
        DetailActions.DataRetrieved(apod)
    )
}