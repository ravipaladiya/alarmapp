package com.lumen.alarm.ui.screens.mood

import androidx.compose.animation.animateColorAsState
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

@Composable
fun MoodScreen(onBack: () -> Unit) {
    val colors = MaterialTheme.lumen
    var selectedMood by remember { mutableIntStateOf(1) }

    val moods = listOf(
        Triple("Calm", Icons.Default.Spa, LilacAccent),
        Triple("Energetic", Icons.Default.FlashOn, RoseAccent),
        Triple("Focused", Icons.Default.CenterFocusStrong, IndigoAccent),
        Triple("Dreamy", Icons.Default.Bedtime, AuroraAccent),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgApp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        LumenTopBar(title = "How do you want to wake?", onBack = onBack)

        Spacer(Modifier.height(8.dp))

        // Mood grid
        Column(
            modifier = Modifier.padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            moods.chunked(2).forEach { rowMoods ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowMoods.forEachIndexed { colIdx, (name, icon, color) ->
                        val globalIdx = moods.indexOf(moods.find { it.first == name })
                        val isSelected = selectedMood == globalIdx
                        val bgColor by animateColorAsState(
                            if (isSelected) color.copy(alpha = 0.2f) else colors.bgCard,
                            label = "mood_bg",
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(ShapeLarge)
                                .background(bgColor)
                                .border(
                                    if (isSelected) 1.dp else 0.dp,
                                    color.copy(alpha = 0.5f),
                                    ShapeLarge,
                                )
                                .clickable { selectedMood = globalIdx }
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(color.copy(alpha = if (isSelected) 0.3f else 0.15f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(icon, null, tint = color, modifier = Modifier.size(26.dp))
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(
                                name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) color else colors.ink0,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            )
                            Spacer(Modifier.height(4.dp))
                            val descriptions = listOf("Soft tones, slow rise", "Loud beats, bright", "Voice agenda, minimal", "Ambient, gradual")
                            Text(
                                descriptions.getOrElse(globalIdx) { "" },
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.ink2,
                            )
                            if (isSelected) {
                                Spacer(Modifier.height(8.dp))
                                Icon(Icons.Default.Check, null, tint = color, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        SectionLabel("Auto-suggest from", modifier = Modifier.padding(horizontal = 22.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(ShapeLarge)
                .background(colors.bgCard),
        ) {
            listOf(
                Triple("Yesterday's mood log", true, IndigoAccent),
                Triple("Calendar density", true, AuroraAccent),
                Triple("Sleep quality", false, LilacAccent),
            ).forEachIndexed { i, (label, checked, color) ->
                var isChecked by remember { mutableStateOf(checked) }
                SettingsRow(
                    label = label,
                    leadingIcon = Icons.Default.AutoAwesome,
                    leadingIconColor = color,
                    trailing = {
                        LumenToggle(checked = isChecked, onCheckedChange = { isChecked = it })
                    },
                )
                if (i < 2) HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}
