package com.lumen.alarm.ui.screens.insights

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lumen.alarm.ui.components.*
import com.lumen.alarm.ui.theme.*

@Composable
fun InsightsScreen(
    onBack: () -> Unit,
    viewModel: InsightsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.lumen

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgApp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        LumenTopBar(
            title = "Insights",
            onBack = onBack,
            trailingContent = {
                LumenPill("Last 30d", color = IndigoAccent, small = true)
            },
        )

        if (uiState.isLoading) {
            InsightsSkeleton()
        } else {
            // Score grid
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                InsightMetricCard(
                    label = "Wake-up rate",
                    value = "${(uiState.wakeUpRate * 100).toInt()}%",
                    change = "+6% vs last month",
                    changePositive = true,
                    color = AuroraAccent,
                    modifier = Modifier.weight(1f),
                )
                InsightMetricCard(
                    label = "Avg snoozes",
                    value = String.format("%.1f", uiState.avgSnoozes),
                    change = "down from 2.7",
                    changePositive = true,
                    color = IndigoAccent,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                InsightMetricCard(
                    label = "Sleep avg",
                    value = "7h 12m",
                    change = "goal 7h 30m",
                    changePositive = false,
                    color = LilacAccent,
                    modifier = Modifier.weight(1f),
                )
                InsightMetricCard(
                    label = "Best streak",
                    value = "${uiState.bestStreak}d",
                    change = "current ${uiState.currentStreak}d",
                    changePositive = true,
                    color = GoldAccent,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(24.dp))
            SectionLabel("Wake-up timing", modifier = Modifier.padding(horizontal = 22.dp))

            // Wake timing bar chart
            WakeTimingChart(
                data = uiState.wakeTimingDays,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .clip(ShapeLarge)
                    .background(colors.bgCard)
                    .padding(18.dp),
            )

            Spacer(Modifier.height(16.dp))
            SectionLabel("Snooze heatmap", modifier = Modifier.padding(horizontal = 22.dp))

            // Snooze heatmap
            SnoozeHeatmap(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .clip(ShapeLarge)
                    .background(colors.bgCard)
                    .padding(18.dp),
            )

            Spacer(Modifier.height(16.dp))

            // Smart insight
            GlowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                glowColor = AuroraAccent,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.AutoAwesome, null, tint = AuroraAccent, modifier = Modifier.size(24.dp))
                    Text(
                        "You dismiss alarms fastest on Tuesdays — try scheduling important meetings then.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.ink1,
                    )
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun InsightMetricCard(
    label: String,
    value: String,
    change: String,
    changePositive: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.lumen
    Column(
        modifier = modifier
            .clip(ShapeLarge)
            .background(colors.bgCard)
            .border(1.dp, color.copy(alpha = 0.25f), ShapeLarge)
            .padding(16.dp),
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = colors.ink2)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.displaySmall, color = color, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Icon(
                if (changePositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                null,
                tint = if (changePositive) AuroraAccent else RoseAccent,
                modifier = Modifier.size(12.dp),
            )
            Text(change, style = MaterialTheme.typography.bodySmall, color = if (changePositive) AuroraAccent else colors.ink2)
        }
    }
}

@Composable
private fun WakeTimingChart(data: List<Int>, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.lumen
    val days = listOf("M", "T", "W", "T", "F", "S", "S", "M", "T", "W", "T", "F", "S", "S")

    Column(modifier = modifier) {
        Text("Minutes from target", style = MaterialTheme.typography.bodySmall, color = colors.ink2)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            val mockData = listOf(2, 0, 5, 1, 3, 8, 12, 0, 2, 1, 0, 4, 6, 2)
            mockData.forEachIndexed { i, v ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((v.coerceAtLeast(2) * 4).dp)
                            .clip(ShapePill)
                            .background(
                                when {
                                    v == 0 -> AuroraAccent
                                    v < 5 -> IndigoAccent
                                    else -> RoseAccent
                                }.copy(alpha = 0.7f)
                            )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(days.getOrElse(i) { "" }, style = MaterialTheme.typography.labelSmall, color = colors.ink3)
                }
            }
        }
    }
}

@Composable
private fun SnoozeHeatmap(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.lumen
    val dayLabels = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    val weeks = 4
    val mock = remember { List(7) { List(weeks) { (Math.random() * 4).toInt() } } }

    Column(modifier = modifier) {
        Text("Snooze frequency by day", style = MaterialTheme.typography.bodySmall, color = colors.ink2)
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("", modifier = Modifier.width(22.dp))
            repeat(weeks) {
                Text("W${it + 1}", style = MaterialTheme.typography.labelSmall, color = colors.ink3, modifier = Modifier.weight(1f))
            }
        }
        dayLabels.forEachIndexed { dayIdx, dayLabel ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(dayLabel, style = MaterialTheme.typography.labelSmall, color = colors.ink2, modifier = Modifier.width(22.dp))
                repeat(weeks) { weekIdx ->
                    val count = mock.getOrNull(dayIdx)?.getOrNull(weekIdx) ?: 0
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(ShapeSmall)
                            .background(
                                when (count) {
                                    0 -> colors.bgHover
                                    1 -> IndigoAccent.copy(alpha = 0.3f)
                                    2 -> IndigoAccent.copy(alpha = 0.6f)
                                    else -> RoseAccent.copy(alpha = 0.7f)
                                }
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightsSkeleton() {
    val inf = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by inf.animateFloat(
        0.3f, 0.7f,
        infiniteRepeatable(tween(1600, easing = LinearEasing), RepeatMode.Reverse),
        label = "shimmer_alpha",
    )
    val colors = MaterialTheme.lumen
    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(ShapeLarge)
                    .background(colors.bgCard.copy(alpha = shimmerAlpha))
            )
        }
    }
}
