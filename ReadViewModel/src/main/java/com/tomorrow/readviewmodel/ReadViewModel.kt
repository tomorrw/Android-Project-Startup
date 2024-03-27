package com.tomorrow.readviewmodel

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.tomorrow.readviewmodel.components.GeneralError
import com.tomorrow.readviewmodel.components.Loader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

open class ReadViewModel<D>(
    internal val load: () -> Flow<D>,
    internal val refresh: () -> Flow<D> = load,
    internal val emptyCheck: (D) -> Boolean = { false },
) : ViewModel() {
    var state by mutableStateOf(State<D>())
    private val scope = CoroutineScope(Dispatchers.Main)
    //used to load data from suspend functions
    constructor(
        suspendLoad: suspend () -> D,
        suspendRefresh: suspend () -> D = suspendLoad,
        emptyCheck: (D) -> Boolean = { false },
    ) : this(
        load = { flow { emit(suspendLoad()) } },
        refresh = { flow { emit(suspendRefresh()) } },
        emptyCheck = emptyCheck,
    )

    init {
        on(Event.Load)
    }

    fun on(event: Event) {
        Log.v("Read View Model Event", "$event")
        when (event) {
            Event.Load -> {
                scope.launch {
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
                scope.launch {
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
                scope.launch {
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

/**
 * has error handling and loading
 * */
@Composable
fun <D> DefaultReadView(
    viewModel: ReadViewModel<D>,
    loader: @Composable () -> Unit = { Loader() },
    error: @Composable (String) -> Unit = {
        GeneralError(
            modifier = Modifier.padding(16.dp),
            message = it,
            description = "Please check your internet connection and try again.",
            onButtonClick = { viewModel.on(ReadViewModel.Event.OnRefresh) },
        )
    },
    emptyState: @Composable () -> Unit = {
        GeneralError(
            modifier = Modifier.padding(16.dp),
            message = "No data found",
            description = "Stay Tuned for more updates!",
            onButtonClick = { viewModel.on(ReadViewModel.Event.OnRefresh) },
        )
    },
    view: @Composable (D) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel.state.error) {
        viewModel.state.error?.let {
            if (it.isNotEmpty()) Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    val viewData = viewModel.state.viewData

    if (viewModel.state.isLoading) loader()
    else if (viewModel.state.isEmpty && viewData is Loaded) emptyState()
    else if (viewData is Loaded) {
        view(viewData.get())
    } else if (viewModel.state.error != null) error(viewModel.state.error ?: "")
    else loader()
}


//until importing this below from shared library
sealed class Loadable<out T> {
    companion object {
        /**
         *
         *
         * This function returns Loaded<T>(data) if data is not null else returns NotLoaded(
         *
         * @param T the type of a member that you want to represent inside a loadable..
         */
        fun <T> smartInit(data: T? = null): Loadable<T> =
            if (data != null) Loaded(data) else NotLoaded()

    }

    fun getDataIfLoaded(): T? = if (this is Loaded) get() else null

    private val isLoaded = this is Loaded
    val isNotLoaded = !isLoaded
}

class Loaded<out T>(private val data: T) : Loadable<T>() {
    fun get(): T {
        return data
    }
}

class NotLoaded<out T> : Loadable<T>()
class MultipleFieldValidationError(
    val errors: Map<String, List<String>>,
    message: String = "fields are not valid"
) : Exception(message)

fun Throwable.toUserFriendlyError(): String = if (this is MultipleFieldValidationError) {
    this.errors.values.mapNotNull { it.firstOrNull() }.joinToString(", ")
} else this.message.let { if (!it.isNullOrBlank() && it.length < 50) it else "Something went wrong!" }