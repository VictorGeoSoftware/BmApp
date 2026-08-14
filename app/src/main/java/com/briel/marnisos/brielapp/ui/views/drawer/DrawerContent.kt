package com.briel.marnisos.brielapp.ui.views.drawer

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.R
import com.briel.marnisos.brielapp.domain.models.FeeFirstStage
import com.briel.marnisos.brielapp.ui.components.InfoIcon
import com.briel.marnisos.brielapp.ui.components.LockIcon
import com.briel.marnisos.brielapp.ui.navigation.BmAppRoute
import com.briel.marnisos.brielapp.ui.navigation.canReach
import com.briel.marnisos.brielapp.ui.theme.BrielAppTheme
import com.briel.marnisos.brielapp.ui.theme.extendedColors

private val LockedNoteBackground = Color(0xFFFEE2E2)
private val LockedNoteContent = Color(0xFF991B1B)

/**
 * A drawer row. [isGated] is false for actions that are always offered because they
 * reset the flow instead of navigating deeper into it.
 */
private data class MenuEntry(
    val route: BmAppRoute,
    @StringRes val labelRes: Int,
    val isGated: Boolean = true,
) {
    fun isEnabled(stage: FeeFirstStage): Boolean = !isGated || stage.canReach(route)
}

/**
 * Drawer menu. Destinations blocked by the fee-first gate stay visible but disabled
 * with a padlock, so the broker can see what is coming instead of items vanishing.
 */
@Composable
internal fun DrawerContent(
    selectedRoute: BmAppRoute,
    stage: FeeFirstStage,
    onDestinationSelected: (BmAppRoute) -> Unit,
    onLogoutClicked: () -> Unit,
    versionLabel: String,
) {
    val menuEntries = listOf(
        MenuEntry(BmAppRoute.CurrentConditions, R.string.drawer_current_conditions),
        MenuEntry(BmAppRoute.Proposals, R.string.drawer_proposals),
        MenuEntry(BmAppRoute.Configuration, R.string.drawer_configuration),
        // Always available: it restarts the flow (R6) rather than navigating into it.
        MenuEntry(BmAppRoute.FetchConsumption, R.string.drawer_new_study, isGated = false),
    )
    val hasLockedEntry = menuEntries.any { entry -> !entry.isEnabled(stage) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .padding(horizontal = 12.dp, vertical = 24.dp),
    ) {
        Text(
            text = stringResource(R.string.drawer_menu_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )

        menuEntries.forEach { entry ->
            val route = entry.route
            val isEnabled = entry.isEnabled(stage)

            NavigationDrawerItem(
                label = {
                    Text(
                        text = stringResource(entry.labelRes),
                        color = if (isEnabled) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                },
                badge = {
                    if (!isEnabled) {
                        Icon(
                            imageVector = LockIcon,
                            contentDescription = stringResource(
                                R.string.drawer_locked_content_description,
                            ),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                },
                selected = selectedRoute == route,
                onClick = { if (isEnabled) onDestinationSelected(route) },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = extendedColors.sectionHighlight,
                ),
            )
        }

        if (hasLockedEntry) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LockedNoteBackground)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = InfoIcon,
                    contentDescription = null,
                    tint = LockedNoteContent,
                    modifier = Modifier.size(15.dp),
                )
                Text(
                    text = stringResource(
                        if (stage == FeeFirstStage.CONSUMPTION_REQUIRED) {
                            R.string.drawer_locked_hint_consumption
                        } else {
                            R.string.drawer_locked_hint
                        },
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = LockedNoteContent,
                )
            }
        }

        NavigationDrawerItem(
            label = { Text(stringResource(R.string.drawer_logout)) },
            selected = false,
            onClick = onLogoutClicked,
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = versionLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Preview(name = "Drawer locked", showBackground = true)
@Composable
private fun DrawerContentLockedPreview() {
    BrielAppTheme(darkTheme = false) {
        DrawerContent(
            selectedRoute = BmAppRoute.CurrentConditions,
            stage = FeeFirstStage.CURRENT_CONDITIONS_REQUIRED,
            onDestinationSelected = {},
            onLogoutClicked = {},
            versionLabel = "v1408_0930",
        )
    }
}

@Preview(name = "Drawer unlocked", showBackground = true)
@Composable
private fun DrawerContentUnlockedPreview() {
    BrielAppTheme(darkTheme = false) {
        DrawerContent(
            selectedRoute = BmAppRoute.Proposals,
            stage = FeeFirstStage.UNLOCKED,
            onDestinationSelected = {},
            onLogoutClicked = {},
            versionLabel = "v1408_0930",
        )
    }
}
