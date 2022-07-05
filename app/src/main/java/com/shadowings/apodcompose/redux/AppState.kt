package com.shadowings.apodcompose.redux

import com.shadowings.apodcompose.deps.DepsState
import com.shadowings.apodcompose.home.HomeState

data class AppState(
    val home: HomeState = HomeState(),
    val deps: DepsState = DepsState()
)