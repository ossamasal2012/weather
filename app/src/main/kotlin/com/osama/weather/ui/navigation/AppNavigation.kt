package com.osama.weather.ui.navigation

import android.app.Application
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.osama.weather.ui.screens.airquality.AirQualityScreen
import com.osama.weather.ui.screens.details.AdvancedDetailsScreen
import com.osama.weather.ui.screens.home.HomeScreen
import com.osama.weather.ui.screens.home.HomeViewModel
import com.osama.weather.ui.screens.privacy.PrivacyPolicyScreen
import com.osama.weather.ui.screens.search.SearchScreen
import com.osama.weather.ui.screens.search.SearchViewModel
import com.osama.weather.ui.screens.settings.SettingsScreen

private object Routes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val AIR_QUALITY = "air_quality"
    const val ADVANCED_DETAILS = "advanced_details"
    const val SETTINGS = "settings"
    const val PRIVACY_POLICY = "privacy_policy"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as Application
    val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)

    // A single shared HomeViewModel, hoisted here so location/weather/air-quality
    // data loaded once on the home screen is reused by every other screen
    // instead of being re-fetched.
    val homeViewModel: HomeViewModel = viewModel(factory = factory)

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onSearchClick = { navController.navigate(Routes.SEARCH) },
                onAirQualityClick = { navController.navigate(Routes.AIR_QUALITY) },
                onAdvancedDetailsClick = { navController.navigate(Routes.ADVANCED_DETAILS) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(
            route = Routes.SEARCH,
            enterTransition = { fadeIn(tween(220)) + slideInVertically(tween(260), initialOffsetY = { it / 10 }) },
            exitTransition = { fadeOut(tween(160)) },
            popEnterTransition = { fadeIn(tween(160)) },
            popExitTransition = { fadeOut(tween(200)) + slideOutVertically(tween(220), targetOffsetY = { it / 10 }) }
        ) {
            val searchViewModel: SearchViewModel = viewModel(factory = factory)
            SearchScreen(
                viewModel = searchViewModel,
                onLocationSelected = { location ->
                    homeViewModel.selectLocation(location)
                    navController.popBackStack()
                },
                onUseCurrentLocation = {
                    homeViewModel.useCurrentLocation()
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.AIR_QUALITY,
            enterTransition = { fadeIn(tween(220)) + slideInVertically(tween(260), initialOffsetY = { it / 10 }) },
            exitTransition = { fadeOut(tween(160)) },
            popEnterTransition = { fadeIn(tween(160)) },
            popExitTransition = { fadeOut(tween(200)) + slideOutVertically(tween(220), targetOffsetY = { it / 10 }) }
        ) {
            AirQualityScreen(homeViewModel = homeViewModel, onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.ADVANCED_DETAILS,
            enterTransition = { fadeIn(tween(220)) + slideInVertically(tween(260), initialOffsetY = { it / 10 }) },
            exitTransition = { fadeOut(tween(160)) },
            popEnterTransition = { fadeIn(tween(160)) },
            popExitTransition = { fadeOut(tween(200)) + slideOutVertically(tween(220), targetOffsetY = { it / 10 }) }
        ) {
            AdvancedDetailsScreen(homeViewModel = homeViewModel, onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.SETTINGS,
            enterTransition = { fadeIn(tween(220)) + slideInVertically(tween(260), initialOffsetY = { it / 10 }) },
            exitTransition = { fadeOut(tween(160)) },
            popEnterTransition = { fadeIn(tween(160)) },
            popExitTransition = { fadeOut(tween(200)) + slideOutVertically(tween(220), targetOffsetY = { it / 10 }) }
        ) {
            SettingsScreen(
                homeViewModel = homeViewModel,
                onBack = { navController.popBackStack() },
                onPrivacyPolicyClick = { navController.navigate(Routes.PRIVACY_POLICY) }
            )
        }

        composable(
            route = Routes.PRIVACY_POLICY,
            enterTransition = { fadeIn(tween(220)) + slideInVertically(tween(260), initialOffsetY = { it / 10 }) },
            exitTransition = { fadeOut(tween(160)) },
            popEnterTransition = { fadeIn(tween(160)) },
            popExitTransition = { fadeOut(tween(200)) + slideOutVertically(tween(220), targetOffsetY = { it / 10 }) }
        ) {
            PrivacyPolicyScreen(homeViewModel = homeViewModel, onBack = { navController.popBackStack() })
        }
    }
}
