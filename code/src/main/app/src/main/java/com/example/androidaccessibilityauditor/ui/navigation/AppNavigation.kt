package com.example.androidaccessibilityauditor.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidaccessibilityauditor.ui.cart.CartScreen
import com.example.androidaccessibilityauditor.ui.coupon.CouponRoute

private const val TRANSITION_DURATION_MS = 400

/**
 * Root navigation graph for the app.
 *
 * All screen-to-screen wiring lives here. To add a new destination:
 *  1. Add a new [Screen] entry in Screen.kt
 *  2. Add a `composable { }` block below
 *  3. Pass a lambda that calls `navController.navigate(Screen.NewDest.route)`
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Cart.route,
    ) {
        composable(
            route = Screen.Cart.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(TRANSITION_DURATION_MS),
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(TRANSITION_DURATION_MS),
                )
            },
        ) {
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCoupon = { navController.navigate(Screen.ApplyCoupon.route) },
            )
        }
        composable(route = Screen.ApplyCoupon.route) {
            CouponRoute(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
