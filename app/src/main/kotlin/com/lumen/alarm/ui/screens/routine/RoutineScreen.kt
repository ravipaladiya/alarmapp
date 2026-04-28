package com.lumen.alarm.ui.screens.routine

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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

data class RoutineAction(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    var enabled: Boolean,
)

@Composable
fun RoutineScreen(onBack: () -> Unit) {
    val colors = MaterialTheme.lumen
    var actions by remember {
        mutableStateOf(listOf(
            RoutineAction("Turn off Do Not Disturb", Icons.Default.NotificationsActive, IndigoAccent, true),
            RoutineAction("Play \"Morning\" playlist", Icons.Default.MusicNote, AuroraAccent, true),
            RoutineAction("Read weather aloud", Icons.Default.WbSunny, GoldAccent, true),
            RoutineAction("Open Calendar", Icons.Default.CalendarToday, IndigoAccent, false),
            RoutineAction("Start coffee maker", Icons.Default.Coffee, RoseAccent, false),
        ))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgApp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        LumenTopBar(
            title = "Routine",
            onBack = onBack,
            trailingContent = {
                LumenButton("Test", onClick = {}, variant = LumenButtonVariant.GHOST)
            },
        )

        // When card
        GlowCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            glowColor = IndigoAccent,
        ) {
            Column {
                LumenPill("WHEN", color = IndigoAccent, small = true)
                Spacer(Modifier.height(8.dp))
                Text("Morning ritual dismissed", style = MaterialTheme.typography.headlineSmall,
                    color = colors.ink0, fontWeight = FontWeight.Bold)
                Text("6:24 AM · Mon–Fri", style = MaterialTheme.typography.bodySmall, color = colors.ink2)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Arrow
        Box(modifier = Modifier.padding(start = 36.dp)) {
            Icon(Icons.Default.KeyboardArrowDown, null, tint = colors.ink3, modifier = Modifier.size(20.dp))
        }

        // Then do
        SectionLabel("Then do", modifier = Modifier.padding(horizontal = 22.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(ShapeLarge)
                .background(colors.bgCard),
        ) {
            actions.forEachIndexed { i, action ->
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
                            .background(action.color.copy(alpha = if (action.enabled) 0.2f else 0.08f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(action.icon, null,
                            tint = if (action.enabled) action.color else colors.ink3,
                            modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Text(
                        action.label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (action.enabled) colors.ink0 else colors.ink3,
                        modifier = Modifier.weight(1f),
                    )
                    LumenToggle(
                        checked = action.enabled,
                        onCheckedChange = { on ->
                            actions = actions.toMutableList().also { it[i] = action.copy(enabled = on) }
                        },
                    )
                }
                if (i < actions.size - 1) {
                    HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))
                }
            }

            // Add action
            HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(ShapeSmall)
                        .border(1.dp, colors.lineMedium, ShapeSmall),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Add, null, tint = colors.ink2, modifier = Modifier.size(16.dp))
                }
                Text("Add action", style = MaterialTheme.typography.bodyLarge, color = colors.ink2)
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}
