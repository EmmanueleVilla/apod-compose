package com.shadowings.apodcompose.redux

import com.shadowings.apodcompose.deps.DepsState
import com.shadowings.apodcompose.detail.detailReducer
import com.shadowings.apodcompose.detail.detailTales
import com.shadowings.apodcompose.home.homeReducer
import com.shadowings.apodcompose.home.homeTales
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.reduxkotlin.applyMiddleware
import org.reduxkotlin.createThreadSafeStore
import org.reduxkotlin.middleware

/**
 * List of callback subscribed to store changes
 */
internal val subscriptions = mutableListOf<Pair<String, ((action: Action) -> Unit)>>()

/**
 * Root reducer
 */
private fun reducer(state: AppState, action: Any) = AppState(
    deps = DepsState(),
    home = homeReducer(state.home, action),
    detail = detailReducer(state.detail, action),
)

/**
 * Tales list
 */
private val tales: MutableList<suspend (action: Action, state: AppState) -> List<Action>> =
    mutableListOf(
        ::homeTales,
        ::detailTales
    )

/**
 * Tales middleware
 */
private fun buildMiddleware() = middleware<AppState> { store, next, action ->
    if (action is Action) {
        store.state.deps.log(action.toString())
    }

    next(action)
    subscriptions.forEach {
        MainScope().launch {
            it.second(action as Action)
        }
    }
    tales.forEach {
        MainScope().launch {
            try {
                it(action as Action, store.state).forEach { store.dispatch(it) }
            } catch (e: Exception) {
                store.state.deps.log(e.toString())
            }
        }
    }
}

/**
 * Store initialization
 */
var store = createThreadSafeStore(
    ::reducer,
    AppState(),
    applyMiddleware(buildMiddleware())
)