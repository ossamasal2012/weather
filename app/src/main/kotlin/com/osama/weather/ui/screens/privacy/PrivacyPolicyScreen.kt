package com.osama.weather.ui.screens.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.osama.weather.R
import com.osama.weather.data.local.PrivacyPolicyLanguage
import com.osama.weather.ui.components.GlassCard
import com.osama.weather.ui.screens.home.HomeViewModel
import com.osama.weather.ui.theme.Radius
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors

/**
 * Settings → Privacy Policy. Opens in Arabic for every person's first visit;
 * switching to English is remembered from then on (persisted through
 * [HomeViewModel.setPrivacyPolicyLanguage]/[com.osama.weather.data.local.PreferencesManager.privacyPolicyLanguage]),
 * however long it's been since the app was last open.
 */
@Composable
fun PrivacyPolicyScreen(homeViewModel: HomeViewModel, onBack: () -> Unit) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val language = uiState.privacyPolicyLanguage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WeatherColors.BrandDeepBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.md)
                .padding(top = Spacing.sm, bottom = Spacing.xxl)
        ) {
            // Fixed Arabic app chrome regardless of which policy language is
            // showing below — only the document body itself switches.
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = WeatherColors.OnBgPrimary
                    )
                }
                Text(
                    text = stringResource(R.string.settings_privacy_policy),
                    style = MaterialTheme.typography.titleLarge,
                    color = WeatherColors.OnBgPrimary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            LanguageToggle(
                selected = language,
                onSelect = { homeViewModel.setPrivacyPolicyLanguage(it) }
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            val rawText = if (language == PrivacyPolicyLanguage.ENGLISH) {
                PrivacyPolicyContent.ENGLISH
            } else {
                PrivacyPolicyContent.ARABIC
            }
            val blocks = remember(rawText) { parsePolicyMarkdown(rawText) }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                // The document itself reads naturally in whichever direction
                // its own language uses — English left-to-right even though
                // the rest of this screen (and the app) stays RTL.
                CompositionLocalProvider(
                    LocalLayoutDirection provides
                        (if (language == PrivacyPolicyLanguage.ENGLISH) LayoutDirection.Ltr else LayoutDirection.Rtl)
                ) {
                    Column {
                        blocks.forEach { block -> PolicyBlockView(block) }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageToggle(
    selected: PrivacyPolicyLanguage,
    onSelect: (PrivacyPolicyLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(Radius.pill))
            .background(WeatherColors.GlassSurface)
            .padding(4.dp)
    ) {
        LanguageToggleOption(
            label = stringResource(R.string.lang_toggle_arabic),
            selected = selected == PrivacyPolicyLanguage.ARABIC,
            onClick = { onSelect(PrivacyPolicyLanguage.ARABIC) },
            modifier = Modifier.weight(1f)
        )
        LanguageToggleOption(
            label = stringResource(R.string.lang_toggle_english),
            selected = selected == PrivacyPolicyLanguage.ENGLISH,
            onClick = { onSelect(PrivacyPolicyLanguage.ENGLISH) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LanguageToggleOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(Radius.pill))
            .background(if (selected) WeatherColors.Accent else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = if (selected) WeatherColors.OnAccent else WeatherColors.OnBgSecondary
        )
    }
}

// ------------------------------------------------------------------ parsing

/**
 * The tiny subset of markdown the policy text actually uses: `##`/`#` and
 * `###` headers, `* ` bullets, `---` dividers and inline `**bold**` spans.
 * Rendered as real Compose styling instead of showing those characters
 * literally.
 */
private sealed interface PolicyBlock {
    data class MajorHeader(val text: String) : PolicyBlock
    data class MinorHeader(val text: String) : PolicyBlock
    data class Paragraph(val text: String) : PolicyBlock
    data class BulletList(val items: List<String>) : PolicyBlock
    data object SectionDivider : PolicyBlock
}

private fun parsePolicyMarkdown(raw: String): List<PolicyBlock> {
    // Blank lines only ever separate elements in this document (including
    // between consecutive bullets) — dropping them up front means adjacent
    // bullet lines merge into one tidy list below instead of scattering.
    val lines = raw.lines().map { it.trim() }.filter { it.isNotEmpty() }
    val blocks = mutableListOf<PolicyBlock>()
    var i = 0
    while (i < lines.size) {
        val line = lines[i]
        when {
            line == "---" -> {
                blocks += PolicyBlock.SectionDivider
                i++
            }
            line.startsWith("### ") -> {
                blocks += PolicyBlock.MinorHeader(line.removePrefix("### "))
                i++
            }
            line.startsWith("## ") -> {
                blocks += PolicyBlock.MajorHeader(line.removePrefix("## "))
                i++
            }
            line.startsWith("# ") -> {
                blocks += PolicyBlock.MajorHeader(line.removePrefix("# "))
                i++
            }
            line.startsWith("* ") -> {
                val items = mutableListOf<String>()
                while (i < lines.size && lines[i].startsWith("* ")) {
                    items += lines[i].removePrefix("* ")
                    i++
                }
                blocks += PolicyBlock.BulletList(items)
            }
            else -> {
                blocks += PolicyBlock.Paragraph(line)
                i++
            }
        }
    }
    return blocks
}

/** Turns `**bold**` spans into real bold styling; everything else renders as-is. */
private fun inlineFormatted(text: String): AnnotatedString = buildAnnotatedString {
    text.split("**").forEachIndexed { index, segment ->
        if (index % 2 == 1) {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = WeatherColors.OnBgPrimary)) { append(segment) }
        } else {
            append(segment)
        }
    }
}

@Composable
private fun PolicyBlockView(block: PolicyBlock) {
    when (block) {
        is PolicyBlock.MajorHeader -> {
            Text(
                text = inlineFormatted(block.text),
                style = MaterialTheme.typography.titleMedium,
                color = WeatherColors.Accent,
                modifier = Modifier.padding(top = Spacing.sm, bottom = Spacing.xxs)
            )
        }
        is PolicyBlock.MinorHeader -> {
            Text(
                text = inlineFormatted(block.text),
                style = MaterialTheme.typography.titleSmall,
                color = WeatherColors.OnBgPrimary,
                modifier = Modifier.padding(top = Spacing.xs, bottom = Spacing.xxs)
            )
        }
        is PolicyBlock.Paragraph -> {
            Text(
                text = inlineFormatted(block.text),
                style = MaterialTheme.typography.bodyMedium,
                color = WeatherColors.OnBgSecondary,
                modifier = Modifier.padding(bottom = Spacing.xs)
            )
        }
        is PolicyBlock.BulletList -> {
            Column(
                modifier = Modifier.padding(bottom = Spacing.xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
            ) {
                block.items.forEach { item ->
                    Row {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WeatherColors.Accent,
                            modifier = Modifier.width(20.dp)
                        )
                        Text(
                            text = inlineFormatted(item),
                            style = MaterialTheme.typography.bodyMedium,
                            color = WeatherColors.OnBgSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
        PolicyBlock.SectionDivider -> {
            HorizontalDivider(
                color = WeatherColors.GlassBorder,
                modifier = Modifier.padding(vertical = Spacing.sm)
            )
        }
    }
}
