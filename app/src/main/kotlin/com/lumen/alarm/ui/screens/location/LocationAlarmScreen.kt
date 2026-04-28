package com.lumen.alarm.ui.screens.location

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lumen.alarm.ui.components.*
import com.lumen.alarm.ui.theme.*

@Composable
fun LocationAlarmScreen(onBack: () -> Unit) {
    val colors = MaterialTheme.lumen
    var triggerOnLeave by remember { mutableStateOf(true) }
    var radius by remember { mutableIntStateOf(200) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgApp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        LumenTopBar(
            title = "Location alarm",
            onBack = onBack,
            trailingContent = {
                LumenButton("Save", onClick = onBack, variant = LumenButtonVariant.PRIMARY)
            },
        )

        // Map placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .height(220.dp)
                .clip(ShapeLarge)
                .background(colors.bgCard),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Map, null, tint = IndigoAccent, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(8.dp))
                Text("Tap to select location on map", style = MaterialTheme.typography.bodyMedium, color = colors.ink2)
                Spacer(Modifier.height(4.dp))
                Text("200m geofence radius", style = MaterialTheme.typography.bodySmall, color = IndigoAccent)
            }
            // Dashed geofence circle indicator
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .border(2.dp, AuroraAccent.copy(alpha = 0.5f), androidx.compose.foundation.shape.CircleShape),
            )
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(ShapeLarge)
                .background(colors.bgCard),
        ) {
            // Trigger
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
                        .background(IndigoAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = IndigoAccent, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Trigger", style = MaterialTheme.typography.bodyLarge, color = colors.ink0)
                }
                Row(
                    modifier = Modifier
                        .clip(ShapePill)
                        .background(colors.bgHover)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    listOf("Leave" to true, "Arrive" to false).forEach { (label, leave) ->
                        Box(
                            modifier = Modifier
                                .clip(ShapePill)
                                .background(if (triggerOnLeave == leave) IndigoAccent else Color.Transparent)
                                .clickable { triggerOnLeave = leave }
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        ) {
                            Text(
                                label,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (triggerOnLeave == leave) Color.White else colors.ink2,
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))

            // Place name
            SettingsRow(
                label = "Place",
                value = "350 5th Ave, NYC",
                leadingIcon = Icons.Default.Search,
                leadingIconColor = IndigoAccent,
                onClick = { },
            )

            HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))

            // Radius
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
                        .background(AuroraAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.RadioButtonChecked, null, tint = AuroraAccent, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Radius", style = MaterialTheme.typography.bodyLarge, color = colors.ink0)
                    Slider(
                        value = radius.toFloat(),
                        onValueChange = { radius = it.toInt() },
                        valueRange = 50f..1000f,
                        colors = SliderDefaults.colors(thumbColor = AuroraAccent, activeTrackColor = AuroraAccent),
                    )
                }
                Text("${radius}m", style = MaterialTheme.typography.bodyMedium, color = AuroraAccent, fontWeight = FontWeight.SemiBold)
            }

            HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))

            // Time window
            SettingsRow(
                label = "Active window",
                value = "17:00 → 20:00",
                leadingIcon = Icons.Default.Schedule,
                leadingIconColor = GoldAccent,
                onClick = { },
            )
        }

        Spacer(Modifier.height(16.dp))

        // Permission info pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(ShapePill)
                .background(AuroraAccent.copy(alpha = 0.1f))
                .border(1.dp, AuroraAccent.copy(alpha = 0.3f), ShapePill)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Default.Info, null, tint = AuroraAccent, modifier = Modifier.size(16.dp))
            Text(
                "Background location required for geofence alarms",
                style = MaterialTheme.typography.bodySmall,
                color = AuroraAccent,
            )
        }

        Spacer(Modifier.height(80.dp))
    }
}
