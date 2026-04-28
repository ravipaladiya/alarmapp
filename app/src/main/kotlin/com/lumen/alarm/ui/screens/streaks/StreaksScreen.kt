package com.lumen.alarm.ui.screens.streaks

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lumen.alarm.ui.components.*
import com.lumen.alarm.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun StreaksScreen(
    onBack: () -> Unit,
    viewModel: StreaksViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.lumen

    val monthName = remember {
        YearMonth.now().month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }
    var showShareMilestone by remember { mutableStateOf(uiState.currentStreak >= 21) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgApp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        LumenTopBar(title = "Streaks", onBack = onBack)

        // Hero streak card
        StreakHeroCard(
            streak = uiState.currentStreak,
            personalBest = uiState.longestStreak,
            coins = uiState.totalCoins,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp),
        )

        Spacer(Modifier.height(16.dp))

        // Progress to next milestone
        StreakProgressBar(
            current = uiState.currentStreak,
            target = 21,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
        )

        Spacer(Modifier.height(20.dp))
        SectionLabel("$monthName calendar", modifier = Modifier.padding(horizontal = 22.dp))

        // Month calendar
        MonthCalendar(
            month = YearMonth.now(),
            completedDates = uiState.completedDates,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(ShapeLarge)
                .background(colors.bgCard)
                .padding(16.dp),
        )

        Spacer(Modifier.height(20.dp))
        SectionLabel("Theme shop", modifier = Modifier.padding(horizontal = 22.dp))

        // Theme shop
        ThemeShop(
            coins = uiState.totalCoins,
            modifier = Modifier.padding(horizontal = 18.dp),
        )

        Spacer(Modifier.height(80.dp))
    }

    // Streak share milestone dialog
    if (showShareMilestone) {
        StreakShareMilestoneDialog(
            streak = uiState.currentStreak,
            onShare = { /* share intent */ showShareMilestone = false },
            onSkip = { showShareMilestone = false },
        )
    }
}

@Composable
private fun StreakHeroCard(
    streak: Int,
    personalBest: Int,
    coins: Int,
    modifier: Modifier = Modifier,
) {
    val inf = rememberInfiniteTransition(label = "hero")
    val glowScale by inf.animateFloat(
        0.95f, 1.05f,
        infiniteRepeatable(tween(2800, easing = EaseInOut), RepeatMode.Reverse),
        label = "glow",
    )

    GlowCard(modifier = modifier, glowColor = GoldAccent) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .scale(glowScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(GoldAccent.copy(alpha = 0.4f), Color.Transparent))
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.LocalFireDepartment, null, tint = GoldAccent, modifier = Modifier.size(32.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "$streak",
                        style = MaterialTheme.typography.displaySmall,
                        color = GoldAccent,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        "day streak",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.lumen.ink1,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                Text(
                    "Personal best: $personalBest days",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.lumen.ink2,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Icon(Icons.Default.MonetizationOn, null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                Text(
                    "$coins",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold,
                )
                Text("coins", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.lumen.ink2)
            }
        }
    }
}

@Composable
private fun StreakProgressBar(current: Int, target: Int, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.lumen
    val progress = (current.toFloat() / target).coerceIn(0f, 1f)
    val animProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "progress",
    )

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Progress to $target days", style = MaterialTheme.typography.bodySmall, color = colors.ink2)
            Text("$current / $target", style = MaterialTheme.typography.bodySmall, color = GoldAccent, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(ShapePill)
                .background(colors.bgCard),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animProgress)
                    .fillMaxHeight()
                    .clip(ShapePill)
                    .background(Brush.horizontalGradient(listOf(GoldAccent, RoseAccent)))
            )
        }
    }
}

@Composable
private fun MonthCalendar(
    month: YearMonth,
    completedDates: List<LocalDate>,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.lumen
    val today = LocalDate.now()
    val firstDay = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()
    val startOffset = (firstDay.dayOfWeek.value - 1)

    Column(modifier = modifier) {
        // Day headers
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach { day ->
                Text(
                    day,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.ink3,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        val totalCells = startOffset + daysInMonth
        val rows = (totalCells + 6) / 7

        repeat(rows) { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(7) { col ->
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - startOffset + 1
                    val date = if (dayNumber in 1..daysInMonth) month.atDay(dayNumber) else null
                    val isCompleted = date != null && date in completedDates
                    val isToday = date == today
                    val isPast = date != null && date.isBefore(today) && !isCompleted

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> GoldAccent.copy(alpha = 0.85f)
                                    isToday -> IndigoAccent
                                    else -> Color.Transparent
                                }
                            )
                            .then(
                                if (isToday && !isCompleted) Modifier.border(1.dp, IndigoAccent, CircleShape)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (date != null) {
                            if (isCompleted) {
                                Icon(Icons.Default.LocalFireDepartment, null, tint = Color.White, modifier = Modifier.size(14.dp))
                            } else {
                                Text(
                                    "$dayNumber",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when {
                                        isToday -> Color.White
                                        isPast -> colors.ink3
                                        else -> colors.ink1
                                    },
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp,
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun ThemeShop(coins: Int, modifier: Modifier = Modifier) {
    val themes = listOf(
        Triple("Sky", 500, IndigoAccent),
        Triple("Amber", 350, GoldAccent),
        Triple("Forest", 800, AuroraAccent),
    )
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        themes.forEach { (name, cost, color) ->
            val affordable = coins >= cost
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(ShapeLarge)
                    .background(color.copy(alpha = 0.15f))
                    .border(1.dp, color.copy(alpha = 0.3f), ShapeLarge)
                    .clickable(enabled = affordable) { /* purchase */ }
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Palette, null, tint = color, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text(name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.lumen.ink0, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Icon(Icons.Default.MonetizationOn, null, tint = GoldAccent, modifier = Modifier.size(10.dp))
                    Text(
                        "$cost",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (affordable) GoldAccent else MaterialTheme.lumen.ink3,
                    )
                }
            }
        }
    }
}

@Composable
private fun StreakShareMilestoneDialog(
    streak: Int,
    onShare: () -> Unit,
    onSkip: () -> Unit,
) {
    val colors = MaterialTheme.lumen
    AlertDialog(
        onDismissRequest = onSkip,
        containerColor = colors.bgCard,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                LumenPill("NEW MILESTONE", color = GoldAccent)
                Spacer(Modifier.height(8.dp))
                Text(
                    "$streak-day streak",
                    style = MaterialTheme.typography.headlineLarge,
                    color = GoldAccent,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    "That's officially a habit. Wear it.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.ink1,
                    textAlign = TextAlign.Center,
                )
            }
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LumenButton("Skip", onClick = onSkip, variant = LumenButtonVariant.GHOST, modifier = Modifier.weight(1f))
                LumenButton("Share streak", onClick = onShare, variant = LumenButtonVariant.GOLD, modifier = Modifier.weight(1f))
            }
        },
    )
}
