package com.shadowings.apodcompose.home

import com.shadowings.apodcompose.redux.Action

open class HomeActions : Action() {
    class Init : HomeActions()
    class DataRetrieved(val data: List<ApodPreview>) : HomeActions() {
        override fun toString(): String {
            return "HomeActions.DataRetrieved:\n${data.joinToString("\n")}"
        }
    }
}