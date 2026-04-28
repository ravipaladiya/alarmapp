package com.lumen.alarm.ui.screens.premium

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lumen.alarm.ui.components.*
import com.lumen.alarm.ui.theme.*

@Composable
fun PremiumScreen(onBack: () -> Unit) {
    val colors = MaterialTheme.lumen
    var selectedPlan by remember { mutableIntStateOf(1) } // 0=monthly, 1=yearly, 2=lifetime

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgApp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        // Header
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
            Spacer(Modifier.weight(1f))
            LumenPill("LUMEN PREMIUM", color = GoldAccent)
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.size(40.dp))
        }

        Spacer(Modifier.height(12.dp))

        // Hero headline
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(GoldAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.WorkspacePremium, null, tint = GoldAccent, modifier = Modifier.size(36.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Unlock the full ritual.",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = colors.ink0,
            )
        }

        Spacer(Modifier.height(24.dp))

        // Features
        val features = listOf(
            Triple(Icons.Default.AutoAwesome, AuroraAccent, "AI Smart Wake-Up" to "Optimal sleep cycle timing"),
            Triple(Icons.Default.Science, IndigoAccent, "All challenge modes" to "Math, QR, shake, memory & more"),
            Triple(Icons.Default.LocalFireDepartment, GoldAccent, "Goals & streak rewards" to "Coins, themes, milestone badges"),
            Triple(Icons.Default.Shield, RoseAccent, "No ads, ever" to "Clean, distraction-free experience"),
            Triple(Icons.Default.LocationOn, AuroraAccent, "Location alarms" to "Trigger by geofence"),
            Triple(Icons.Default.Mic, LilacAccent, "Voice commands" to "Hands-free alarm control"),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(ShapeLarge)
                .background(colors.bgCard),
        ) {
            features.forEachIndexed { i, (icon, color, textPair) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(ShapeSmall)
                            .background(color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(textPair.first, style = MaterialTheme.typography.bodyLarge, color = colors.ink0, fontWeight = FontWeight.SemiBold)
                        Text(textPair.second, style = MaterialTheme.typography.bodySmall, color = colors.ink2)
                    }
                    Icon(Icons.Default.Check, null, tint = AuroraAccent, modifier = Modifier.size(16.dp))
                }
                if (i < features.size - 1) {
                    HorizontalDivider(color = colors.lineSubtle, modifier = Modifier.padding(horizontal = 18.dp))
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        SectionLabel("Choose your plan", modifier = Modifier.padding(horizontal = 22.dp))
        Spacer(Modifier.height(8.dp))

        // Pricing tiers
        val plans = listOf(
            Triple(0, "Monthly", "₹149/mo"),
            Triple(1, "Yearly", "₹999/yr · save 44%"),
            Triple(2, "Lifetime", "₹2,499 one-time"),
        )
        Column(
            modifier = Modifier.padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            plans.forEach { (index, name, price) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ShapeLarge)
                        .background(
                            if (selectedPlan == index) GoldAccent.copy(alpha = 0.12f)
                            else colors.bgCard
                        )
                        .border(
                            1.dp,
                            if (selectedPlan == index) GoldAccent.copy(alpha = 0.5f) else colors.lineSubtle,
                            ShapeLarge,
                        )
                        .clickable { selectedPlan = index }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selectedPlan == index,
                        onClick = { selectedPlan = index },
                        colors = RadioButtonDefaults.colors(selectedColor = GoldAccent),
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, style = MaterialTheme.typography.bodyLarge, color = colors.ink0, fontWeight = FontWeight.SemiBold)
                        Text(price, style = MaterialTheme.typography.bodySmall, color = colors.ink2)
                    }
                    if (index == 1) {
                        LumenPill("Best value", color = GoldAccent, small = true)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        LumenButton(
            text = "Start 7-day free trial",
            onClick = { /* handle purchase */ },
            variant = LumenButtonVariant.GOLD,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
        )

        Spacer(Modifier.height(12.dp))
        Text(
            "Cancel anytime · No charges during trial",
            style = MaterialTheme.typography.bodySmall,
            color = colors.ink3,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(80.dp))
    }
}
