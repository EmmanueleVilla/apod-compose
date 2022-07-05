package com.shadowings.apodcompose.home


fun homeReducer(state: HomeState, action: Any): HomeState {
    return when (action) {
        is HomeActions.DataRetrieved -> state.copy(apods = action.data)
        else -> state
    }
}