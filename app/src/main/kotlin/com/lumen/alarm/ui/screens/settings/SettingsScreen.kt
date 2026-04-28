package com.lumen.alarm.ui.screens.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lumen.alarm.domain.model.AccentColor
import com.lumen.alarm.domain.model.ThemeMode
import com.lumen.alarm.ui.components.*
import com.lumen.alarm.ui.theme.*

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenPremium: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val prefs by viewModel.prefs.collectAsStateWithLifecycle()
    val colors = MaterialTheme.lumen

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgApp)
            .verticalScroll(rememberScrollState())
    ) {
        LumenTopBar(title = "Settings", onBack = onBack)

        // Premium banner
        GlowCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            glowColor = GoldAccent,
            onClick = onOpenPremium,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WorkspacePremium, null, tint = GoldAccent, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Lumen Premium",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "Unlock Smart Wake, challenges, goals & more",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.ink2,
                    )
                }
                Icon(Icons.Default.ChevronRight, null, tint = GoldAccent, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(Modifier.height(8.dp))
        SectionLabel("Defaults", modifier = Modifier.padding(horizontal = 22.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(ShapeLarge)
                .background(colors.bgCard),
        ) {
            SettingsRow(
                label = "Default snooze",
                value = "${prefs.defaultSnoozeMinutes} min · ${prefs.defaultSnoozeMaxCount}× limit",
                leadingIcon = Icons.Default.Snooze,
                leadingIconColor = GoldAccent,
                onClick = { /* open snooze sheet */ },
            )
            HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))
            SettingsRow(
                label = "Default tone",
                value = prefs.defaultSoundName,
                leadingIcon = Icons.Default.MusicNote,
                leadingIconColor = LilacAccent,
                onClick = { /* open sound picker */ },
            )
            HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))
            SettingsRow(
                label = "Time format",
                value = if (prefs.is24HourFormat) "24-hour" else "12-hour",
                leadingIcon = Icons.Default.Schedule,
                leadingIconColor = IndigoAccent,
                trailing = {
                    LumenToggle(
                        checked = prefs.is24HourFormat,
                        onCheckedChange = { viewModel.set24HourFormat(it) },
                    )
                },
            )
            HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))
            SettingsRow(
                label = "First day of week",
                value = if (prefs.firstDayOfWeek == 1) "Monday" else "Sunday",
                leadingIcon = Icons.Default.CalendarToday,
                leadingIconColor = AuroraAccent,
                onClick = { /* toggle */ },
            )
        }

        Spacer(Modifier.height(20.dp))
        SectionLabel("Appearance", modifier = Modifier.padding(horizontal = 22.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(ShapeLarge)
                .background(colors.bgCard),
        ) {
            // Theme mode
            SettingsRow(
                label = "Theme",
                value = prefs.themeMode.name.lowercase().replaceFirstChar { it.uppercase() },
                leadingIcon = Icons.Default.DarkMode,
                leadingIconColor = LilacAccent,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ThemeMode.entries.forEach { mode ->
                        val selected = prefs.themeMode == mode
                        Box(
                            modifier = Modifier
                                .clip(ShapePill)
                                .background(if (selected) IndigoAccent else colors.bgHover)
                                .clickable { viewModel.setThemeMode(mode) }
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                        ) {
                            Text(
                                mode.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodySmall,
                                color = if (selected) Color.White else colors.ink2,
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))
            // Accent color
            SettingsRow(
                label = "Accent color",
                leadingIcon = Icons.Default.Palette,
                leadingIconColor = IndigoAccent,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val accentColors = mapOf(
                        AccentColor.INDIGO to IndigoAccent,
                        AccentColor.AURORA to AuroraAccent,
                        AccentColor.ROSE to RoseAccent,
                        AccentColor.GOLD to GoldAccent,
                        AccentColor.LILAC to LilacAccent,
                    )
                    accentColors.forEach { (accentEnum, color) ->
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (prefs.accentColor == accentEnum)
                                        Modifier.border(2.dp, Color.White, CircleShape)
                                    else Modifier
                                )
                                .clickable { viewModel.setAccentColor(accentEnum) },
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        SectionLabel("Data", modifier = Modifier.padding(horizontal = 22.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(ShapeLarge)
                .background(colors.bgCard),
        ) {
            SettingsRow(
                label = "Backup & sync",
                value = if (prefs.backupEnabled) "Google Drive · synced" else "Off",
                leadingIcon = Icons.Default.Backup,
                leadingIconColor = AuroraAccent,
                trailing = {
                    LumenToggle(
                        checked = prefs.backupEnabled,
                        onCheckedChange = { viewModel.setBackup(it) },
                    )
                },
            )
            HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))
            SettingsRow(
                label = "Export alarm history",
                leadingIcon = Icons.Default.Download,
                leadingIconColor = IndigoAccent,
                onClick = { /* export */ },
            )
        }

        Spacer(Modifier.height(20.dp))
        SectionLabel("About", modifier = Modifier.padding(horizontal = 22.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(ShapeLarge)
                .background(colors.bgCard),
        ) {
            SettingsRow(
                label = "Help & feedback",
                leadingIcon = Icons.Default.HelpOutline,
                leadingIconColor = IndigoAccent,
                onClick = { /* open feedback */ },
            )
            HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))
            SettingsRow(
                label = "Version",
                value = "1.0.0",
                leadingIcon = Icons.Default.Info,
                leadingIconColor = colors.ink2,
            )
        }

        Spacer(Modifier.height(80.dp))
    }
}

