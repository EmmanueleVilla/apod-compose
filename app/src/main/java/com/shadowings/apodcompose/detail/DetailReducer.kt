package com.shadowings.apodcompose.detail

fun detailReducer(state: DetailState, action: Any): DetailState {
    return when (action) {
        is DetailActions.DataRetrieved -> state.copy(apod = action.data)
        else -> state
    }
}