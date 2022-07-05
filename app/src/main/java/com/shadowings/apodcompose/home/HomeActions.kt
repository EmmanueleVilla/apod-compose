package com.shadowings.apodcompose.home

import com.shadowings.apodcompose.redux.Action

/**
 * Home actions
 */
open class HomeActions : Action() {
    /**
     * Initialize the home page
     */
    class Init : HomeActions()

    /**
     * Called when the homepage data is ready
     */
    data class DataRetrieved(val data: List<ApodModel>) : HomeActions()
}