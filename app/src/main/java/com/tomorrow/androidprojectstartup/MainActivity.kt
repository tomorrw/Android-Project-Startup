package com.tomorrow.androidprojectstartup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tomorrow.androidprojectstartup.ui.theme.AndroidProjectStartupTheme
import com.tomorrow.navigation.Route
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.tomorrow.navigation.setUp
val LocalNavController = compositionLocalOf<NavHostController> {
    error("No LocalNavController provided")
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController: NavHostController = rememberNavController()

            LocalNavController provides navController
            AndroidProjectStartupTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                println("======== ${ AppRoute.OnBoarding.generate() }")

                }
            }
        }
    }
}





@Composable
fun onBoardingView() {
    val navController = LocalNavController.current
    Column {
        Text(text = "OnBoarding")
        Button(onClick = { navController.navigate(AppRoute.OnView.generate()) }) {
            Text(text = "on view")
        }

    }

}
@Composable
fun onView() {
    val navController = LocalNavController.current
    Column {
        Text(text = "View")
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Back")
        }
    }

}

sealed class AppRoute(
    override val path: String,
    override val arguments: List<NamedNavArgument>? = null,
    override val deepLinkPrefixes: List<String> = AppRoute.deepLinkPrefixes ,
    override val component: @Composable (params: NavBackStackEntry) -> Unit,
    val shouldBeAuthenticated: Boolean = false,
    val shouldDisplayBottomBar: Boolean = false,
) : Route {
    object OnBoarding : AppRoute(
        path = "on-boarding",
        component = { onBoardingView() },
        shouldBeAuthenticated = false
    )

    object OnView : AppRoute(
        path = "on-view",
        component = { onBoardingView() },
        shouldBeAuthenticated = false
    )





    companion object {
        private val deepLinkPrefixes: List<String> = listOf(
            "app://convenire",""
        )

        fun getNormalRouteFromDeepLink(deepLink: String): String? {
            val prefix = deepLinkPrefixes.map { "$it/" }.firstOrNull { deepLink.contains(it) } ?: return null
            return deepLink.substringAfter(prefix)
        }

        val allRoutes: List<AppRoute> =
            AppRoute::class.sealedSubclasses.mapNotNull { it.objectInstance }

        private fun fromString(string: String?): AppRoute? =
            if (string.isNullOrBlank()) null
            else allRoutes.find { it.getFullPath() == string }

        fun shouldDisplayBottomBar(string: String?): Boolean =
            fromString(string)?.shouldDisplayBottomBar == true

        fun shouldBeAuthenticated(string: String?): Boolean =
            fromString(string)?.shouldBeAuthenticated == true
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidProjectStartupTheme {
        Greeting("Android")
    }
}