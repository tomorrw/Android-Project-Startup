# Navigation

Use the navigation module and define your own routes and get the ability to navigate between them. + Deep linking is also supported.


## Usage
Inherit in your app the `Route` class and define your routes.

```kotlin
sealed class AppRoute(
    override val path: String,
    override val arguments: List<NamedNavArgument>? = null,
    override val deepLinkPrefixes: List<String>,
    override val component: @Composable (params: NavBackStackEntry) -> Unit,
    //💡 You could add your own custom properties in here
    val shouldBeAuthenticated: Boolean = true,
    val shouldDisplayBottomBar: Boolean = false,
) : Route {
//A Route Example
    object RouteName :
        AppRoute(
            path = "grandParent/Parent/",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                nullable = false
                defaultValue = null
            }),
            component = {
                val id = it.arguments?.getString("id")
                MyComponent(id)
            },
            shouldDisplayBottomBar = true
        ) {
        fun generateExplicit(date: String) =
            generate(nullableParams = listOf(Route.NullableParam("id", id)))
    }

    companion object {
        //deepLink Prefixes
        private val deepLinkPrefixes: List<String> = listOf(
            "app://convenire",...
        )
        //handle if the deepLink is not a normal route
        fun getNormalRouteFromDeepLink(deepLink: String): String? {
            val prefix = deepLinkPrefixes.map { "$it/" }.firstOrNull { deepLink.contains(it) } ?: return null
            return deepLink.substringAfter(prefix)
        }

    //custom functions
        val allRoutes: List<AppRoute> =
            AppRoute::class.sealedSubclasses.mapNotNull { it.objectInstance }

        private fun fromString(string: String?): AppRoute? =
            if (string.isNullOrBlank()) null
            else allRoutes.find { it.getFullPath() == string }

        fun shouldDisplayBottomBar(string: String?): Boolean =
            fromString(string)?.shouldDisplayBottomBar == true
    }
}
```

## Initialization
Setup Up all your routes in a `NavHost` to be able to navigate between them
```kotlin
import androidx.navigation.compose.NavHost

...

NavHost(
navController,
startDestination = AppRoute.OnBoarding.generate()
) {
//The setup function will generate all the routes with the appropriate arguments
    setUp(AppRoute.allRoutes) 
}
```

## Methods
Methods specific for routes

* `getFullPath()` - Get the full path of the route
* `generate()` - Generate the route with the given parameters
* `getFullDeepLinkPaths()` - Get the full deep link path of the route
