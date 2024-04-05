package com.test.cryptocoins.common


/**
 * Created by Chirag Sidhiwala on 04/04/24.
 */
interface UIState {
    data class Loading(
        val load: Boolean = true
    ) : UIState

    data class Success<out T>(val data: T) : UIState

    data class Error(val throwable: Throwable): UIState
}