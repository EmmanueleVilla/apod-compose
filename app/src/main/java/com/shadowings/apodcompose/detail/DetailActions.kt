package com.shadowings.apodcompose.detail

import com.shadowings.apodcompose.home.ApodModel
import com.shadowings.apodcompose.redux.Action

/**
 * Detail actions
 */
open class DetailActions : Action() {
    /**
     * Init the detail state with the given date
     */
    data class Init(val date: String) : DetailActions()

    /**
     * Called when the detail has been retrieved
     */
    data class DataRetrieved(val data: ApodModel) : DetailActions()
}