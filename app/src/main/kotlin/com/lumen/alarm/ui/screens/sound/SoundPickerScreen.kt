package com.lumen.alarm.ui.screens.sound

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

data class SoundItem(
    val name: String,
    val category: String,
    val duration: String,
    val uri: String = "",
)

@Composable
fun SoundPickerScreen(onBack: () -> Unit) {
    val colors = MaterialTheme.lumen
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedSound by remember { mutableStateOf("Default") }
    var isPlaying by remember { mutableStateOf<String?>(null) }
    var volume by remember { mutableFloatStateOf(0.6f) }

    val tabs = listOf("Library", "Nature", "Classic", "Files")
    val sounds = mapOf(
        "Library" to listOf(
            SoundItem("Default", "Default", "0:30"),
            SoundItem("Gentle Rise", "Calm", "1:00"),
            SoundItem("Morning Glow", "Calm", "0:45"),
            SoundItem("Focus Bell", "Calm", "0:20"),
        ),
        "Nature" to listOf(
            SoundItem("Rain Forest", "Nature", "2:00"),
            SoundItem("Ocean Waves", "Nature", "1:30"),
            SoundItem("Bird Song", "Nature", "1:00"),
            SoundItem("Thunder Roll", "Nature", "0:45"),
        ),
        "Classic" to listOf(
            SoundItem("Classic Bell", "Classic", "0:15"),
            SoundItem("Trumpet Rise", "Classic", "0:20"),
            SoundItem("Piano Dawn", "Classic", "1:00"),
        ),
        "Files" to listOf(
            SoundItem("My custom tone", "Custom", "0:45"),
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgApp)
    ) {
        LumenTopBar(title = "Wake sound", onBack = onBack)

        // Volume bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Default.VolumeDown, null, tint = colors.ink2, modifier = Modifier.size(18.dp))
            Slider(
                value = volume,
                onValueChange = { volume = it },
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = IndigoAccent,
                    activeTrackColor = IndigoAccent,
                    inactiveTrackColor = colors.bgCard,
                ),
            )
            Icon(Icons.Default.VolumeUp, null, tint = colors.ink2, modifier = Modifier.size(18.dp))
            Text("${(volume * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = colors.ink2)
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = colors.bgApp,
            contentColor = IndigoAccent,
            edgePadding = 18.dp,
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            tab,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (selectedTab == index) IndigoAccent else colors.ink2,
                        )
                    }
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            items(sounds[tabs[selectedTab]] ?: emptyList()) { sound ->
                SoundRow(
                    sound = sound,
                    isSelected = sound.name == selectedSound,
                    isPlaying = isPlaying == sound.name,
                    onSelect = { selectedSound = sound.name },
                    onTogglePlay = {
                        isPlaying = if (isPlaying == sound.name) null else sound.name
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 4.dp),
                )
            }

            if (selectedTab == 3) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                            .clip(ShapeLarge)
                            .background(colors.bgCard)
                            .clickable { /* file picker */ }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(Icons.Default.FolderOpen, null, tint = IndigoAccent)
                        Text("Choose from device", style = MaterialTheme.typography.bodyLarge, color = colors.ink0)
                    }
                }
            }
        }
    }
}

@Composable
private fun SoundRow(
    sound: SoundItem,
    isSelected: Boolean,
    isPlaying: Boolean,
    onSelect: () -> Unit,
    onTogglePlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.lumen
    Row(
        modifier = modifier
            .clip(ShapeLarge)
            .background(if (isSelected) IndigoAccent.copy(alpha = 0.1f) else colors.bgCard)
            .border(
                if (isSelected) 1.dp else 0.dp,
                IndigoAccent.copy(alpha = 0.3f),
                ShapeLarge,
            )
            .clickable(onClick = onSelect)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onTogglePlay, modifier = Modifier.size(36.dp)) {
            Icon(
                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                null,
                tint = if (isSelected) IndigoAccent else colors.ink2,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                sound.name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) IndigoAccent else colors.ink0,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(sound.category, style = MaterialTheme.typography.bodySmall, color = colors.ink2)
                Text("·", style = MaterialTheme.typography.bodySmall, color = colors.ink3)
                Text(sound.duration, style = MaterialTheme.typography.bodySmall, color = colors.ink2)
            }
        }
        if (isSelected) {
            Icon(Icons.Default.Check, null, tint = IndigoAccent, modifier = Modifier.size(18.dp))
        }
    }
}
