package com.osama.weather.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.osama.weather.R
import com.osama.weather.ui.theme.Radius
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors

sealed interface UpdateDialogState {
    data class Available(val versionName: String, val forceUpdate: Boolean) : UpdateDialogState
    data object RequestingInstallPermission : UpdateDialogState
    data class Downloading(val percent: Int) : UpdateDialogState
    data object Installing : UpdateDialogState
    data object Failed : UpdateDialogState
}

/**
 * The mandatory update dialog described in the brief: cannot be dismissed
 * (no scrim-click-to-close, no back-press escape) while an update is
 * outstanding, shows the real live download percentage once "تحديث الآن" is
 * tapped, and hands off to "جارٍ فتح شاشة التثبيت…" the moment the download
 * finishes.
 */
@Composable
fun UpdateDialog(
    state: UpdateDialogState,
    onUpdateNowClick: () -> Unit,
    onOpenInstallSettings: () -> Unit,
    onRetryClick: () -> Unit
) {
    Dialog(
        onDismissRequest = { /* intentionally not dismissible */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Radius.lg))
                .background(Color(0xFF0E1A3A))
                .background(WeatherColors.GlassSurfaceStrong)
                .padding(Spacing.xl),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.update_available_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = WeatherColors.OnBgPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(Spacing.sm))

                val message = when (state) {
                    is UpdateDialogState.Available -> stringResource(R.string.update_available_message, state.versionName)
                    UpdateDialogState.RequestingInstallPermission -> stringResource(R.string.update_install_permission_message)
                    is UpdateDialogState.Downloading -> stringResource(R.string.update_downloading_percent, state.percent)
                    UpdateDialogState.Installing -> stringResource(R.string.update_installing)
                    UpdateDialogState.Failed -> stringResource(R.string.update_download_failed)
                }
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WeatherColors.OnBgSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(Spacing.lg))

                when (state) {
                    is UpdateDialogState.Downloading -> {
                        DownloadProgressBar(percent = state.percent)
                        Spacer(Modifier.height(Spacing.sm))
                        Text(
                            text = "${state.percent}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = WeatherColors.Accent
                        )
                    }
                    UpdateDialogState.Installing -> {
                        CircularProgressIndicator(color = WeatherColors.Accent)
                    }
                    is UpdateDialogState.Available -> {
                        UpdateActionButton(stringResource(R.string.update_now), onUpdateNowClick)
                    }
                    UpdateDialogState.RequestingInstallPermission -> {
                        UpdateActionButton(stringResource(R.string.update_open_settings), onOpenInstallSettings)
                    }
                    UpdateDialogState.Failed -> {
                        UpdateActionButton(stringResource(R.string.update_retry), onRetryClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateActionButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = WeatherColors.Accent, contentColor = WeatherColors.OnAccent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun DownloadProgressBar(percent: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = (percent / 100f).coerceIn(0f, 1f),
        animationSpec = tween(220, easing = LinearEasing),
        label = "downloadProgress"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(50))
            .background(WeatherColors.GlassSurface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = animatedProgress.coerceAtLeast(0.03f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(WeatherColors.Accent)
        )
    }
}
