package com.example.androidaccessibilityauditor.ui.navigation

/**
 * Sealed class representing every destination in the app.
 *
 * Adding a new screen only requires adding a new object here and a matching
 * composable inside AppNavigation.kt – nothing else needs to change.
 */
sealed class Screen(val route: String) {
    data object Cart : Screen("cart")
    data object ApplyCoupon : Screen("apply_coupon")
}
