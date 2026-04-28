package com.lumen.alarm.ui.screens.planner

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lumen.alarm.ui.components.*
import com.lumen.alarm.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class PlannerTask(
    val time: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
)

@Composable
fun PlannerScreen(onBack: () -> Unit) {
    val colors = MaterialTheme.lumen
    val today = LocalDate.now()
    val dateText = today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault()))

    val tasks = listOf(
        PlannerTask("6:30", "Cold shower", "4 min", Icons.Default.Shower, AuroraAccent),
        PlannerTask("6:45", "Journal", "2 prompts", Icons.Default.Edit, LilacAccent),
        PlannerTask("7:00", "Strength", "push day", Icons.Default.FitnessCenter, RoseAccent),
        PlannerTask("8:30", "Standup", "zoom", Icons.Default.VideoCall, IndigoAccent),
        PlannerTask("10:00", "Deep work block", "3h", Icons.Default.CenterFocusStrong, GoldAccent),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgApp)
            .verticalScroll(rememberScrollState())
    ) {
        LumenTopBar(
            title = "Today",
            onBack = onBack,
            trailingContent = {
                LumenPill("Day ${(1..21).random()}", color = IndigoAccent, small = true)
            },
        )

        // Weather card
        GlowCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            glowColor = GoldAccent,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WbSunny, null, tint = GoldAccent, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Clear · 16°→24°", style = MaterialTheme.typography.bodyLarge, color = colors.ink0)
                    Text(dateText, style = MaterialTheme.typography.bodySmall, color = colors.ink2)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Default.LightMode, null, tint = GoldAccent, modifier = Modifier.size(12.dp))
                        Text("6:12 AM", style = MaterialTheme.typography.bodySmall, color = colors.ink2)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Default.Brightness3, null, tint = LilacAccent, modifier = Modifier.size(12.dp))
                        Text("7:48 PM", style = MaterialTheme.typography.bodySmall, color = colors.ink2)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        SectionLabel("This morning", modifier = Modifier.padding(horizontal = 22.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(ShapeLarge)
                .background(colors.bgCard),
        ) {
            tasks.forEachIndexed { i, task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Time
                    Text(
                        task.time,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.ink2,
                        modifier = Modifier.width(36.dp),
                    )
                    // Timeline dot + line
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(20.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(task.color)
                        )
                        if (i < tasks.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(colors.lineSubtle)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(task.title, style = MaterialTheme.typography.bodyLarge, color = colors.ink0, fontWeight = FontWeight.Medium)
                        Text(task.subtitle, style = MaterialTheme.typography.bodySmall, color = colors.ink2)
                    }
                    Icon(task.icon, null, tint = task.color, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Smart insight
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(ShapeLarge)
                .background(IndigoAccent.copy(alpha = 0.1f))
                .border(1.dp, IndigoAccent.copy(alpha = 0.2f), ShapeLarge)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Default.AutoAwesome, null, tint = IndigoAccent, modifier = Modifier.size(16.dp))
            Text(
                "3 tasks before standup — you've got time",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.ink1,
            )
        }

        Spacer(Modifier.height(80.dp))
    }
}
