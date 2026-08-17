package com.briel.marnisos.brielapp.ui.views.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.R
import com.briel.marnisos.brielapp.ui.components.MenuIcon
import com.briel.marnisos.brielapp.ui.theme.BrielAppTheme

/**
 * Top bar of the authenticated shell.
 *
 * Only the report action lives here, and only on the proposals screen. The scan
 * shortcut was removed: the fetch-consumption screen already offers scanning as an
 * explicit choice, so the duplicate in the top bar was noise (supersedes rule R7).
 * Going back to edit the customer's current conditions is the drawer's job.
 */
@Composable
internal fun TopActionBar(
    isGeneratingPdf: Boolean,
    showPrintButton: Boolean,
    onGeneratePdfClick: () -> Unit,
    onOpenDrawer: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onOpenDrawer) {
            Icon(
                imageVector = MenuIcon,
                contentDescription = stringResource(
                    R.string.top_bar_open_drawer_content_description,
                ),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (showPrintButton) {
                PrintButton(
                    isGeneratingPdf = isGeneratingPdf,
                    onGeneratePdfClick = onGeneratePdfClick,
                )
            }
        }
    }
}

@Preview(name = "Top bar - proposals", showBackground = true)
@Composable
private fun TopActionBarProposalsPreview() {
    BrielAppTheme(darkTheme = false) {
        TopActionBar(
            isGeneratingPdf = false,
            showPrintButton = true,
            onGeneratePdfClick = {},
            onOpenDrawer = {},
        )
    }
}

@Preview(name = "Top bar - generating", showBackground = true)
@Composable
private fun TopActionBarGeneratingPreview() {
    BrielAppTheme(darkTheme = false) {
        TopActionBar(
            isGeneratingPdf = true,
            showPrintButton = true,
            onGeneratePdfClick = {},
            onOpenDrawer = {},
        )
    }
}

@Preview(name = "Top bar - other screens", showBackground = true)
@Composable
private fun TopActionBarPlainPreview() {
    BrielAppTheme(darkTheme = false) {
        TopActionBar(
            isGeneratingPdf = false,
            showPrintButton = false,
            onGeneratePdfClick = {},
            onOpenDrawer = {},
        )
    }
}
