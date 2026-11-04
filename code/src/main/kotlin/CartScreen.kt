package com.example.androidaccessibilityauditor.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidaccessibilityauditor.R
import com.example.androidaccessibilityauditor.data.model.CartItem
import com.example.androidaccessibilityauditor.ui.theme.BodyBold
import com.example.androidaccessibilityauditor.ui.theme.BodySmall
import com.example.androidaccessibilityauditor.ui.theme.GSVCBorderDivider
import com.example.androidaccessibilityauditor.ui.theme.GSVCBodyContainer
import com.example.androidaccessibilityauditor.ui.theme.GSVCCardPrimary
import com.example.androidaccessibilityauditor.ui.theme.GSVCHeadSecondary
import com.example.androidaccessibilityauditor.ui.theme.GSVCSurfaceSecondary
import com.example.androidaccessibilityauditor.ui.theme.GSVCSmall
import com.example.androidaccessibilityauditor.ui.theme.GSVCTextDisable
import com.example.androidaccessibilityauditor.ui.theme.GSVCTextInvert
import com.example.androidaccessibilityauditor.ui.theme.GSVCTextPrimary
import com.example.androidaccessibilityauditor.ui.theme.H4
import com.example.androidaccessibilityauditor.ui.theme.HeadingBold
import com.example.androidaccessibilityauditor.ui.theme.HelperBold
import com.example.androidaccessibilityauditor.ui.theme.Interactions
import com.example.androidaccessibilityauditor.ui.theme.LabelSubtitle
import com.example.androidaccessibilityauditor.ui.theme.LabelSubtitleSemibold
import com.example.androidaccessibilityauditor.ui.theme.Orange

// ── Checkbox icon resource IDs ───────────────────────────────────────────────
// R.drawable.ic_checkbox_filled  → Orange fill / white check
// R.drawable.ic_checkbox_outline → white fill / Orange outline
// These are vector drawables bundled with material-icons-extended.
// We use painter resources referencing the Material checkbox icons via the
// Icons API converted to ImageVector and then to Painter below.

@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCoupon: () -> Unit = {},
    cartViewModel: CartViewModel = viewModel(factory = CartViewModel.Factory),
) {
    val uiState by cartViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(GSVCBodyContainer),
    ) {
        // ── Toolbar ────────────────────────────────────────────────────────
        CartToolbar(onNavigateBack = onNavigateBack)

        // ── Select All row ─────────────────────────────────────────────────
        SelectAllRow(
            isChecked = uiState.isSelectAllChecked,
            onToggle = { cartViewModel.onAction(CartAction.ToggleSelectAll) },
        )

        // ── Divider ────────────────────────────────────────────────────────
        CartDivider()

        // ── Scrollable body ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .background(GSVCBodyContainer)
                .padding(16.dp),
        ) {
            if (uiState.isEmpty) {
                EmptyCartContent()
            } else {
                CartBodyContent(
                    uiState = uiState,
                    onAction = { cartViewModel.onAction(it) },
                    onNavigateToCoupon = onNavigateToCoupon,
                )
            }
        }

        // ── Bottom summary area ────────────────────────────────────────────
        BottomSummaryArea(
            total = uiState.total,
            subtotal = uiState.subtotal,
            selectedUnitCount = uiState.selectedUnitCount,
            onContinue = { /* No action defined */ },
        )
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Toolbar
// ────────────────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun CartToolbar(onNavigateBack: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GSVCHeadSecondary)
            .padding(end = 16.dp)
            .padding(top = 4.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.cd_navigate_back),
                tint = Orange,
            )
        }
        Text(
            text = stringResource(R.string.cart_title),
            style = H4,
            color = GSVCTextPrimary,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}


// ────────────────────────────────────────────────────────────────────────────
// Cart body content
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun CartBodyContent(
    uiState: CartUiState,
    onAction: (CartAction) -> Unit,
    onNavigateToCoupon: () -> Unit,
) {
    // Section heading
    Text(
        text = stringResource(R.string.cart_section_heading),
        style = HeadingBold,
        color = GSVCTextPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
    )

    // Product list card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = GSVCSmall,
        colors = CardDefaults.cardColors(containerColor = GSVCSurfaceSecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
            // Card header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.cart_promo_label_prefix),
                    style = BodySmall,
                    color = GSVCTextPrimary,
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = stringResource(R.string.cart_promo_label_suffix),
                    style = HelperBold,
                    color = Orange,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            CartDivider()

            // Product items
            uiState.items.forEachIndexed { index, item ->
                ProductItem(
                    item = item,
                    onToggleSelection = { onAction(CartAction.ToggleItemSelection(item.id)) },
                    onRemove = { onAction(CartAction.RemoveItem(item.id)) },
                    onQuantityChange = { qty -> onAction(CartAction.UpdateQuantity(item.id, qty)) },
                )
//                if (index < uiState.items.lastIndex) {
//                    CartDivider()
//                }
            }
        }
    }

    // Store pickup card
    Spacer(modifier = Modifier.height(16.dp))
    StorePickupCard(
        isOn = uiState.isStorePickupOn,
        onToggle = { onAction(CartAction.ToggleStorePickup) },
    )

    // Coupon card
    Spacer(modifier = Modifier.height(16.dp))
    CouponCard(onClick = onNavigateToCoupon)

    // Warranty card
    Spacer(modifier = Modifier.height(16.dp))
    WarrantyCard()
}

// ────────────────────────────────────────────────────────────────────────────
// Product item
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProductItem(
    item: CartItem,
    onToggleSelection: () -> Unit,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        // Sub-section 1: seller, image, name/SKU, selection icon
        ProductItemTopSection(
            item = item,
            onToggleSelection = onToggleSelection,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sub-section 2: trash, quantity, prices, loan detail
        ProductItemBottomSection(
            item = item,
            onRemove = onRemove,
            onQuantityChange = onQuantityChange,
        )
    }
}

@Composable
private fun ProductItemTopSection(
    item: CartItem,
    onToggleSelection: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        // Left column: seller label + image + name/SKU
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.cart_seller),
                style = LabelSubtitle,
                color = GSVCTextDisable,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Top) {
                // Product image
                androidx.compose.foundation.Image(
                    painter = painterResource(id = item.product.imageRes),
                    contentDescription = item.product.name,
                    modifier = Modifier.size(72.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.product.name,
                        style = BodySmall,
                        color = GSVCTextPrimary,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.cart_sku_format, item.product.sku),
                        style = LabelSubtitle,
                        color = GSVCTextDisable,
                    )
                }
            }
        }

        // Right: selection checkbox
        Box(
            modifier = Modifier
                .clickable(onClick = onToggleSelection)
                .padding(4.dp),
        ) {
            CheckboxIcon(isChecked = item.isSelected)
        }
    }
}

@Composable
private fun ProductItemBottomSection(
    item: CartItem,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Trash icon
        Box(
            modifier = Modifier.border(
                width = 1.dp,
                color = GSVCBorderDivider,
                shape = GSVCSmall)
        ) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = Orange,
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Quantity dropdown box
        QuantityBox(
            quantity = item.quantity,
            onQuantityChange = onQuantityChange,
        )

        Spacer(modifier = Modifier.weight(1f))

        // Price area
        Column(horizontalAlignment = Alignment.End) {
            // Struck-through original price
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = stringResource(
                        R.string.cart_total_format,
                        item.product.originalPrice * item.quantity
                    ),
                    style = BodySmall.copy(textDecoration = TextDecoration.LineThrough),
                    color = GSVCTextDisable,
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Final (financed) price
                Text(
                    text = stringResource(
                        R.string.cart_total_format,
                        item.product.totalPrice * item.quantity
                    ),
                    style = BodyBold,
                    color = GSVCTextPrimary,
                )
            }

            // Loan detail row — below the price row
            LoanDetailText(
                amountPerWeek = item.product.amountPerWeek,
                loanWeeks = item.product.loanWeeks,
            )

        }
    }
}
