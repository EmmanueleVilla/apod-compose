package com.shadowings.apodcompose

import com.shadowings.apodcompose.redux.Action
import com.shadowings.apodcompose.redux.AppState
import com.shadowings.apodcompose.redux.store
import com.shadowings.apodcompose.redux.subscriptions
import org.reduxkotlin.StoreSubscription

class StoreInterface {
    private var subscription: StoreSubscription? = null

    /**
     * Subscribes to the store with two callbacks,
     * one for the states update
     * and one for the actions pass-through
     */
    fun sub(
        id: String,
        stateCallback: (state: AppState) -> Unit,
        actionCallback: (action: Action) -> Unit
    ) {
        subscription = store.subscribe {
            stateCallback.invoke(store.state)
        }
        stateCallback.invoke(store.state)
        subscriptions.add(Pair(id, actionCallback))
    }

    /**
     * Unsubscribes from the store
     */
    fun unsub(id: String) {
        subscriptions.removeAll { it.first == id }
        subscription?.invoke()
        subscription = null
    }

    /**
     * Dispatches the given action to the store
     */
    fun dispatchAction(action: Action) {
        store.dispatch(action)
    }
}
