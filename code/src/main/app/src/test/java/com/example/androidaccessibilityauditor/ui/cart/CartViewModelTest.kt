package com.example.androidaccessibilityauditor.ui.cart

import com.example.androidaccessibilityauditor.data.model.CartItem
import com.example.androidaccessibilityauditor.data.model.Product
import com.example.androidaccessibilityauditor.data.repository.CartRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [CartViewModel] state transitions and business calculations.
 *
 * Uses an in-memory [CartRepository] stub with the two approved sample products.
 * All tests are deterministic and do not depend on Android framework.
 */
class CartViewModelTest {

    // ── Test data ────────────────────────────────────────────────────────────

    private val productAcqua = Product(
        id = 1,
        name = "Perfume Acqua GIO",
        sku = "1300776735",
        seller = "Vendido por Elektra",
        originalPrice = 899,
        totalPrice = 1000,
        amountPerWeek = 35,
        loanWeeks = 100,
        imageRes = 0, // No R.drawable in unit tests
    )

    private val productLattafa = Product(
        id = 2,
        name = "Perfume unisex Lattafa Kharmrah Qahwa Eau de Parfum",
        sku = "1300776735",
        seller = "Vendido por Elektra",
        originalPrice = 1000,
        totalPrice = 1899,
        amountPerWeek = 35,
        loanWeeks = 100,
        imageRes = 0,
    )

    private val initialItems = listOf(
        CartItem(id = 1, product = productAcqua, quantity = 1, isSelected = true),
        CartItem(id = 2, product = productLattafa, quantity = 1, isSelected = true),
    )

    private lateinit var viewModel: CartViewModel

    private val repository = object : CartRepository {
        override fun getCartItems() = initialItems
    }

    @Before
    fun setUp() {
        viewModel = CartViewModel(repository)
    }

    // ── Phase 3 unit tests ───────────────────────────────────────────────────

    @Test
    fun initialState_bothItemsPresentBothSelectedQuantityOne() {
        val state = viewModel.uiState.value
        assertEquals(2, state.items.size)
        assertTrue(state.items.all { it.isSelected })
        assertTrue(state.items.all { it.quantity == 1 })
        assertTrue(state.isSelectAllChecked)
    }

    @Test
    fun initialCalculations_selectedUnitCount2_total2899_subtotal3189() {
        val state = viewModel.uiState.value
        assertEquals(2, state.selectedUnitCount)
        assertEquals(2899, state.total)      // 1000 + 1899
        assertEquals(3189, state.subtotal)   // 2899 * 1.1 HALF_UP
    }

    /**
     * Critical rounding test: total = 2899 → 10% = 289.9 → HALF_UP = 290 → subtotal = 3189.
     * Integer division (2899 / 10 = 289) would give subtotal = 3188 — one peso less.
     */
    @Test
    fun roundingTest_total2899_subtotalMustBe3189NotIntegerDivision3188() {
        val state = viewModel.uiState.value
        assertEquals(2899, state.total)
        assertEquals(3189, state.subtotal)
        // Explicitly verify the wrong value is NOT produced
        assertFalse("Integer division result 3188 must not be used", state.subtotal == 3188)
    }

    @Test
    fun toggleItemSelection_deselectItem1_selectedUnitCount1_total1899_subtotal2089() {
        viewModel.onAction(CartAction.ToggleItemSelection(itemId = 1))
        val state = viewModel.uiState.value
        assertEquals(1, state.selectedUnitCount)
        assertEquals(1899, state.total)
        // 1899 * 1.1 HALF_UP: 10% of 1899 = 189.9 → rounds to 190 → subtotal = 2089
        assertEquals(2089, state.subtotal)
    }

    @Test
    fun deselectAllItems_selectedUnitCount0_total0_subtotal0() {
        viewModel.onAction(CartAction.ToggleItemSelection(itemId = 1))
        viewModel.onAction(CartAction.ToggleItemSelection(itemId = 2))
        val state = viewModel.uiState.value
        assertEquals(0, state.selectedUnitCount)
        assertEquals(0, state.total)
        assertEquals(0, state.subtotal)
    }

    @Test
    fun selectAll_whenAllSelected_deselectsAll() {
        // All start selected — ToggleSelectAll should deselect all
        viewModel.onAction(CartAction.ToggleSelectAll)
        val state = viewModel.uiState.value
        assertTrue(state.items.none { it.isSelected })
        assertFalse(state.isSelectAllChecked)
        assertEquals(0, state.selectedUnitCount)
    }

    @Test
    fun selectAll_whenNoneSelected_selectsAll() {
        // Deselect all first
        viewModel.onAction(CartAction.ToggleSelectAll) // deselects all
        viewModel.onAction(CartAction.ToggleSelectAll) // re-selects all
        val state = viewModel.uiState.value
        assertTrue(state.items.all { it.isSelected })
        assertTrue(state.isSelectAllChecked)
    }

    @Test
    fun selectAll_whenPartiallySelected_selectsAll() {
        viewModel.onAction(CartAction.ToggleItemSelection(itemId = 1)) // deselect item 1 only
        viewModel.onAction(CartAction.ToggleSelectAll)                 // should select all
        val state = viewModel.uiState.value
        assertTrue(state.items.all { it.isSelected })
        assertTrue(state.isSelectAllChecked)
    }

    @Test
    fun isSelectAllChecked_accuracyAfterMixedSelectionChanges() {
        // Both start selected → isSelectAllChecked = true
        assertTrue(viewModel.uiState.value.isSelectAllChecked)
        // Deselect one → false
        viewModel.onAction(CartAction.ToggleItemSelection(itemId = 1))
        assertFalse(viewModel.uiState.value.isSelectAllChecked)
        // Re-select that one → true
        viewModel.onAction(CartAction.ToggleItemSelection(itemId = 1))
        assertTrue(viewModel.uiState.value.isSelectAllChecked)
    }

    @Test
    fun removeItem1_listHas1Item_totalsRecalculate() {
        viewModel.onAction(CartAction.RemoveItem(itemId = 1))
        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals(2, state.items[0].id)
        assertEquals(1, state.selectedUnitCount)
        assertEquals(1899, state.total)
        assertEquals(2089, state.subtotal)
        assertFalse(state.isEmpty)
    }

    @Test
    fun removeAllItems_isEmptyTrue() {
        viewModel.onAction(CartAction.RemoveItem(itemId = 1))
        viewModel.onAction(CartAction.RemoveItem(itemId = 2))
        val state = viewModel.uiState.value
        assertTrue(state.isEmpty)
        assertEquals(0, state.items.size)
        assertEquals(0, state.total)
        assertEquals(0, state.subtotal)
    }

    @Test
    fun updateQuantity_item1Qty2_total3899_subtotal4289() {
        viewModel.onAction(CartAction.UpdateQuantity(itemId = 1, quantity = 2))
        val state = viewModel.uiState.value
        // total = 1000*2 + 1899*1 = 3899
        assertEquals(3899, state.total)
        // 3899 * 1.1 HALF_UP: 10% = 389.9 → rounds to 390 → subtotal = 4289
        assertEquals(4289, state.subtotal)
        assertEquals(3, state.selectedUnitCount) // qty=2 item1 + qty=1 item2
    }

    @Test
    fun updateQuantity_onUnselectedItem_doesNotAffectTotal() {
        // Deselect item 1, then change its quantity
        viewModel.onAction(CartAction.ToggleItemSelection(itemId = 1))
        val totalBeforeQuantityChange = viewModel.uiState.value.total
        viewModel.onAction(CartAction.UpdateQuantity(itemId = 1, quantity = 5))
        val state = viewModel.uiState.value
        // Only item 2 is selected, so total unchanged
        assertEquals(totalBeforeQuantityChange, state.total)
        assertEquals(1899, state.total)
    }

    @Test
    fun storePickupToggle_changesOnlyIsStorePickupOn_totalsUnchanged() {
        val stateBefore = viewModel.uiState.value
        viewModel.onAction(CartAction.ToggleStorePickup)
        val stateAfter = viewModel.uiState.value
        assertTrue(stateAfter.isStorePickupOn)
        assertEquals(stateBefore.total, stateAfter.total)
        assertEquals(stateBefore.subtotal, stateAfter.subtotal)
        assertEquals(stateBefore.selectedUnitCount, stateAfter.selectedUnitCount)
        // Toggle back
        viewModel.onAction(CartAction.ToggleStorePickup)
        assertFalse(viewModel.uiState.value.isStorePickupOn)
    }
}
