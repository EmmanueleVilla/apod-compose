package com.shadowings.apodcompose.redux

import com.shadowings.apodcompose.deps.DepsState
import com.shadowings.apodcompose.detail.DetailState
import com.shadowings.apodcompose.home.HomeState

data class AppState(
    val deps: DepsState = DepsState(),
    val home: HomeState = HomeState(),
    val detail: DetailState = DetailState(),
)