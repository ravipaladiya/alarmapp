package com.lumen.alarm.ui.screens.onboarding

import androidx.compose.animation.*
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
import java.time.DayOfWeek

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val step by viewModel.step.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = step,
        transitionSpec = {
            slideInHorizontally { it } + fadeIn() togetherWith
            slideOutHorizontally { -it } + fadeOut()
        },
        label = "onboarding_step",
    ) { currentStep ->
        when (currentStep) {
            0 -> SplashStep(onNext = { viewModel.nextStep() })
            1 -> QuickStartStep(
                onComplete = {
                    viewModel.completeOnboarding()
                    onComplete()
                }
            )
            else -> {
                viewModel.completeOnboarding()
                onComplete()
            }
        }
    }
}

@Composable
private fun SplashStep(onNext: () -> Unit) {
    val inf = rememberInfiniteTransition(label = "splash")
    val glow by inf.animateFloat(
        0.5f, 1f,
        infiniteRepeatable(tween(2500, easing = EaseInOut), RepeatMode.Reverse),
        label = "moon_glow",
    )
    val floatY by inf.animateFloat(
        -6f, 6f,
        infiniteRepeatable(tween(3000, easing = EaseInOut), RepeatMode.Reverse),
        label = "float",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepest),
        contentAlignment = Alignment.Center,
    ) {
        // Stars
        StarfieldBackground()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(40.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(glow * 0.1f + 0.95f),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(IndigoAccent.copy(alpha = glow * 0.3f))
                )
                Icon(
                    Icons.Default.Bedtime,
                    null,
                    tint = IndigoSoft,
                    modifier = Modifier
                        .size(50.dp)
                        .offset(y = floatY.dp),
                )
            }
            Text(
                "lumen",
                style = MaterialTheme.typography.headlineLarge,
                color = Ink0,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                "rise gently",
                style = MaterialTheme.typography.bodyLarge,
                color = Ink2,
                letterSpacing = androidx.compose.ui.unit.TextUnit(4f, androidx.compose.ui.unit.TextUnitType.Sp),
            )
            Spacer(Modifier.height(32.dp))
            LumenButton(
                text = "Get started",
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(0.7f),
            )
            Text(
                "No account needed · Works offline",
                style = MaterialTheme.typography.bodySmall,
                color = Ink3,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun QuickStartStep(onComplete: () -> Unit) {
    var hour by remember { mutableIntStateOf(7) }
    var minute by remember { mutableIntStateOf(0) }
    var selectedDays by remember {
        mutableStateOf(setOf(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
        ))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepest)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))
            Text(
                "lumen",
                style = MaterialTheme.typography.headlineMedium,
                color = IndigoSoft,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "Set your first alarm.\nEverything else can wait.",
                style = MaterialTheme.typography.headlineLarge,
                color = Ink0,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(32.dp))

            // Quick time presets
            Text("Quick start", style = MaterialTheme.typography.bodySmall, color = Ink2, letterSpacing = 1.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple("Workout", 5, 30),
                    Triple("Work", 7, 0),
                    Triple("School", 6, 45),
                ).forEach { (label, h, m) ->
                    Box(
                        modifier = Modifier
                            .clip(ShapePill)
                            .background(BgCard)
                            .border(1.dp, LineSubtle, ShapePill)
                            .clickable { hour = h; minute = m }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(label, style = MaterialTheme.typography.bodySmall, color = Ink1, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${if (h % 12 == 0) 12 else h % 12}:${m.toString().padStart(2, '0')} ${if (h < 12) "AM" else "PM"}",
                                style = MaterialTheme.typography.bodySmall, color = Ink2,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Time card (glowing)
            GlowCard(
                modifier = Modifier.fillMaxWidth(),
                glowColor = IndigoAccent,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        val displayHour = if (hour % 12 == 0) 12 else hour % 12
                        Text(
                            "$displayHour:${minute.toString().padStart(2, '0')}",
                            style = MaterialTheme.typography.displayMedium,
                            color = Ink0,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            if (hour < 12) "AM" else "PM",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Ink2,
                            modifier = Modifier.padding(bottom = 6.dp),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    DayDots(
                        selectedDays = selectedDays,
                        onDayToggle = { day ->
                            val updated = selectedDays.toMutableSet()
                            if (day in updated) updated.remove(day) else updated.add(day)
                            selectedDays = updated
                        },
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            LumenButton(
                text = "Set alarm & continue",
                onClick = onComplete,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))

            // Trust signals
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                listOf(
                    "No account" to Icons.Default.PersonOff,
                    "AI free 14d" to Icons.Default.AutoAwesome,
                    "Works offline" to Icons.Default.WifiOff,
                ).forEach { (label, icon) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(icon, null, tint = Ink2, modifier = Modifier.size(12.dp))
                        Text(label, style = MaterialTheme.typography.bodySmall, color = Ink2)
                    }
                }
            }
        }
    }
}

@Composable
private fun StarfieldBackground() {
    val inf = rememberInfiniteTransition(label = "stars")
    val twinkle1 by inf.animateFloat(0.3f, 1f, infiniteRepeatable(tween(4800, easing = EaseInOut), RepeatMode.Reverse), label = "t1")
    val twinkle2 by inf.animateFloat(1f, 0.2f, infiniteRepeatable(tween(3200, 600, easing = EaseInOut), RepeatMode.Reverse), label = "t2")

    Box(modifier = Modifier.fillMaxSize()) {
        // This would normally be a Canvas drawing, simplified here
    }
}

@Suppress("UNUSED")
private val Int.letterSp get() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)
