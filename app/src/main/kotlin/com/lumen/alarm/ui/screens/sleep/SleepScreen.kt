package com.lumen.alarm.ui.screens.sleep

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lumen.alarm.ui.components.*
import com.lumen.alarm.ui.theme.*

@Composable
fun SleepScreen(onBack: () -> Unit) {
    val colors = MaterialTheme.lumen

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgApp)
            .verticalScroll(rememberScrollState())
    ) {
        LumenTopBar(
            title = "Sleep tonight",
            onBack = onBack,
            trailingContent = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.CalendarToday, null, tint = colors.ink2, modifier = Modifier.size(20.dp))
                }
            },
        )

        // Sleep window card
        GlowCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            glowColor = AuroraAccent,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                LumenPill("Suggested window", color = AuroraAccent, small = true)
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Bedtime", style = MaterialTheme.typography.bodySmall, color = colors.ink2)
                        Text("10:42 PM", style = MaterialTheme.typography.displaySmall,
                            color = colors.ink0, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.ArrowForward, null, tint = AuroraAccent, modifier = Modifier.size(20.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Wake time", style = MaterialTheme.typography.bodySmall, color = colors.ink2)
                        Text("6:18 AM", style = MaterialTheme.typography.displaySmall,
                            color = AuroraAccent, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("7h 36m · 5 sleep cycles", style = MaterialTheme.typography.bodySmall, color = colors.ink2)
                Spacer(Modifier.height(16.dp))
                // Sleep arc visualization
                SleepArcCanvas(modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
        SectionLabel("Sleep cycles", modifier = Modifier.padding(horizontal = 22.dp))

        // Sleep cycles graph
        SleepCyclesGraph(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(ShapeLarge)
                .background(colors.bgCard)
                .padding(16.dp),
        )

        Spacer(Modifier.height(16.dp))
        SectionLabel("Data sources", modifier = Modifier.padding(horizontal = 22.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(ShapeLarge)
                .background(colors.bgCard),
        ) {
            listOf(
                Triple(Icons.Default.Watch, "Pixel Watch 2", "Connected"),
                Triple(Icons.Default.PhoneAndroid, "Phone position sensor", "On"),
                Triple(Icons.Default.Edit, "Manual log", "Off"),
            ).forEachIndexed { i, (icon, name, status) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(icon, null, tint = AuroraAccent, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(14.dp))
                    Text(name, style = MaterialTheme.typography.bodyLarge, color = colors.ink0, modifier = Modifier.weight(1f))
                    Text(status, style = MaterialTheme.typography.bodySmall,
                        color = if (status == "Connected" || status == "On") AuroraAccent else colors.ink3)
                }
                if (i < 2) HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun SleepArcCanvas(modifier: Modifier = Modifier) {
    val inf = rememberInfiniteTransition(label = "arc")
    val animAlpha by inf.animateFloat(
        0.7f, 1f,
        infiniteRepeatable(tween(2000, easing = EaseInOut), RepeatMode.Reverse),
        label = "arc_alpha",
    )
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(0f, h)
            cubicTo(w * 0.2f, h * 0.3f, w * 0.4f, 0f, w * 0.5f, h * 0.1f)
            cubicTo(w * 0.6f, h * 0.2f, w * 0.8f, h * 0.05f, w, h * 0.3f)
        }
        drawPath(
            path = path,
            brush = Brush.horizontalGradient(
                listOf(
                    LilacAccent.copy(alpha = animAlpha),
                    AuroraAccent.copy(alpha = animAlpha),
                )
            ),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun SleepCyclesGraph(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.lumen
    // Simulated sleep-stage sequence (0=Deep, 1=Light, 2=REM) across the night
    val sleepStages = remember {
        listOf(1,1,0,0,0,0,2,1,1,0,0,0,2,2,1,1,0,0,2,2,1,1,1,2,2,1,1,2,2,1,2,1)
    }
    val stageColors = listOf(AuroraAccent, IndigoAccent, LilacAccent)
    // Normalized bar heights: Deep tallest (fills full height), Light medium, REM short
    val stageHeights = listOf(1.0f, 0.55f, 0.28f)

    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("REM" to LilacAccent, "Light" to IndigoAccent, "Deep" to AuroraAccent).forEach { (label, color) ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(Modifier.size(8.dp).clip(androidx.compose.foundation.shape.CircleShape).background(color))
                    Text(label, style = MaterialTheme.typography.bodySmall, color = colors.ink2)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            sleepStages.forEach { stage ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(stageHeights[stage])
                        .clip(ShapePill)
                        .background(stageColors[stage].copy(alpha = 0.72f))
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("10:42 PM", style = MaterialTheme.typography.bodySmall, color = colors.ink3)
            Text("6:18 AM", style = MaterialTheme.typography.bodySmall, color = AuroraAccent)
        }
    }
}
