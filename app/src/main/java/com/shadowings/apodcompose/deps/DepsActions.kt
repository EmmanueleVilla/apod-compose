package com.shadowings.apodcompose.deps

import com.shadowings.apodcompose.redux.Action

/**
 * Deps actions
 */
open class DepsActions : Action() {
    /**
     * Init the deps state with the given dependencies
     */
    data class Init(
        val fromCache: (key: String) -> String?,
        val toCache: (key: String, content: String) -> Unit
    ) : DepsActions()
}