package com.osama.weather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.osama.weather.R
import com.osama.weather.ui.theme.WeatherColors

/** The two round glass icon buttons anchored in the home screen's top bar. */
@Composable
fun SearchIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Filled.Search,
        contentDescription = stringResource(R.string.cd_search),
        tint = WeatherColors.OnBgPrimary,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(WeatherColors.GlassSurface)
            .clickable(onClick = onClick)
            .padding(9.dp)
    )
}

@Composable
fun MyLocationIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Filled.MyLocation,
        contentDescription = stringResource(R.string.cd_my_location),
        tint = WeatherColors.OnBgPrimary,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(WeatherColors.GlassSurface)
            .clickable(onClick = onClick)
            .padding(9.dp)
    )
}
