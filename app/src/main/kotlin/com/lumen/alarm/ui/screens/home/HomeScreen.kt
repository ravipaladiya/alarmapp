package com.lumen.alarm.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lumen.alarm.domain.model.Alarm
import com.lumen.alarm.domain.model.AlarmType
import com.lumen.alarm.domain.model.ChallengeType
import com.lumen.alarm.ui.components.*
import com.lumen.alarm.ui.theme.*

@Composable
fun HomeScreen(
    onAddAlarm: () -> Unit,
    onEditAlarm: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenInsights: () -> Unit,
    onOpenStreaks: () -> Unit,
    onOpenSleep: () -> Unit,
    onOpenWorldClock: () -> Unit,
    onOpenPlanner: () -> Unit,
    onOpenPremium: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val prefs by viewModel.prefs.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.lumen.bgApp)
    ) {
        // Ambient aurora background
        AuroraBackground()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp),
        ) {
            item {
                // Header
                HomeHeader(
                    greeting = uiState.greeting,
                    dateText = uiState.dateText,
                    onSettings = onOpenSettings,
                )
            }

            item {
                // Quick nav pills
                QuickNavPills(
                    onInsights = onOpenInsights,
                    onStreaks = onOpenStreaks,
                    onSleep = onOpenSleep,
                    onPlanner = onOpenPlanner,
                )
            }

            // Next alarm hero card
            if (uiState.nextAlarm != null) {
                item {
                    NextAlarmHeroCard(
                        alarm = uiState.nextAlarm!!,
                        countdown = uiState.countdownText,
                        is24h = prefs.is24HourFormat,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                    )
                }
            }

            // Alarm list
            if (uiState.alarms.isNotEmpty()) {
                item {
                    SectionLabel(
                        text = "All alarms",
                        modifier = Modifier.padding(start = 22.dp, top = 12.dp),
                    )
                }
                items(uiState.alarms, key = { it.id }) { alarm ->
                    AlarmRow(
                        alarm = alarm,
                        is24h = prefs.is24HourFormat,
                        isSelected = alarm.id in uiState.selectedAlarmIds,
                        isMultiSelectMode = uiState.isMultiSelectMode,
                        onToggle = { enabled -> viewModel.setAlarmEnabled(alarm.id, enabled) },
                        onEdit = { onEditAlarm(alarm.id) },
                        onLongPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.enterMultiSelect(alarm.id)
                        },
                        onSelectToggle = { viewModel.toggleAlarmSelection(alarm.id) },
                        onDelete = { viewModel.requestDeleteAlarm(alarm) },
                        modifier = Modifier
                            .padding(horizontal = 18.dp, vertical = 4.dp)
                            .animateItem(),
                    )
                }
            } else {
                item {
                    EmptyAlarmsState(
                        onSetFirstAlarm = onAddAlarm,
                        modifier = Modifier.padding(32.dp),
                    )
                }
            }
        }

        // Multi-select toolbar
        AnimatedVisibility(
            visible = uiState.isMultiSelectMode,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            MultiSelectToolbar(
                count = uiState.selectedAlarmIds.size,
                onClose = { viewModel.exitMultiSelect() },
                onDelete = { viewModel.deleteSelected() },
                onDisable = { viewModel.disableSelected() },
            )
        }

        // FAB
        LumenFab(
            onClick = onAddAlarm,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .navigationBarsPadding(),
        )

        // Toast
        AnimatedVisibility(
            visible = uiState.toastMessage != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
                .statusBarsPadding(),
        ) {
            uiState.toastMessage?.let { msg ->
                LumenToast(message = msg, onDismiss = { viewModel.dismissToast() })
            }
        }
    }

    // Delete confirmation dialog
    if (uiState.showDeleteDialog) {
        DeleteAlarmDialog(
            alarm = uiState.alarmToDelete,
            onConfirm = { viewModel.confirmDeleteAlarm() },
            onDismiss = { viewModel.cancelDeleteAlarm() },
        )
    }
}

@Composable
private fun AuroraBackground() {
    val inf = rememberInfiniteTransition(label = "aurora")
    val offset by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Reverse),
        label = "aurora_offset",
    )
    val colors = MaterialTheme.lumen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        IndigoAccent.copy(alpha = 0.08f),
                        AuroraAccent.copy(alpha = 0.04f),
                        Color.Transparent,
                    ),
                    center = androidx.compose.ui.geometry.Offset(offset * 400f, 200f),
                    radius = 600f,
                )
            )
    )
}

@Composable
private fun HomeHeader(
    greeting: String,
    dateText: String,
    onSettings: () -> Unit,
) {
    val colors = MaterialTheme.lumen
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 22.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineLarge,
                color = colors.ink0,
            )
            Text(
                text = dateText,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.ink2,
            )
        }
        IconButton(
            onClick = onSettings,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(colors.bgCard),
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Settings",
                tint = colors.ink1,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun QuickNavPills(
    onInsights: () -> Unit,
    onStreaks: () -> Unit,
    onSleep: () -> Unit,
    onPlanner: () -> Unit,
) {
    val colors = MaterialTheme.lumen
    val pills = listOf(
        Triple("Insights", Icons.Default.BarChart, onInsights),
        Triple("Streaks", Icons.Default.LocalFireDepartment, onStreaks),
        Triple("Sleep", Icons.Default.Bedtime, onSleep),
        Triple("Planner", Icons.Default.CalendarToday, onPlanner),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        pills.forEach { (label, icon, action) ->
            Row(
                modifier = Modifier
                    .clip(ShapePill)
                    .background(colors.bgCard)
                    .border(1.dp, colors.lineSubtle, ShapePill)
                    .clickable(onClick = action)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(icon, contentDescription = null, tint = IndigoAccent, modifier = Modifier.size(14.dp))
                Text(label, style = MaterialTheme.typography.bodySmall, color = colors.ink1)
            }
        }
    }
}

@Composable
private fun NextAlarmHeroCard(
    alarm: Alarm,
    countdown: String,
    is24h: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.lumen
    val timeText = if (is24h) alarm.time24h else alarm.timeLabel

    GlowCard(
        modifier = modifier.fillMaxWidth(),
        glowColor = IndigoAccent,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                LumenPill(
                    text = "Next alarm",
                    color = IndigoAccent,
                    showLive = true,
                    small = true,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.displayMedium,
                    color = colors.ink0,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = countdown,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.ink2,
                )
                if (alarm.label.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = alarm.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.ink1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(8.dp))
                if (alarm.repeatDays.isNotEmpty()) {
                    DayDots(
                        selectedDays = alarm.repeatDays,
                        modifier = Modifier,
                    )
                }
            }
            // Alarm type badge
            AlarmTypeBadge(alarm.alarmType)
        }
    }
}

@Composable
private fun AlarmTypeBadge(type: AlarmType) {
    val (icon, color) = when (type) {
        AlarmType.SMART_WAKE -> Icons.Default.AutoAwesome to AuroraAccent
        AlarmType.CHALLENGE -> Icons.Default.Science to IndigoAccent
        AlarmType.LOCATION -> Icons.Default.LocationOn to GoldAccent
        AlarmType.NORMAL -> Icons.Default.Alarm to MaterialTheme.lumen.ink2
    }
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
    }
}

@Composable
fun AlarmRow(
    alarm: Alarm,
    is24h: Boolean,
    isSelected: Boolean,
    isMultiSelectMode: Boolean,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onLongPress: () -> Unit,
    onSelectToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.lumen
    val timeText = if (is24h) alarm.time24h else alarm.timeLabel
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) IndigoAccent.copy(alpha = 0.12f) else colors.bgCard,
        label = "row_bg",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShapeLarge)
            .background(bgColor)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) IndigoAccent.copy(alpha = 0.4f) else Color.Transparent,
                shape = ShapeLarge,
            )
            .combinedClickable(
                onClick = if (isMultiSelectMode) onSelectToggle else onEdit,
                onLongClick = onLongPress,
            )
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .semantics {
                contentDescription = "${alarm.label.ifBlank { "Alarm" }}, " +
                    "$timeText, ${alarm.repeatLabel}, " +
                    "${if (alarm.isEnabled) "on" else "off"}, " +
                    "double tap to ${if (isMultiSelectMode) "select" else "edit"}"
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Multi-select checkbox
        AnimatedVisibility(visible = isMultiSelectMode) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) IndigoAccent else colors.bgHover)
                    .padding(end = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (isSelected) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (alarm.isEnabled) colors.ink0 else colors.ink3,
                )
                Spacer(Modifier.width(8.dp))
                AlarmTypeIndicator(alarm)
            }
            Spacer(Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (alarm.label.isNotBlank()) {
                    Text(
                        text = alarm.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.ink2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Text(
                    text = alarm.repeatLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.ink2,
                )
            }
            if (alarm.repeatDays.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                DayDots(selectedDays = alarm.repeatDays)
            }
        }

        Spacer(Modifier.width(12.dp))
        LumenToggle(
            checked = alarm.isEnabled,
            onCheckedChange = onToggle,
            contentDescription = if (alarm.isEnabled) "Alarm enabled" else "Alarm disabled",
        )
    }
}

@Composable
private fun AlarmTypeIndicator(alarm: Alarm) {
    when {
        alarm.isSmartWake -> Icon(
            Icons.Default.AutoAwesome, null,
            tint = AuroraAccent, modifier = Modifier.size(14.dp),
        )
        alarm.challengeType != ChallengeType.NONE -> Icon(
            Icons.Default.Science, null,
            tint = IndigoAccent, modifier = Modifier.size(14.dp),
        )
        alarm.alarmType == AlarmType.LOCATION -> Icon(
            Icons.Default.LocationOn, null,
            tint = GoldAccent, modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun MultiSelectToolbar(
    count: Int,
    onClose: () -> Unit,
    onDelete: () -> Unit,
    onDisable: () -> Unit,
) {
    val colors = MaterialTheme.lumen
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(IndigoAccent.copy(alpha = 0.95f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClose) {
            Icon(Icons.Default.Close, "Deselect all", tint = Color.White)
        }
        Text(
            text = "$count selected",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.SemiBold,
        )
        IconButton(onClick = onDisable) {
            Icon(Icons.Default.NotificationsOff, "Disable selected", tint = Color.White)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, "Delete selected", tint = Color.White)
        }
    }
}

@Composable
private fun EmptyAlarmsState(
    onSetFirstAlarm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.lumen
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .border(2.dp, colors.lineMedium, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Bedtime, null, tint = IndigoAccent, modifier = Modifier.size(32.dp))
        }
        Text(
            text = "No alarms yet",
            style = MaterialTheme.typography.headlineSmall,
            color = colors.ink0,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Set your first alarm and start building a consistent morning ritual.",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.ink2,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        LumenButton(
            text = "Set first alarm",
            onClick = onSetFirstAlarm,
            variant = LumenButtonVariant.PRIMARY,
        )
        // Quick-start chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("6:00 Workout", "7:30 Work", "6:45 School").forEach { chip ->
                Row(
                    modifier = Modifier
                        .clip(ShapePill)
                        .background(colors.bgCard)
                        .border(1.dp, colors.lineSubtle, ShapePill)
                        .clickable(onClick = onSetFirstAlarm)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(Icons.Default.Alarm, null, tint = IndigoAccent, modifier = Modifier.size(12.dp))
                    Text(chip, style = MaterialTheme.typography.bodySmall, color = colors.ink1)
                }
            }
        }
    }
}

@Composable
private fun LumenToast(message: String, onDismiss: () -> Unit) {
    val colors = MaterialTheme.lumen
    Row(
        modifier = Modifier
            .clip(ShapeLarge)
            .background(colors.bgCard)
            .border(1.dp, AuroraAccent.copy(alpha = 0.3f), ShapeLarge)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(Icons.Default.CheckCircle, null, tint = AuroraAccent, modifier = Modifier.size(16.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = colors.ink0)
    }
}

@Composable
private fun DeleteAlarmDialog(
    alarm: Alarm?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = MaterialTheme.lumen
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.bgCard,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.Delete, null, tint = RoseAccent, modifier = Modifier.size(22.dp))
                Text("Delete this alarm?", color = colors.ink0, style = MaterialTheme.typography.headlineSmall)
            }
        },
        text = {
            alarm?.let {
                Text(
                    text = "\"${it.label.ifBlank { it.timeLabel }}\" will be permanently deleted.",
                    color = colors.ink1,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
        confirmButton = {
            LumenButton(text = "Delete", onClick = onConfirm, variant = LumenButtonVariant.DANGER)
        },
        dismissButton = {
            LumenButton(text = "Cancel", onClick = onDismiss, variant = LumenButtonVariant.GHOST)
        },
    )
}
