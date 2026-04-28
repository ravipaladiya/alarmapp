package com.lumen.alarm.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val LumenShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

// Named radius tokens matching design spec
val RadiusSm   = 10.dp
val RadiusMd   = 16.dp
val RadiusLg   = 22.dp
val RadiusXl   = 28.dp
val RadiusPill = 999.dp

val ShapeSmall    = RoundedCornerShape(RadiusSm)
val ShapeMedium   = RoundedCornerShape(RadiusMd)
val ShapeLarge    = RoundedCornerShape(RadiusLg)
val ShapeXLarge   = RoundedCornerShape(RadiusXl)
val ShapePill     = RoundedCornerShape(RadiusPill)

// Bottom sheet only rounds the top corners
val ShapeSheet    = RoundedCornerShape(topStart = RadiusXl, topEnd = RadiusXl)
