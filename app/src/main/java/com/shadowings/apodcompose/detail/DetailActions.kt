package com.shadowings.apodcompose.detail

import com.shadowings.apodcompose.home.ApodModel
import com.shadowings.apodcompose.redux.Action

open class DetailActions : Action() {
    data class Init(val date: String) : DetailActions()
    data class DataRetrieved(val data: ApodModel) : DetailActions()
}