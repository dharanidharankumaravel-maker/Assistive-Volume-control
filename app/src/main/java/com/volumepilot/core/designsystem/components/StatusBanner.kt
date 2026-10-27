package com.volumepilot.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.volumepilot.core.designsystem.LocalSpacing

enum class BannerType {
    INFO,
    WARNING,
    SUCCESS
}

@Composable
fun StatusBanner(
    message: String,
    modifier: Modifier = Modifier,
    type: BannerType = BannerType.INFO,
    icon: ImageVector? = null
) {
    val spacing = LocalSpacing.current

    val containerColor = when (type) {
        BannerType.INFO -> MaterialTheme.colorScheme.primaryContainer
        BannerType.WARNING -> MaterialTheme.colorScheme.errorContainer
        BannerType.SUCCESS -> MaterialTheme.colorScheme.secondaryContainer
    }

    val contentColor = when (type) {
        BannerType.INFO -> MaterialTheme.colorScheme.onPrimaryContainer
        BannerType.WARNING -> MaterialTheme.colorScheme.onErrorContainer
        BannerType.SUCCESS -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Icon(
                imageVector = icon ?: when (type) {
                    BannerType.WARNING -> Icons.Default.Warning
                    else -> Icons.Default.Info
                },
                contentDescription = null,
                tint = contentColor
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
