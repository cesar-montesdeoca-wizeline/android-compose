package com.example.androidaccessibilityauditor.ui.coupon

/** User interactions on the Coupon / Checkout screen. */
sealed interface CouponAction {
    /** User tapped the back arrow in the toolbar. */
    data object NavigateBack : CouponAction

    /** User tapped "Redimir" to apply the entered coupon code. */
    data object RedeemCoupon : CouponAction

    /** User tapped "Continuar" to proceed to the next checkout step. */
    data object ContinueCheckout : CouponAction
}
