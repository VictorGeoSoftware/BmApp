package com.briel.marnisos.brielapp.ui.views.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.briel.marnisos.brielapp.ui.models.ComparatorDestination

@Composable
internal fun DrawerContent(
    selectedDestination: ComparatorDestination,
    onDestinationSelected: (ComparatorDestination) -> Unit,
    showCurrentUserConditionsOption: Boolean,
    versionLabel: String,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .padding(horizontal = 12.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Menú",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        NavigationDrawerItem(
            label = { Text("Propuestas") },
            selected = selectedDestination == ComparatorDestination.PROPOSALS,
            onClick = { onDestinationSelected(ComparatorDestination.PROPOSALS) }
        )

        NavigationDrawerItem(
            label = { Text("Configuración") },
            selected = selectedDestination == ComparatorDestination.CONFIGURATION,
            onClick = { onDestinationSelected(ComparatorDestination.CONFIGURATION) }
        )

        if (showCurrentUserConditionsOption) {
            NavigationDrawerItem(
                label = { Text("Condiciones actuales") },
                selected = selectedDestination == ComparatorDestination.CURRENT_USER_CONDITIONS,
                onClick = { onDestinationSelected(ComparatorDestination.CURRENT_USER_CONDITIONS) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = versionLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
