package com.shadowings.apodcompose.redux

import com.shadowings.apodcompose.deps.DepsState
import com.shadowings.apodcompose.detail.DetailState
import com.shadowings.apodcompose.home.HomeState

/**
 * Application state
 * @param deps: dependencies functions
 * @param home: contains the list of the apods
 * @param detail: contains the detail apod
 */
data class AppState(
    val deps: DepsState = DepsState(),
    val home: HomeState = HomeState(),
    val detail: DetailState = DetailState(),
)
