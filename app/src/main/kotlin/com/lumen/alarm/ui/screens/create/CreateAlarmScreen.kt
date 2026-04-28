package com.lumen.alarm.ui.screens.create

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lumen.alarm.domain.model.*
import com.lumen.alarm.ui.components.*
import com.lumen.alarm.ui.theme.*
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlarmScreen(
    alarmId: Long?,
    onBack: () -> Unit,
    onPickSound: (Long) -> Unit,
    onPickChallenge: (Long) -> Unit,
    onPickLocation: (Long) -> Unit,
    onPickRoutine: (Long) -> Unit,
    viewModel: CreateAlarmViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onBack()
    }

    val colors = MaterialTheme.lumen

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgApp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {
                    if (uiState.hasChanges) viewModel.requestDiscard() else onBack()
                }) {
                    Icon(Icons.Default.Close, "Cancel", tint = colors.ink1)
                }
                Text(
                    text = if (alarmId != null) "Edit alarm" else "New alarm",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.ink0,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                )
                LumenButton(
                    text = "Save",
                    onClick = { viewModel.saveAlarm() },
                    variant = LumenButtonVariant.PRIMARY,
                    modifier = Modifier.padding(end = 4.dp),
                )
            }

            Spacer(Modifier.height(8.dp))

            // Time Picker
            TimePickerCard(
                hour = uiState.hour,
                minute = uiState.minute,
                onTimeChanged = viewModel::setTime,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
            )

            Spacer(Modifier.height(20.dp))

            // Label
            OutlinedTextField(
                value = uiState.label,
                onValueChange = viewModel::setLabel,
                label = { Text("Label", color = colors.ink2) },
                placeholder = { Text("e.g. Morning ritual", color = colors.ink3) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.ink0,
                    unfocusedTextColor = colors.ink0,
                    focusedBorderColor = IndigoAccent,
                    unfocusedBorderColor = colors.lineSubtle,
                    cursorColor = IndigoAccent,
                ),
                shape = ShapeMedium,
            )

            Spacer(Modifier.height(24.dp))
            SectionLabel("Configure", modifier = Modifier.padding(horizontal = 22.dp))

            // Settings card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .clip(ShapeLarge)
                    .background(colors.bgCard),
            ) {
                // Repeat
                AlarmFormRow(
                    icon = Icons.Default.Repeat,
                    iconColor = IndigoAccent,
                    label = "Repeat",
                    value = when {
                        uiState.repeatDays.isEmpty() -> "Once"
                        uiState.repeatDays.size == 7 -> "Every day"
                        uiState.repeatDays == setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY) -> "Weekdays"
                        else -> "${uiState.repeatDays.size} days"
                    },
                    onClick = { viewModel.showRepeatSheet() },
                    trailing = {
                        DayDots(selectedDays = uiState.repeatDays)
                    }
                )
                HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))

                // Sound
                AlarmFormRow(
                    icon = Icons.Default.MusicNote,
                    iconColor = LilacAccent,
                    label = "Sound",
                    value = uiState.soundName,
                    onClick = { onPickSound(alarmId ?: -1L) },
                )
                HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))

                // Vibration
                AlarmFormRow(
                    icon = Icons.Default.Vibration,
                    iconColor = AuroraAccent,
                    label = "Vibration",
                    value = uiState.vibrationPattern.displayName,
                    onClick = { viewModel.showVibrationSheet() },
                )
                HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))

                // Snooze
                AlarmFormRow(
                    icon = Icons.Default.Snooze,
                    iconColor = GoldAccent,
                    label = "Snooze",
                    value = "${uiState.snoozeMinutes} min · ${uiState.snoozeMaxCount}× limit",
                    onClick = { viewModel.showSnoozeSheet() },
                )
                HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))

                // Volume ramp-up
                AlarmFormRowToggle(
                    icon = Icons.Default.VolumeUp,
                    iconColor = IndigoAccent,
                    label = "Volume ramp-up",
                    description = "Gradually increase volume",
                    checked = uiState.volumeRampUp,
                    onCheckedChange = viewModel::setVolumeRampUp,
                )
                HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))

                // Flashlight
                AlarmFormRowToggle(
                    icon = Icons.Default.FlashOn,
                    iconColor = GoldAccent,
                    label = "Flashlight",
                    description = "Flash to help you wake up",
                    checked = uiState.flashlight,
                    onCheckedChange = viewModel::setFlashlight,
                )
            }

            Spacer(Modifier.height(16.dp))
            SectionLabel("Wake type", modifier = Modifier.padding(horizontal = 22.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .clip(ShapeLarge)
                    .background(colors.bgCard),
            ) {
                // Smart Wake
                AlarmFormRowToggle(
                    icon = Icons.Default.AutoAwesome,
                    iconColor = AuroraAccent,
                    label = "Smart Wake",
                    description = "AI picks optimal time in sleep cycle",
                    checked = uiState.isSmartWake,
                    onCheckedChange = viewModel::setSmartWake,
                    badge = "AI",
                    badgeColor = AuroraAccent,
                )
                HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))

                // Challenge type
                AlarmFormRow(
                    icon = Icons.Default.Science,
                    iconColor = IndigoAccent,
                    label = "Challenge",
                    value = uiState.challengeType.displayName,
                    onClick = { onPickChallenge(alarmId ?: -1L) },
                )
                HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))

                // Location
                AlarmFormRow(
                    icon = Icons.Default.LocationOn,
                    iconColor = RoseAccent,
                    label = "Location trigger",
                    value = if (uiState.alarmType == AlarmType.LOCATION) "Set" else "Off",
                    onClick = { onPickLocation(alarmId ?: -1L) },
                )
                HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))

                // Routine
                AlarmFormRow(
                    icon = Icons.Default.PlayCircle,
                    iconColor = AuroraAccent,
                    label = "Morning routine",
                    value = "Configure",
                    onClick = { onPickRoutine(alarmId ?: -1L) },
                )
            }

            Spacer(Modifier.height(80.dp))
        }

        // Repeat bottom sheet
        if (uiState.showRepeatSheet) {
            RepeatBottomSheet(
                selectedDays = uiState.repeatDays,
                onDaysChanged = viewModel::setRepeatDays,
                onDismiss = { viewModel.hideRepeatSheet() },
            )
        }

        // Snooze bottom sheet
        if (uiState.showSnoozeSheet) {
            SnoozeDurationSheet(
                currentMinutes = uiState.snoozeMinutes,
                onSelected = { min -> viewModel.setSnooze(min, uiState.snoozeMaxCount) },
                onDismiss = { viewModel.hideSnoozeSheet() },
            )
        }

        // Vibration bottom sheet
        if (uiState.showVibrationSheet) {
            VibrationPatternSheet(
                current = uiState.vibrationPattern,
                onSelected = viewModel::setVibration,
                onDismiss = { viewModel.hideVibrationSheet() },
            )
        }
    }

    // Discard dialog
    if (uiState.showDiscardDialog) {
        DiscardChangesDialog(
            onKeepEditing = { viewModel.cancelDiscard() },
            onSaveAndClose = { viewModel.saveAlarm() },
            onDiscard = { viewModel.confirmDiscard() },
        )
    }
}

@Composable
private fun TimePickerCard(
    hour: Int,
    minute: Int,
    onTimeChanged: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.lumen
    var h by remember(hour) { mutableIntStateOf(hour) }
    var m by remember(minute) { mutableIntStateOf(minute) }
    val isAm = h < 12
    val displayHour = if (h % 12 == 0) 12 else h % 12

    GlowCard(modifier = modifier, glowColor = IndigoAccent) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Hour scroll
                NumberScroller(
                    value = displayHour,
                    range = 1..12,
                    onValueChange = { v ->
                        h = if (isAm) { if (v == 12) 0 else v } else { if (v == 12) 12 else v + 12 }
                        onTimeChanged(h, m)
                    },
                )
                Text(
                    ":",
                    style = MaterialTheme.typography.displayMedium,
                    color = colors.ink0,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
                // Minute scroll
                NumberScroller(
                    value = m,
                    range = 0..59,
                    padStart = true,
                    onValueChange = { v ->
                        m = v
                        onTimeChanged(h, m)
                    },
                )
                Spacer(Modifier.width(16.dp))
                // AM/PM toggle
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("AM", "PM").forEach { period ->
                        val selected = (period == "AM") == isAm
                        Box(
                            modifier = Modifier
                                .clip(ShapeMedium)
                                .background(if (selected) IndigoAccent else colors.bgHover)
                                .clickable {
                                    h = if (period == "AM") {
                                        if (h >= 12) h - 12 else h
                                    } else {
                                        if (h < 12) h + 12 else h
                                    }
                                    onTimeChanged(h, m)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                period,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selected) Color.White else colors.ink2,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberScroller(
    value: Int,
    range: IntRange,
    padStart: Boolean = false,
    onValueChange: (Int) -> Unit,
) {
    val colors = MaterialTheme.lumen

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp),
    ) {
        IconButton(onClick = {
            val next = if (value >= range.last) range.first else value + 1
            onValueChange(next)
        }) {
            Icon(Icons.Default.KeyboardArrowUp, null, tint = colors.ink2)
        }

        val displayText = if (padStart) value.toString().padStart(2, '0')
        else value.toString()

        Text(
            text = displayText,
            style = MaterialTheme.typography.displayMedium,
            color = colors.ink0,
            fontWeight = FontWeight.Bold,
        )

        IconButton(onClick = {
            val prev = if (value <= range.first) range.last else value - 1
            onValueChange(prev)
        }) {
            Icon(Icons.Default.KeyboardArrowDown, null, tint = colors.ink2)
        }
    }
}

@Composable
private fun AlarmFormRow(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    onClick: () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = MaterialTheme.lumen
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(ShapeSmall)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = colors.ink0)
            if (value.isNotBlank()) {
                Text(value, style = MaterialTheme.typography.bodySmall, color = colors.ink2)
            }
        }
        trailing?.invoke() ?: run {
            Icon(Icons.Default.ChevronRight, null, tint = colors.ink3, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun AlarmFormRowToggle(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    description: String = "",
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    badge: String? = null,
    badgeColor: Color = IndigoAccent,
) {
    val colors = MaterialTheme.lumen
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(ShapeSmall)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(label, style = MaterialTheme.typography.bodyLarge, color = colors.ink0)
                if (badge != null) {
                    LumenPill(badge, color = badgeColor, small = true)
                }
            }
            if (description.isNotBlank()) {
                Text(description, style = MaterialTheme.typography.bodySmall, color = colors.ink2)
            }
        }
        LumenToggle(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RepeatBottomSheet(
    selectedDays: Set<DayOfWeek>,
    onDaysChanged: (Set<DayOfWeek>) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = MaterialTheme.lumen
    var current by remember { mutableStateOf(selectedDays) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.bgCard,
        dragHandle = { BottomSheetDefaults.DragHandle(color = colors.ink3) },
    ) {
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)) {
            Text("Repeat", style = MaterialTheme.typography.headlineSmall, color = colors.ink0, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            val presets = listOf(
                "Weekdays" to setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
                "Weekends" to setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
                "Every day" to DayOfWeek.entries.toSet(),
                "Once" to emptySet<DayOfWeek>(),
            )
            presets.forEach { (name, days) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ShapeMedium)
                        .clickable { current = days }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(name, style = MaterialTheme.typography.bodyLarge, color = colors.ink0, modifier = Modifier.weight(1f))
                    if (current == days) Icon(Icons.Default.Check, null, tint = IndigoAccent, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("Custom", style = MaterialTheme.typography.bodyMedium, color = colors.ink2)
            Spacer(Modifier.height(12.dp))

            DayOfWeek.entries.forEach { day ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(day.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyLarge, color = colors.ink0, modifier = Modifier.weight(1f))
                    LumenToggle(
                        checked = day in current,
                        onCheckedChange = { on ->
                            val updated = current.toMutableSet()
                            if (on) updated.add(day) else updated.remove(day)
                            current = updated
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            LumenButton(
                text = "Done",
                onClick = { onDaysChanged(current); onDismiss() },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SnoozeDurationSheet(
    currentMinutes: Int,
    onSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = MaterialTheme.lumen
    val options = listOf(3, 5, 10, 15, 30)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.bgCard,
        dragHandle = { BottomSheetDefaults.DragHandle(color = colors.ink3) },
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text("Snooze duration", style = MaterialTheme.typography.headlineSmall, color = colors.ink0, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            options.forEach { mins ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ShapeMedium)
                        .clickable { onSelected(mins); onDismiss() }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "$mins min${if (mins == 5) "  (default)" else ""}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (mins == currentMinutes) IndigoAccent else colors.ink0,
                        fontWeight = if (mins == currentMinutes) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.weight(1f),
                    )
                    if (mins == currentMinutes) Icon(Icons.Default.Check, null, tint = IndigoAccent, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VibrationPatternSheet(
    current: VibrationPattern,
    onSelected: (VibrationPattern) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = MaterialTheme.lumen
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.bgCard,
        dragHandle = { BottomSheetDefaults.DragHandle(color = colors.ink3) },
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text("Vibration pattern", style = MaterialTheme.typography.headlineSmall, color = colors.ink0, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            VibrationPattern.entries.forEach { pattern ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ShapeMedium)
                        .clickable { onSelected(pattern); onDismiss() }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(pattern.displayName, style = MaterialTheme.typography.bodyLarge,
                            color = if (pattern == current) IndigoAccent else colors.ink0)
                        Text(pattern.description, style = MaterialTheme.typography.bodySmall, color = colors.ink2)
                    }
                    if (pattern == current) Icon(Icons.Default.Check, null, tint = IndigoAccent, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DiscardChangesDialog(
    onKeepEditing: () -> Unit,
    onSaveAndClose: () -> Unit,
    onDiscard: () -> Unit,
) {
    val colors = MaterialTheme.lumen
    AlertDialog(
        onDismissRequest = onKeepEditing,
        containerColor = colors.bgCard,
        title = { Text("Discard changes?", color = colors.ink0, style = MaterialTheme.typography.headlineSmall) },
        text = { Text("You have unsaved changes.", color = colors.ink1, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LumenButton("Save & close", onClick = onSaveAndClose, modifier = Modifier.fillMaxWidth())
                LumenButton("Discard", onClick = onDiscard, variant = LumenButtonVariant.DANGER, modifier = Modifier.fillMaxWidth())
                LumenButton("Keep editing", onClick = onKeepEditing, variant = LumenButtonVariant.GHOST, modifier = Modifier.fillMaxWidth())
            }
        }
    )
}
