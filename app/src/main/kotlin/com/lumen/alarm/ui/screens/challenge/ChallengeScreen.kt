package com.lumen.alarm.ui.screens.challenge

import androidx.compose.animation.animateColorAsState
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
import com.lumen.alarm.domain.model.ChallengeType
import com.lumen.alarm.ui.components.*
import com.lumen.alarm.ui.theme.*
import kotlin.random.Random

@Composable
fun ChallengeScreen(onBack: () -> Unit) {
    val colors = MaterialTheme.lumen
    var selectedChallenge by remember { mutableStateOf(ChallengeType.MATH) }
    var mathAnswer by remember { mutableStateOf("") }

    // Generate math puzzle
    val a = remember { Random.nextInt(10, 20) }
    val b = remember { Random.nextInt(5, 15) }
    val c = remember { Random.nextInt(2, 10) }
    val answer = remember { a * b - c }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgApp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        LumenTopBar(
            title = "Challenge dismiss",
            onBack = onBack,
            trailingContent = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Info, null, tint = colors.ink2, modifier = Modifier.size(20.dp))
                }
            },
        )

        // Challenge type list
        SectionLabel("Challenge type", modifier = Modifier.padding(horizontal = 22.dp))

        val challengeOptions = listOf(
            Triple(ChallengeType.MATH, Icons.Default.Calculate, IndigoAccent),
            Triple(ChallengeType.SHAKE, Icons.Default.Vibration, RoseAccent),
            Triple(ChallengeType.QR_SCAN, Icons.Default.QrCode, AuroraAccent),
            Triple(ChallengeType.MEMORY, Icons.Default.Psychology, LilacAccent),
            Triple(ChallengeType.TYPE_PHRASE, Icons.Default.Keyboard, GoldAccent),
            Triple(ChallengeType.PHOTO, Icons.Default.CameraAlt, IndigoAccent),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(ShapeLarge)
                .background(colors.bgCard),
        ) {
            challengeOptions.forEachIndexed { i, (type, icon, color) ->
                val isSelected = selectedChallenge == type
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isSelected) color.copy(alpha = 0.08f) else Color.Transparent)
                        .clickable { selectedChallenge = type }
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(ShapeSmall)
                            .background(color.copy(alpha = if (isSelected) 0.2f else 0.1f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(icon, null, tint = if (isSelected) color else colors.ink2, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Text(
                        type.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected) color else colors.ink0,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.weight(1f),
                    )
                    if (isSelected) {
                        Icon(Icons.Default.Check, null, tint = color, modifier = Modifier.size(18.dp))
                    }
                }
                if (i < challengeOptions.size - 1) {
                    HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))
                }
            }
        }

        // Preview card
        if (selectedChallenge == ChallengeType.MATH) {
            Spacer(Modifier.height(20.dp))
            SectionLabel("Preview", modifier = Modifier.padding(horizontal = 22.dp))

            GlowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                glowColor = IndigoAccent,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "$a × $b − $c = ?",
                        style = MaterialTheme.typography.headlineLarge,
                        color = colors.ink0,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(16.dp))
                    // Keypad
                    MathKeypad(
                        currentInput = mathAnswer,
                        onDigit = { d ->
                            if (mathAnswer.length < 4) mathAnswer += d
                        },
                        onBackspace = {
                            if (mathAnswer.isNotEmpty()) mathAnswer = mathAnswer.dropLast(1)
                        },
                        onOk = {
                            if (mathAnswer.toIntOrNull() == answer) mathAnswer = "✓"
                        },
                    )
                }
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun MathKeypad(
    currentInput: String,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onOk: () -> Unit,
) {
    val colors = MaterialTheme.lumen
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("⌫", "0", "OK"),
    )

    // Display
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShapeMedium)
            .background(colors.bgHover)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            currentInput.ifBlank { "..." },
            style = MaterialTheme.typography.headlineLarge,
            color = if (currentInput.isBlank()) colors.ink3 else colors.ink0,
        )
    }
    Spacer(Modifier.height(12.dp))

    keys.forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            row.forEach { key ->
                val isOk = key == "OK"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(ShapeMedium)
                        .background(if (isOk) AuroraAccent else colors.bgHover)
                        .clickable {
                            when (key) {
                                "⌫" -> onBackspace()
                                "OK" -> onOk()
                                else -> onDigit(key)
                            }
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        key,
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (isOk) BgDeepest else colors.ink0,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}
