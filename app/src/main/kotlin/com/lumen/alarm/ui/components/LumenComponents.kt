package com.lumen.alarm.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import com.lumen.alarm.ui.theme.*
import java.time.DayOfWeek

// ── LumenButton ─────────────────────────────────────────────────────────────

enum class LumenButtonVariant { PRIMARY, GHOST, DANGER, AURORA, GOLD }

@Composable
fun LumenButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: LumenButtonVariant = LumenButtonVariant.PRIMARY,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "btn_scale",
    )

    val colors = MaterialTheme.lumen
    val (bg, fgColor) = when (variant) {
        LumenButtonVariant.PRIMARY -> Brush.horizontalGradient(
            listOf(colors.indigoSoft, IndigoAccent)
        ) to Color.White
        LumenButtonVariant.DANGER -> Brush.horizontalGradient(
            listOf(RoseAccent, Color(0xFFFF6B8A))
        ) to Color.White
        LumenButtonVariant.AURORA -> Brush.horizontalGradient(
            listOf(AuroraAccent, Color(0xFF4DBFAD))
        ) to Color.White
        LumenButtonVariant.GOLD -> Brush.horizontalGradient(
            listOf(GoldAccent, Color(0xFFE8A83C))
        ) to Color.White
        LumenButtonVariant.GHOST -> Brush.linearGradient(
            listOf(Color.Transparent, Color.Transparent)
        ) to colors.ink0
    }

    val border = if (variant == LumenButtonVariant.GHOST)
        BorderStroke(1.dp, colors.lineMedium) else null

    Box(
        modifier = modifier
            .scale(scale)
            .clip(ShapePill)
            .then(if (border != null) Modifier.border(border, ShapePill) else Modifier.background(bg, ShapePill))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, tint = fgColor, modifier = Modifier.size(18.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (!enabled) colors.ink2 else fgColor,
                letterSpacing = 0.5.sp,
            )
        }
    }
}

// ── LumenToggle ──────────────────────────────────────────────────────────────

@Composable
fun LumenToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "",
) {
    val colors = MaterialTheme.lumen
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 600f),
        label = "toggle_thumb",
    )
    val bgColor by animateColorAsState(
        targetValue = if (checked) IndigoAccent else colors.bgHover,
        animationSpec = tween(200),
        label = "toggle_bg",
    )

    Box(
        modifier = modifier
            .size(width = 44.dp, height = 24.dp)
            .clip(ShapePill)
            .background(bgColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { onCheckedChange(!checked) },
            )
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .padding(start = 2.dp + thumbOffset)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

// ── DayDots ──────────────────────────────────────────────────────────────────

@Composable
fun DayDots(
    selectedDays: Set<DayOfWeek>,
    onDayToggle: ((DayOfWeek) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.lumen
    val days = listOf(
        DayOfWeek.MONDAY to "M",
        DayOfWeek.TUESDAY to "T",
        DayOfWeek.WEDNESDAY to "W",
        DayOfWeek.THURSDAY to "T",
        DayOfWeek.FRIDAY to "F",
        DayOfWeek.SATURDAY to "S",
        DayOfWeek.SUNDAY to "S",
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        days.forEach { (day, label) ->
            val selected = day in selectedDays
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.1f else 1f,
                animationSpec = spring(dampingRatio = 0.4f, stiffness = 900f),
                label = "dot_scale_$label",
            )
            val bgColor by animateColorAsState(
                targetValue = if (selected) IndigoAccent else colors.bgHover,
                label = "dot_bg_$label",
            )
            val textColor by animateColorAsState(
                targetValue = if (selected) Color.White else colors.ink2,
                label = "dot_text_$label",
            )

            Box(
                modifier = Modifier
                    .size(26.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(bgColor)
                    .then(
                        if (onDayToggle != null) Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onDayToggle(day) },
                        ) else Modifier
                    )
                    .semantics {
                        contentDescription = "${day.name.lowercase()}, ${if (selected) "selected" else "not selected"}"
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }
    }
}

// ── LumenPill ────────────────────────────────────────────────────────────────

@Composable
fun LumenPill(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = IndigoAccent,
    showLive: Boolean = false,
    small: Boolean = false,
) {
    val inf = rememberInfiniteTransition(label = "live_pulse")
    val pulseAlpha by inf.animateFloat(
        initialValue = 1f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(1100, easing = EaseInOut), RepeatMode.Reverse),
        label = "pulse_alpha",
    )

    Row(
        modifier = modifier
            .clip(ShapePill)
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.3f), ShapePill)
            .padding(horizontal = if (small) 8.dp else 10.dp, vertical = if (small) 3.dp else 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        if (showLive) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = pulseAlpha))
            )
        }
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            letterSpacing = 0.8.sp,
        )
    }
}

// ── GlowCard ─────────────────────────────────────────────────────────────────

@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    glowColor: Color = GlowIndigo,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = MaterialTheme.lumen
    val inf = rememberInfiniteTransition(label = "glow")
    val glowAlpha by inf.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2750, easing = EaseInOut), RepeatMode.Reverse),
        label = "glow_alpha",
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 700f),
        label = "card_scale",
    )

    Column(
        modifier = modifier
            .scale(scale)
            .clip(ShapeLarge)
            .background(colors.bgCard)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(glowColor.copy(alpha = glowAlpha * 0.6f), glowColor.copy(alpha = glowAlpha * 0.2f))
                ),
                shape = ShapeLarge,
            )
            .then(
                if (onClick != null) Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ) else Modifier
            )
            .padding(18.dp),
        content = content,
    )
}

// ── SectionLabel ─────────────────────────────────────────────────────────────

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.lumen.ink2,
        modifier = modifier.padding(horizontal = 4.dp, vertical = 8.dp),
        letterSpacing = 1.6.sp,
    )
}

// ── SettingsRow ───────────────────────────────────────────────────────────────

@Composable
fun SettingsRow(
    label: String,
    modifier: Modifier = Modifier,
    value: String = "",
    onClick: (() -> Unit)? = null,
    leadingIcon: ImageVector? = null,
    leadingIconColor: Color = IndigoAccent,
    trailing: @Composable (() -> Unit)? = null,
) {
    val colors = MaterialTheme.lumen
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(ShapeMedium)
                    .background(leadingIconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(leadingIcon, contentDescription = null, tint = leadingIconColor, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(14.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge, color = colors.ink0)
            if (value.isNotBlank()) {
                Text(text = value, style = MaterialTheme.typography.bodySmall, color = colors.ink2)
            }
        }
        if (trailing != null) {
            trailing()
        }
    }
}

// ── LumenTopBar ───────────────────────────────────────────────────────────────

@Composable
fun LumenTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    trailingContent: @Composable RowScope.() -> Unit = {},
) {
    val colors = MaterialTheme.lumen
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.ink0,
                )
            }
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = colors.ink0,
            modifier = Modifier.weight(1f),
        )
        trailingContent()
    }
}

// ── LumenFab ─────────────────────────────────────────────────────────────────

@Composable
fun LumenFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Add,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 700f),
        label = "fab_scale",
    )

    Box(
        modifier = modifier
            .size(56.dp)
            .scale(scale)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(listOf(IndigoAccent, IndigoDeep))
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .semantics { contentDescription = "Add new alarm" },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
    }
}

