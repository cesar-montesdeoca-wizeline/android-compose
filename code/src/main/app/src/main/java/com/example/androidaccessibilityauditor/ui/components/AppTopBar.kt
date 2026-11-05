package com.example.androidaccessibilityauditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidaccessibilityauditor.R
import com.example.androidaccessibilityauditor.ui.theme.GSVCHeadSecondary
import com.example.androidaccessibilityauditor.ui.theme.GSVCTextPrimary
import com.example.androidaccessibilityauditor.ui.theme.H4
import com.example.androidaccessibilityauditor.ui.theme.Orange

/**
 * Shared top app bar with back navigation.
 *
 * Uses the GSVC design tokens:
 *  - Background: [GSVCHeadSecondary] (white).
 *  - Back icon tint: [Orange].
 *  - Title typography: [H4] (Regular 400, 16sp), colour [GSVCTextPrimary].
 *
 * Mirrors the visual structure of the Cart toolbar without depending on it.
 * Callers supply the title string so the component stays feature-agnostic.
 *
 * @param title           Text displayed as the toolbar title.
 * @param onNavigateBack  Invoked when the back icon is tapped.
 * @param modifier        Optional [Modifier] for the outer [Row].
 */
@Composable
fun AppTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
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
            text = title,
            style = H4,
            color = GSVCTextPrimary,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppTopBarPreview() {
    AppTopBar(
        title = "Realizar compra",
        onNavigateBack = {},
    )
}
