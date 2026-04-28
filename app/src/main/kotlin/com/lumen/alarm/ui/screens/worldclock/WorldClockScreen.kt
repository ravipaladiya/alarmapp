package com.lumen.alarm.ui.screens.worldclock

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lumen.alarm.ui.components.*
import com.lumen.alarm.ui.theme.*
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class CityClockItem(
    val city: String,
    val timezone: String,
    val hasAlarm: Boolean = false,
)

@Composable
fun WorldClockScreen(onBack: () -> Unit) {
    val colors = MaterialTheme.lumen
    val cities = listOf(
        CityClockItem("Mumbai", "Asia/Kolkata"),
        CityClockItem("San Francisco", "America/Los_Angeles", hasAlarm = true),
        CityClockItem("London", "Europe/London"),
        CityClockItem("Tokyo", "Asia/Tokyo"),
        CityClockItem("Sydney", "Australia/Sydney"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgApp)
    ) {
        LumenTopBar(
            title = "World clock",
            onBack = onBack,
            trailingContent = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Add, null, tint = IndigoAccent, modifier = Modifier.size(22.dp))
                }
            },
        )

        // World strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .height(40.dp)
                .clip(ShapeMedium)
                .background(colors.bgCard),
            contentAlignment = Alignment.Center,
        ) {
            Text("◀ timezone strip ▶", style = MaterialTheme.typography.bodySmall, color = colors.ink3)
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(cities) { city ->
                CityClockRow(
                    city = city,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                )
            }
        }
    }
}

@Composable
private fun CityClockRow(city: CityClockItem, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.lumen
    val now = ZonedDateTime.now(ZoneId.of(city.timezone))
    val timeText = now.format(DateTimeFormatter.ofPattern("hh:mm a"))
    val isNight = now.hour < 6 || now.hour >= 20

    Row(
        modifier = modifier
            .clip(ShapeLarge)
            .background(colors.bgCard)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            if (isNight) Icons.Default.Bedtime else Icons.Default.LightMode,
            null,
            tint = if (isNight) LilacAccent else GoldAccent,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(city.city, style = MaterialTheme.typography.bodyLarge, color = colors.ink0, fontWeight = FontWeight.SemiBold)
            Text(city.timezone.substringAfter("/").replace("_", " "),
                style = MaterialTheme.typography.bodySmall, color = colors.ink2)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(timeText, style = MaterialTheme.typography.titleLarge, color = colors.ink0, fontWeight = FontWeight.Bold)
            if (city.hasAlarm) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.Alarm, null, tint = IndigoAccent, modifier = Modifier.size(10.dp))
                    Text("alarm set", style = MaterialTheme.typography.bodySmall, color = IndigoAccent)
                }
            }
        }
    }
}
