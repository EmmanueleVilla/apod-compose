package com.shadowings.apodcompose.detail

import com.shadowings.apodcompose.home.ApodModel
import com.shadowings.apodcompose.redux.Action

open class DetailActions : Action() {
    class Init(val date: String) : DetailActions()
    class DataRetrieved(val data: ApodModel) : DetailActions() {
        override fun toString(): String {
            return "DetailActions.DataRetrieved:\n${data}"
        }
    }
}