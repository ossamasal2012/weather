package com.osama.weather.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.osama.weather.R
import com.osama.weather.domain.model.GeoLocation
import com.osama.weather.ui.components.GlassCard
import com.osama.weather.ui.theme.Radius
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onLocationSelected: (GeoLocation) -> Unit,
    onUseCurrentLocation: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WeatherColors.BrandDeepBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = Spacing.md)
                .padding(top = Spacing.sm)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = WeatherColors.OnBgPrimary
                    )
                }
                Text(
                    text = stringResource(R.string.search_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = WeatherColors.OnBgPrimary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = { Text(stringResource(R.string.search_hint), color = WeatherColors.OnBgTertiary) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = WeatherColors.OnBgSecondary) },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = WeatherColors.OnBgSecondary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(Radius.md),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = WeatherColors.OnBgPrimary,
                    unfocusedTextColor = WeatherColors.OnBgPrimary,
                    focusedBorderColor = WeatherColors.Accent,
                    unfocusedBorderColor = WeatherColors.GlassBorderStrong,
                    focusedContainerColor = WeatherColors.GlassSurface,
                    unfocusedContainerColor = WeatherColors.GlassSurface,
                    cursorColor = WeatherColors.Accent
                )
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                item {
                    CurrentLocationRow(onClick = onUseCurrentLocation)
                    Spacer(modifier = Modifier.height(Spacing.sm))
                }

                when {
                    uiState.isSearching -> item { LoadingRow() }

                    uiState.query.length >= 2 && uiState.hasSearched && uiState.results.isEmpty() -> item {
                        Text(
                            text = stringResource(R.string.search_no_results, uiState.query),
                            color = WeatherColors.OnBgSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(Spacing.md)
                        )
                    }

                    uiState.results.isNotEmpty() -> {
                        items(uiState.results) { result ->
                            LocationRow(location = result, onClick = { onLocationSelected(result) })
                        }
                    }

                    uiState.recentSearches.isNotEmpty() -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.recent_searches),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = WeatherColors.OnBgTertiary,
                                    modifier = Modifier.padding(start = Spacing.xs)
                                )
                                Text(
                                    text = stringResource(R.string.clear_recent),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = WeatherColors.Accent,
                                    modifier = Modifier
                                        .clickable { viewModel.clearRecentSearches() }
                                        .padding(Spacing.xs)
                                )
                            }
                        }
                        items(uiState.recentSearches) { result ->
                            LocationRow(location = result, onClick = { onLocationSelected(result) }, icon = Icons.Filled.History)
                        }
                    }

                    else -> item {
                        Text(
                            text = stringResource(R.string.search_empty_state),
                            color = WeatherColors.OnBgTertiary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(Spacing.md)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentLocationRow(onClick: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.MyLocation, contentDescription = null, tint = WeatherColors.Accent)
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = stringResource(R.string.use_my_location),
                style = MaterialTheme.typography.titleMedium,
                color = WeatherColors.OnBgPrimary
            )
        }
    }
}

@Composable
private fun LocationRow(
    location: GeoLocation,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Filled.Place
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.sm))
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.sm, horizontal = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = WeatherColors.OnBgTertiary)
        Spacer(modifier = Modifier.width(Spacing.sm))
        Column {
            Text(
                text = location.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = WeatherColors.OnBgPrimary
            )
            Text(
                text = location.displayName,
                style = MaterialTheme.typography.bodySmall,
                color = WeatherColors.OnBgTertiary
            )
        }
    }
}

@Composable
private fun LoadingRow() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(Spacing.md),
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = WeatherColors.Accent, modifier = Modifier.height(28.dp).width(28.dp))
    }
}
