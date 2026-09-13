package com.osama.weather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors

/** One small metric tile — icon, label, big value, optional secondary line. */
@Composable
fun DetailStatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    secondaryLine: String? = null
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = WeatherColors.OnBgTertiary, modifier = Modifier.height(18.dp))
            Spacer(Modifier.height(Spacing.xxs))
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = WeatherColors.OnBgTertiary)
        }
        Spacer(Modifier.height(Spacing.xs))
        Text(text = value, style = MaterialTheme.typography.headlineSmall, color = WeatherColors.OnBgPrimary)
        if (secondaryLine != null) {
            Text(text = secondaryLine, style = MaterialTheme.typography.bodySmall, color = WeatherColors.OnBgSecondary)
        }
    }
}

/** Responsive 2-column grid of [DetailStatCard]s. */
@Composable
fun StatGrid(modifier: Modifier = Modifier, content: List<@Composable () -> Unit>) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        content.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                rowItems.forEach { item ->
                    Column(modifier = Modifier.weight(1f)) { item() }
                }
                if (rowItems.size == 1) {
                    Column(modifier = Modifier.weight(1f)) {}
                }
            }
        }
    }
}
