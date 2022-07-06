package com.shadowings.apodcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.shadowings.apodcompose.deps.CacheManager
import com.shadowings.apodcompose.deps.DepsActions
import com.shadowings.apodcompose.redux.Action
import com.shadowings.apodcompose.redux.AppState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit


/**
 * Main activity that shows the [ActivityComposable]
 */
class MainActivity : ComponentActivity() {
    private val appStateSubject: BehaviorSubject<AppState> =
        BehaviorSubject.createDefault(AppState())

    private var appState: AppState by mutableStateOf(AppState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(StoreInterface()) {
            dispatchAction(
                DepsActions.Init({
                    CacheManager().readFromCache(cacheDir, it)
                }, { key, content ->
                    CacheManager().writeToCache(cacheDir, key, content)
                })
            )
            sub("main_activity", ::handleAppState, ::handleAction)
        }

        val appLinkIntent = intent
        val appLinkData = appLinkIntent.data?.let {
            return@let it.toString().split("/").last()
        }


        setContent {
            ActivityComposable(appState, appLinkData ?: "")
        }

        appStateSubject
            .throttleLast(
                250,
                TimeUnit.MILLISECONDS,
                AndroidSchedulers.mainThread()
            )
            .subscribe(
                { this.runOnUiThread { appState = it } },
                { Log.e("apod", it.toString()) }
            )
    }

    private fun handleAppState(appState: AppState) {
        appStateSubject.onNext(appState)
    }

    private fun handleAction(action: Action) {
        // nothing to do here
    }
}