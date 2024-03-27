package com.tomorrow.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

fun NavGraphBuilder.setUp(routes: List<Route>) {
    routes.forEach { route ->
        composable(
            route = route.getFullPath(),
            arguments = route.arguments ?: listOf(),
            deepLinks = route.getFullDeepLinkPaths().map { navDeepLink { uriPattern = it } }
        ) { route.component(it) }
    }
}
