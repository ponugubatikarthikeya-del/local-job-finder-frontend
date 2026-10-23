package com.linker.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),    // Chips, small buttons, tags
    medium = RoundedCornerShape(18.dp),   // Standard cards, dialogs
    large = RoundedCornerShape(24.dp),    // Large feature cards, bottom sheets
    extraLarge = RoundedCornerShape(28.dp) // Main hero containers
)
