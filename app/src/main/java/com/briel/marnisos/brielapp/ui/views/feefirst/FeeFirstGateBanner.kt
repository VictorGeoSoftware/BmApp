package com.briel.marnisos.brielapp.ui.views.feefirst

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.briel.marnisos.brielapp.domain.models.FeeFirstGateModel
import com.briel.marnisos.brielapp.ui.components.CheckCircleIcon
import com.briel.marnisos.brielapp.ui.components.LockIcon
import com.briel.marnisos.brielapp.ui.theme.BrielAppTheme

private val LockedBackground = Color(0xFFFEE2E2)
private val LockedContent = Color(0xFF991B1B)
private val UnlockedBackground = Color(0xFFDCFCE7)
private val UnlockedContent = Color(0xFF166534)

/**
 * Explains whether the fee-first gate is currently blocking navigation, and how far
 * the broker is from clearing it.
 */
@Composable
internal fun FeeFirstGateBanner(
    gate: FeeFirstGateModel,
    modifier: Modifier = Modifier,
) {
    val isUnlocked = gate.isUnlocked
    val background = if (isUnlocked) UnlockedBackground else LockedBackground
    val content = if (isUnlocked) UnlockedContent else LockedContent

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = if (isUnlocked) CheckCircleIcon else LockIcon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(18.dp),
            )

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(
                        if (isUnlocked) {
                            R.string.fee_first_unlocked_banner_title
                        } else {
                            R.string.fee_first_locked_banner_title
                        },
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = content,
                )

                Text(
                    text = stringResource(
                        if (isUnlocked) {
                            R.string.fee_first_unlocked_banner_message
                        } else {
                            R.string.fee_first_locked_banner_message
                        },
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = content,
                )
            }
        }

        if (gate.requiredFieldCount > 0) {
            FeeFirstProgress(gate = gate, contentColor = content)
        }
    }
}

@Composable
private fun FeeFirstProgress(
    gate: FeeFirstGateModel,
    contentColor: Color,
) {
    Text(
        text = stringResource(
            R.string.fee_first_progress,
            gate.completedRequiredFieldCount,
            gate.requiredFieldCount,
        ),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = contentColor,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(contentColor.copy(alpha = 0.18f)),
    ) {
        val completionRatio = gate.completedRequiredFieldCount.toFloat() /
            gate.requiredFieldCount.toFloat()

        if (completionRatio > 0f) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(fraction = completionRatio.coerceIn(0f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(contentColor),
            ) {}
        }
    }
}

@Preview(name = "Locked", showBackground = true)
@Composable
private fun FeeFirstGateBannerLockedPreview() {
    BrielAppTheme(darkTheme = false) {
        FeeFirstGateBanner(
            gate = FeeFirstGateModel(
                hasFetchedConsumption = true,
                requiredFieldCount = 5,
                completedRequiredFieldCount = 2,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "Unlocked", showBackground = true)
@Composable
private fun FeeFirstGateBannerUnlockedPreview() {
    BrielAppTheme(darkTheme = false) {
        FeeFirstGateBanner(
            gate = FeeFirstGateModel(
                hasFetchedConsumption = true,
                requiredFieldCount = 5,
                completedRequiredFieldCount = 5,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
