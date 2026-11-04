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

// ── Stateless screen ─────────────────────────────────────────────────────────

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
