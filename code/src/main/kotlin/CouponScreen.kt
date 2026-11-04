package com.example.androidaccessibilityauditor.ui.coupon

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.androidaccessibilityauditor.R
import com.example.androidaccessibilityauditor.data.model.CartItem
import com.example.androidaccessibilityauditor.ui.components.AppPrimaryButton
import com.example.androidaccessibilityauditor.ui.components.AppTopBar
import com.example.androidaccessibilityauditor.ui.theme.BodyBold
import com.example.androidaccessibilityauditor.ui.theme.BodySmall
import com.example.androidaccessibilityauditor.ui.theme.CouponSuccessBackground
import com.example.androidaccessibilityauditor.ui.theme.CouponSuccessBorder
import com.example.androidaccessibilityauditor.ui.theme.CouponSuccessText
import com.example.androidaccessibilityauditor.ui.theme.GSVCBodyContainer
import com.example.androidaccessibilityauditor.ui.theme.GSVCBorderDivider
import com.example.androidaccessibilityauditor.ui.theme.GSVCHeadSecondary
import com.example.androidaccessibilityauditor.ui.theme.GSVCSmall
import com.example.androidaccessibilityauditor.ui.theme.GSVCSurfaceSecondary
import com.example.androidaccessibilityauditor.ui.theme.GSVCTextDisable
import com.example.androidaccessibilityauditor.ui.theme.GSVCTextPrimary
import com.example.androidaccessibilityauditor.ui.theme.HeadingBold
import com.example.androidaccessibilityauditor.ui.theme.HelperBold
import com.example.androidaccessibilityauditor.ui.theme.LabelSubtitle
import com.example.androidaccessibilityauditor.ui.theme.LabelSubtitleSemibold

// ── Route composable ─────────────────────────────────────────────────────────

/**
 * Route entry point for the Coupon / Checkout screen.
 *
 * Obtains the Hilt-managed [CouponViewModel], collects its state lifecycle-
 * aware, and delegates all rendering to the stateless [CouponScreen].
 * Navigation callbacks are passed in from [AppNavigation] so the ViewModel
 * is not coupled to the navigation layer.
 */
@Composable
fun CouponRoute(
    onNavigateBack: () -> Unit,
) {
    val viewModel: CouponViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CouponScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
    )
}

// ── Stateless screen ─────────────────────────────────────────────────────────

/**
 * Stateless Coupon / Checkout screen.
 *
 * Receives [uiState], [onAction], and [onNavigateBack] from [CouponRoute].
 * All child composables receive plain values and lambdas; none call
 * [hiltViewModel] directly.
 */
@Composable
fun CouponScreen(
    uiState: CouponUiState,
    onAction: (CouponAction) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(GSVCBodyContainer),
    ) {
        // ── Fixed top bar ──────────────────────────────────────────────────
        AppTopBar(
            title = stringResource(R.string.coupon_toolbar_title),
            onNavigateBack = onNavigateBack,
        )

        // ── Scrollable body ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Section title
            Text(
                text = stringResource(R.string.coupon_section_order),
                style = HeadingBold,
                color = GSVCTextPrimary,
                modifier = Modifier.fillMaxWidth(),
            )

            // Card 1: Order summary
            OrderSummaryCard(uiState = uiState)

            // Card 2: Coupon
            CouponCard(uiState = uiState, onAction = onAction)

            // Card 3: Payment breakdown
            PaymentBreakdownCard(uiState = uiState)
        }

        // ── Fixed bottom checkout bar ──────────────────────────────────────
        BottomCheckoutBar(uiState = uiState, onAction = onAction)
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Card 1 — Order Summary
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun OrderSummaryCard(uiState: CouponUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = GSVCSmall,
        colors = CardDefaults.cardColors(containerColor = GSVCSurfaceSecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            uiState.selectedItems.forEachIndexed { index, item ->
                OrderSummaryItem(item = item)
                if (index < uiState.selectedItems.lastIndex) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Subtotal row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.coupon_breakdown_total),
                    style = HeadingBold,
                    color = GSVCTextPrimary,
                )
                Text(
                    text = stringResource(R.string.cart_total_format, uiState.subtotal.toInt()),
                    style = HeadingBold,
                    color = GSVCTextPrimary,
                )
            }
        }
    }
}

@Composable
private fun OrderSummaryItem(item: CartItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        // Product image
        androidx.compose.foundation.Image(
            painter = painterResource(id = item.product.imageRes),
            contentDescription = item.product.name,
            modifier = Modifier.size(64.dp),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Seller
            Text(
                text = stringResource(R.string.cart_seller),
                style = LabelSubtitle,
                color = GSVCTextDisable,
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Product name
            Text(
                text = item.product.name,
                style = BodySmall,
                color = GSVCTextPrimary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            // SKU
            Text(
                text = stringResource(R.string.cart_sku_format, item.product.sku),
                style = LabelSubtitle,
                color = GSVCTextDisable,
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Quantity and prices row
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Quantity badge
                Text(
                    text = "Cant. ${item.quantity}",
                    style = HelperBold,
                    color = GSVCTextPrimary,
                )
                Spacer(modifier = Modifier.weight(1f))
                // Struck-through original price
                Text(
                    text = stringResource(
                        R.string.cart_total_format,
                        item.product.originalPrice * item.quantity,
                    ),
                    style = BodySmall.copy(textDecoration = TextDecoration.LineThrough),
                    color = GSVCTextDisable,
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Financed price
                Text(
                    text = stringResource(
                        R.string.cart_total_format,
                        item.product.totalPrice * item.quantity,
                    ),
                    style = BodyBold,
                    color = GSVCTextPrimary,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Loan detail
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$${item.product.amountPerWeek}",
                    style = LabelSubtitleSemibold,
                    color = GSVCTextPrimary,
                )
                Text(
                    text = " durante ",
                    style = LabelSubtitle,
                    color = GSVCTextPrimary,
                )
                Text(
                    text = "${item.product.loanWeeks}",
                    style = LabelSubtitleSemibold,
                    color = GSVCTextPrimary,
                )
                Text(
                    text = " semanas",
                    style = LabelSubtitle,
                    color = GSVCTextPrimary,
                )
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Card 2 — Coupon
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun CouponCard(
    uiState: CouponUiState,
    onAction: (CouponAction) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = GSVCSmall,
        colors = CardDefaults.cardColors(containerColor = GSVCSurfaceSecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row: icon + label
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = null,
                    tint = GSVCTextPrimary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.cart_coupon),
                    style = BodySmall,
                    color = GSVCTextPrimary,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Input + Redimir button row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Custom coupon code input box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(39.dp)
                        .border(width = 1.dp, color = GSVCBorderDivider, shape = GSVCSmall)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    BasicTextField(
                        value = uiState.couponCode,
                        onValueChange = { /* read-only for this release */ },
                        textStyle = HelperBold.copy(color = GSVCTextPrimary),
                        singleLine = true,
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Redimir button
                AppPrimaryButton(
                    text = stringResource(R.string.coupon_button_redeem),
                    onClick = { onAction(CouponAction.RedeemCoupon) },
                )
            }

            // Success banner (always visible per spec: isCouponApplied = true)
            if (uiState.isCouponApplied) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = CouponSuccessBackground, shape = GSVCSmall)
                        .border(width = 1.dp, color = CouponSuccessBorder, shape = GSVCSmall)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = uiState.discountLabel,
                        style = HelperBold,
                        color = CouponSuccessText,
                    )
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Card 3 — Payment Breakdown
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun PaymentBreakdownCard(uiState: CouponUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = GSVCSmall,
        colors = CardDefaults.cardColors(containerColor = GSVCSurfaceSecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Centered title
            Text(
                text = stringResource(R.string.coupon_breakdown_title),
                style = BodySmall,
                color = GSVCTextPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtotal row
            BreakdownRow(
                label = stringResource(R.string.coupon_breakdown_subtotal),
                value = stringResource(R.string.cart_total_format, uiState.subtotal.toInt()),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Shipping row
            BreakdownRow(
                label = stringResource(R.string.coupon_breakdown_shipping),
                value = stringResource(R.string.coupon_shipping_cost_default),
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Total row
            BreakdownRow(
                label = stringResource(R.string.coupon_breakdown_total),
                value = stringResource(R.string.cart_total_format, uiState.total.toInt()),
            )
        }
    }
}

@Composable
private fun BreakdownRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = BodySmall, color = GSVCTextPrimary)
        Text(text = value, style = BodySmall, color = GSVCTextPrimary)
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Bottom checkout bar
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun BottomCheckoutBar(
    uiState: CouponUiState,
    onAction: (CouponAction) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GSVCHeadSecondary)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left: totals and item count
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.cart_total_format, uiState.total.toInt()),
                    style = BodyBold,
                    color = GSVCTextPrimary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Strike-through original subtotal (before any discount)
                Text(
                    text = stringResource(R.string.cart_total_format, uiState.subtotal.toInt()),
                    style = BodySmall.copy(textDecoration = TextDecoration.LineThrough),
                    color = GSVCTextDisable,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(
                    R.string.cart_count_format,
                    uiState.selectedItems.sumOf { it.quantity },
                ),
                style = LabelSubtitle,
                color = GSVCTextPrimary,
            )
        }

        // Right: Continue button
        AppPrimaryButton(
            text = stringResource(R.string.cart_continue),
            onClick = { onAction(CouponAction.ContinueCheckout) },
            enabled = uiState.selectedItems.isNotEmpty(),
        )
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Shared divider
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun HorizontalDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(GSVCBorderDivider),
    )
}

// ────────────────────────────────────────────────────────────────────────────
// Previews
// ────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CouponScreenPreview() {
    CouponScreen(
        uiState = CouponUiState(),
        onAction = {},
        onNavigateBack = {},
    )
}