package com.example.androidaccessibilityauditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidaccessibilityauditor.ui.theme.GSVCSmall
import com.example.androidaccessibilityauditor.ui.theme.GSVCTextInvert
import com.example.androidaccessibilityauditor.ui.theme.Interactions
import com.example.androidaccessibilityauditor.ui.theme.Orange

/**
 * Shared primary action button.
 *
 * Uses the GSVC design tokens:
 *  - Background: [Orange] when enabled, [Orange] at 40% alpha when disabled.
 *  - Text colour: [GSVCTextInvert] (white).
 *  - Typography: [Interactions] (SemiBold 600, 16sp).
 *  - Shape: [GSVCSmall] (RoundedCornerShape 8.dp).
 *
 * Implemented as a [Box] + [clickable] to match the visual pattern of the
 * existing Cart "Continuar" button exactly, avoiding Material3 Button
 * default padding and elevation overrides.
 *
 * @param text     Label shown inside the button.
 * @param onClick  Action invoked on tap (ignored when [enabled] is false).
 * @param modifier Optional [Modifier] for sizing and placement by the caller.
 * @param enabled  When false the button is visually dimmed and non-interactive.
 */
@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .background(
                color = if (enabled) Orange else Orange.copy(alpha = 0.4f),
                shape = GSVCSmall,
            )
            .clickable(enabled = enabled, onClick = onClick, role = Role.Button)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = Interactions,
            color = GSVCTextInvert,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppPrimaryButtonEnabledPreview() {
    AppPrimaryButton(text = "Continuar", onClick = {})
}

@Preview(showBackground = true)
@Composable
private fun AppPrimaryButtonDisabledPreview() {
    AppPrimaryButton(text = "Continuar", onClick = {}, enabled = false)
}
