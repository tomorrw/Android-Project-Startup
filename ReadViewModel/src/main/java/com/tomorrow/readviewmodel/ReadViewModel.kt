package com.tomorrow.readviewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tomorrow.readviewmodel.utils.Loadable
import com.tomorrow.readviewmodel.utils.Loaded
import com.tomorrow.readviewmodel.utils.NotLoaded
import com.tomorrow.readviewmodel.utils.toUserFriendlyError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

open class ReadViewModel<D>(
    internal val load: () -> Flow<D>,
    internal val refresh: () -> Flow<D> = load,
    internal val onDismiss: suspend () -> Unit = {},
    internal val emptyCheck: (D) -> Boolean = { false },
) : ViewModel() {
    var state by mutableStateOf(State<D>())
    private val scope = CoroutineScope(Dispatchers.Main)
    //used to load data from suspend functions
    constructor(
        suspendLoad: suspend () -> D,
        suspendRefresh: suspend () -> D = suspendLoad,
        emptyCheck: (D) -> Boolean = { false },
        onDismiss: suspend () -> Unit = {},
    ) : this(
        load = { flow { emit(suspendLoad()) } },
        refresh = { flow { emit(suspendRefresh()) } },
        emptyCheck = emptyCheck,
        onDismiss = onDismiss,
    )

    init {
        on(Event.Load)
    }

    fun on(event: Event) {
        Log.v("Read View Model Event", "$event")
        when (event) {
            Event.Load -> {
                scope.launch(Dispatchers.Main) {
                    state = state.copy(isLoading = true, error = null)
                    try {
                        load().collect {
                            onDataReception(it)
                            state = state.copy(
                                isLoading = false,
                                isEmpty = emptyCheck(it),
                            )
                        }
                    } catch (e: Throwable) {
                        Log.e("Read View Model Error", "$e")
                        state = state.copy(error = e.toUserFriendlyError(), isLoading = false)
                    }
                }
            }

            Event.OnRefresh -> {
                scope.launch(Dispatchers.Main) {
                    state = state.copy(isRefreshing = true, error = null)
                    try {
                        refresh().collect {
                            onDataReception(it)
                            state = state.copy(
                                isRefreshing = false,
                                isEmpty = emptyCheck(it),
                            )
                        }
                    } catch (e: Throwable) {
                        Log.e("Read View Model Error", "$e")
                        state = state.copy(error = e.toUserFriendlyError(), isRefreshing = false)
                    }
                }
            }

            Event.ClearErrors -> {
                state = state.copy(error = null)
            }

            Event.LoadSilently -> {
                scope.launch(Dispatchers.Main) {
                    state = state.copy(error = null)
                    try {
                        load().collect {
                            onDataReception(it)
                            state = state.copy(isEmpty = emptyCheck(it))
                        }
                    } catch (e: Throwable) {
                        Log.e("Read View Model Error", "$e")
                        state = state.copy(error = e.toUserFriendlyError())
                    }
                }
            }

            Event.OnDismiss -> scope.launch { onDismiss() }
        }
    }

    open fun onDataReception(d: D) {
        state = state.copy(viewData = Loaded(d))
    }

    sealed class Event {
        data object OnRefresh : Event()
        data object Load : Event()
        data object ClearErrors : Event()
        data object LoadSilently : Event()
        data object OnDismiss : Event()
    }

    data class State<D>(
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val isEmpty: Boolean = false,
        val viewData: Loadable<D> = NotLoaded(),
        val error: String? = null,
    ) {
        fun copy(
            isLoading: Boolean = this.isLoading,
            isRefreshing: Boolean = this.isRefreshing,
            isEmpty: Boolean = this.isEmpty,
            viewData: D?,
            error: String? = this.error,
        ) = State(isLoading, isRefreshing, isEmpty, Loadable.smartInit(viewData), error)
    }
}
