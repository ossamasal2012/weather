package com.osama.weather.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.osama.weather.ui.theme.Radius
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors

/**
 * The one card style used everywhere in the app: a soft translucent surface
 * with a thin light border, letting the animated sky show through instead of
 * sitting on top of it like an opaque sheet. Two strengths only — [strong]
 * for content that should feel slightly more prominent (the hero card) — to
 * keep the whole app visually disciplined rather than a patchwork of ad-hoc
 * card styles.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = Radius.lg,
    strong: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(Spacing.md),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Column(
        modifier = modifier
            .clip(shape)
            .background(if (strong) WeatherColors.GlassSurfaceStrong else WeatherColors.GlassSurface)
            .border(
                BorderStroke(1.dp, if (strong) WeatherColors.GlassBorderStrong else WeatherColors.GlassBorder),
                shape
            )
            .padding(contentPadding),
        content = content
    )
}
