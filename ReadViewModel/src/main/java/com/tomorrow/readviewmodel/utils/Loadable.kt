package com.tomorrow.readviewmodel.utils

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