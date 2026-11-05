package com.example.androidaccessibilityauditor.ui.coupon

import androidx.lifecycle.ViewModel
import com.example.androidaccessibilityauditor.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for the Coupon / Checkout screen.
 *
 * Initialises [CouponUiState] from the repository at construction time:
 *  - Only selected [CartItem]s are included.
 *  - [subtotal] = sum of (product.totalPrice × quantity) for selected items.
 *  - [shipping] = 0.0 (static for this release).
 *  - [total] = subtotal + shipping.
 *
 * [onAction] is the single entry point for all UI-driven events.
 * [RedeemCoupon] and [ContinueCheckout] are no-op placeholders for this
 * release; the coupon success state is pre-applied in the initial state.
 */
@HiltViewModel
class CouponViewModel @Inject constructor(
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(buildInitialState())
    val uiState: StateFlow<CouponUiState> = _uiState.asStateFlow()

    // ── Single action entry point ────────────────────────────────────────────

    fun onAction(action: CouponAction) {
        when (action) {
            CouponAction.NavigateBack     -> { /* Handled by the caller via callback */ }
            CouponAction.RedeemCoupon     -> redeemCoupon()
            CouponAction.ContinueCheckout -> { /* No-op placeholder for this release */ }
        }
    }

    // ── State builder ────────────────────────────────────────────────────────

    private fun redeemCoupon() {
        _uiState.value = _uiState.value.copy(isCouponApplied = true)
    }

    private fun buildInitialState(): CouponUiState {
        val selectedItems = cartRepository.getCartItems().filter { it.isSelected }
        val subtotal = selectedItems.sumOf { it.product.totalPrice.toDouble() * it.quantity }
        val shipping = 0.0
        return CouponUiState(
            selectedItems = selectedItems,
            subtotal = subtotal,
            shipping = shipping,
            total = subtotal + shipping,
        )
    }
}
