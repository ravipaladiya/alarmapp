package com.lumen.alarm.ui.screens.ringing

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lumen.alarm.ui.components.LumenPill
import com.lumen.alarm.ui.theme.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.absoluteValue
import kotlinx.coroutines.delay

@Composable
fun AlarmRingingScreen(
    alarmId: Long,
    alarmLabel: String,
    onDismiss: () -> Unit,
    onSnooze: (Int) -> Unit,
) {
    val inf = rememberInfiniteTransition(label = "ring_anim")
    val ring1Scale by inf.animateFloat(
        1f, 1.4f,
        infiniteRepeatable(tween(2400, easing = EaseInOut), RepeatMode.Reverse),
        label = "ring1",
    )
    val ring2Scale by inf.animateFloat(
        1f, 1.7f,
        infiniteRepeatable(tween(2400, 300, easing = EaseInOut), RepeatMode.Reverse),
        label = "ring2",
    )
    val ring3Scale by inf.animateFloat(
        1f, 2f,
        infiniteRepeatable(tween(2400, 600, easing = EaseInOut), RepeatMode.Reverse),
        label = "ring3",
    )
    val bellFloat by inf.animateFloat(
        -4f, 4f,
        infiniteRepeatable(tween(600, easing = EaseInOut), RepeatMode.Reverse),
        label = "bell_float",
    )

    var now by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalTime.now()
            delay(1000L)
        }
    }
    val timeText = now.format(DateTimeFormatter.ofPattern("hh:mm"))
    val periodText = now.format(DateTimeFormatter.ofPattern("a"))
    val dateText = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault()))

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val leftNudge by inf.animateFloat(
        0f, -6f,
        infiniteRepeatable(tween(700, easing = EaseInOut), RepeatMode.Reverse),
        label = "nudge_l",
    )
    val rightNudge by inf.animateFloat(
        0f, 6f,
        infiniteRepeatable(tween(700, easing = EaseInOut), RepeatMode.Reverse),
        label = "nudge_r",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(
                        RoseAccent.copy(alpha = 0.18f),
                        IndigoAccent.copy(alpha = 0.10f),
                        BgDeepest,
                    ),
                    radius = 1200f,
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(0.8f))

            // Date and label
            Text(dateText, style = MaterialTheme.typography.bodyMedium, color = Ink2)
            Spacer(Modifier.height(4.dp))

            // Large time
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.displayLarge,
                    color = Ink0,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    periodText,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Ink1,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            if (alarmLabel.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                LumenPill(
                    text = alarmLabel,
                    color = RoseAccent,
                    showLive = true,
                )
            }

            Spacer(Modifier.weight(1f))

            // Pulsing rings + bell
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center,
            ) {
                // Ring 3
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(ring3Scale)
                        .clip(CircleShape)
                        .border(1.dp, RoseAccent.copy(alpha = 0.15f), CircleShape)
                )
                // Ring 2
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(ring2Scale)
                        .clip(CircleShape)
                        .border(1.5.dp, RoseAccent.copy(alpha = 0.25f), CircleShape)
                )
                // Ring 1
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(ring1Scale)
                        .clip(CircleShape)
                        .border(2.dp, RoseAccent.copy(alpha = 0.4f), CircleShape)
                )
                // Bell icon
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(listOf(RoseAccent.copy(alpha = 0.3f), Color.Transparent))
                        )
                        .graphicsLayer { translationY = bellFloat },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Alarm, null, tint = RoseAccent, modifier = Modifier.size(32.dp))
                }
            }

            Spacer(Modifier.weight(1f))

            // Slide actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ShapePill)
                    .background(BgCard.copy(alpha = 0.8f))
                    .border(1.dp, LineMedium, ShapePill)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (dragOffset < -100) onDismiss()
                                else if (dragOffset > 100) onSnooze(5)
                                dragOffset = 0f
                            },
                            onDragCancel = { dragOffset = 0f },
                            onHorizontalDrag = { _, delta -> dragOffset += delta }
                        )
                    }
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Dismiss — left drag (dragOffset < -100) triggers dismiss
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .graphicsLayer { translationX = leftNudge },
                ) {
                    Icon(Icons.Default.ChevronLeft, null, tint = RoseAccent, modifier = Modifier.size(20.dp))
                    Text("Dismiss", style = MaterialTheme.typography.bodyMedium, color = Ink1)
                }

                Box(
                    modifier = Modifier
                        .size(1.dp, 24.dp)
                        .background(LineMedium)
                )

                // Snooze — right drag (dragOffset > 100) triggers snooze
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable { onSnooze(5) }
                        .graphicsLayer { translationX = rightNudge },
                ) {
                    Text("Snooze 5m", style = MaterialTheme.typography.bodyMedium, color = Ink1)
                    Icon(Icons.Default.ChevronRight, null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                "Swipe left to dismiss · right to snooze",
                style = MaterialTheme.typography.bodySmall,
                color = Ink3,
                textAlign = TextAlign.Center,
            )
        }
    }
}
