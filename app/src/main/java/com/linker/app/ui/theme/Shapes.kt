package com.linker.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),    // Checkboxes, small tooltips
    medium = RoundedCornerShape(12.dp),  // Standard Cards, Alert Dialogs
    large = RoundedCornerShape(16.dp)    // Bottom sheets, large containers
)