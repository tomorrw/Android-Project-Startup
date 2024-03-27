package com.tomorrow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry

interface Route {
    val path: String
    val arguments: List<NamedNavArgument>?
    val component: @Composable (params: NavBackStackEntry) -> Unit
    val deepLinkPrefixes: List<String>

    /**
     * this function is used to create the full path of the route
     * forExample:  /patient/{id}?name={name}
     * */
    fun getFullPath(): String = path
        .plusIfNotNull(arguments
            ?.filter { !it.argument.isNullable }?.joinToString { "/{${it.name}}" }
        )
        .plusIfNotNull(arguments
            ?.filter { it.argument.isNullable }
            ?.let { arguments ->
                if (arguments.isNotEmpty()) arguments.joinToString(
                    prefix = "?",
                    separator = "&"
                ) { "${it.name}={${it.name}}" } else null
            }

        )

    fun getFullDeepLinkPaths() = deepLinkPrefixes.map { it + "/" + getFullPath() }

    data class NullableParam(val name: String, val value: Any?)

    /**
     * a general function to generate a route
     * for example:  /patient/1?name=yammine
     * basePath followed by all the params first followed by the nullable params
     * */
    fun generate(vararg params: Any, nullableParams: List<NullableParam>? = null): String =
        path
            .plusIfNotNull(params.joinToString { "/$it" })
            .plusIfNotNull(
                nullableParams
                    ?.filter { it.value != null }
                    ?.joinToString(prefix = "?", separator = "&") { "${it.name}=${it.value}" })

    private fun String.plusIfNotNull(other: Any?) = other?.let { this.plus(other) } ?: this
}

