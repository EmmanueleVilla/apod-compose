package com.shadowings.apodcompose.deps

/**
 * Handles the deps actions
 */
fun depsReducer(state: DepsState, action: Any): DepsState {
    return when (action) {
        is DepsActions.Init -> state.copy(fromCache = action.fromCache, toCache = action.toCache)
        else -> state
    }
}