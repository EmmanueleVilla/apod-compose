package com.shadowings.apodcompose.home

/**
 * Handles the home actions
 */
fun homeReducer(state: HomeState, action: Any): HomeState {
    return when (action) {
        is HomeActions.DataRetrieved -> state.copy(apods = action.data)
        else -> state
    }
}