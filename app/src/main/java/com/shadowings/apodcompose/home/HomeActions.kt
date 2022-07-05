package com.shadowings.apodcompose.home

import com.shadowings.apodcompose.redux.Action

open class HomeActions : Action() {
    class Init : HomeActions()
    data class DataRetrieved(val data: List<ApodModel>) : HomeActions()
}