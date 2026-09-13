package com.osama.weather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.osama.weather.R
import com.osama.weather.domain.model.UsAqiCategory
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors

@Composable
fun AirQualitySummaryCard(
    usAqi: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val category = usAqi?.let { UsAqiCategory.fromValue(it) }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.air_quality),
                    style = MaterialTheme.typography.titleMedium,
                    color = WeatherColors.OnBgSecondary
                )
                Spacer(Modifier.width(Spacing.xxs))
                if (usAqi != null && category != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(category.color)
                        )
                        Spacer(Modifier.width(Spacing.xs))
                        Text(
                            text = "$usAqi · ${stringResource(category.labelRes)}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = WeatherColors.OnBgPrimary
                        )
                    }
                } else {
                    Text(
                        text = "—",
                        style = MaterialTheme.typography.headlineSmall,
                        color = WeatherColors.OnBgPrimary
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = WeatherColors.OnBgTertiary
            )
        }
    }
}
