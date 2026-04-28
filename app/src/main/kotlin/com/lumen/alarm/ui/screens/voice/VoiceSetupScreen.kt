package com.lumen.alarm.ui.screens.voice

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
import com.lumen.alarm.ui.components.*
import com.lumen.alarm.ui.theme.*

@Composable
fun VoiceSetupScreen(onBack: () -> Unit) {
    val colors = MaterialTheme.lumen
    var isListening by remember { mutableStateOf(false) }
    var transcription by remember { mutableStateOf("") }

    val inf = rememberInfiniteTransition(label = "mic")
    val ring1 by inf.animateFloat(
        1f, 1.3f,
        infiniteRepeatable(tween(1200, easing = EaseInOut), RepeatMode.Reverse),
        label = "r1",
    )
    val ring2 by inf.animateFloat(
        1f, 1.6f,
        infiniteRepeatable(tween(1200, 300, easing = EaseInOut), RepeatMode.Reverse),
        label = "r2",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgApp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, "Close", tint = colors.ink1)
            }
            Text(
                "Voice setup",
                style = MaterialTheme.typography.headlineSmall,
                color = colors.ink0,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
            )
            IconButton(onClick = {}) {
                Icon(Icons.Default.Info, null, tint = colors.ink2, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(Modifier.height(40.dp))

        // Mic animation
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (isListening) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(ring2)
                        .clip(CircleShape)
                        .border(1.dp, LilacAccent.copy(alpha = 0.2f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(ring1)
                        .clip(CircleShape)
                        .border(1.5.dp, LilacAccent.copy(alpha = 0.35f), CircleShape)
                )
            }
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(LilacAccent.copy(alpha = 0.3f), IndigoAccent.copy(alpha = 0.2f)))
                    )
                    .clickable { isListening = !isListening },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    null,
                    tint = LilacAccent,
                    modifier = Modifier.size(32.dp),
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            if (isListening) "Listening…" else "Tap to speak",
            style = MaterialTheme.typography.bodyLarge,
            color = if (isListening) LilacAccent else colors.ink2,
        )

        if (transcription.isNotBlank() || isListening) {
            Spacer(Modifier.height(12.dp))
            Text(
                if (isListening) "\"six twenty-four\"" else transcription,
                style = MaterialTheme.typography.headlineSmall,
                color = colors.ink0,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp),
            )
        }

        Spacer(Modifier.height(40.dp))
        SectionLabel("Try saying", modifier = Modifier.padding(horizontal = 22.dp).align(Alignment.Start))
        Spacer(Modifier.height(8.dp))

        val phrases = listOf(
            "Set workout alarm for 5:30 AM",
            "Mute Friday morning",
            "How long until my alarm?",
            "Turn off tomorrow's alarm",
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            phrases.forEach { phrase ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ShapeLarge)
                        .background(colors.bgCard)
                        .clickable { transcription = phrase }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.RecordVoiceOver, null, tint = LilacAccent, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(phrase, style = MaterialTheme.typography.bodyMedium, color = colors.ink1)
                }
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}
