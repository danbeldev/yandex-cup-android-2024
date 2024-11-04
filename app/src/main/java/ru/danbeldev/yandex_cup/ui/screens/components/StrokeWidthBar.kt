package ru.danbeldev.yandex_cup.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StrokeWidthBar(
    strokeWidth: Float = 5f,
    color: Color = Color.Transparent,
    onStrokeWithChange: (Float) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .clip(AbsoluteRoundedCornerShape(3.dp))
            .background(Color(0xFFB0B0B0))
            .widthIn(max = 300.dp)
    ) {
        StrokeWidthSlider(
            value = strokeWidth,
            onValueChange = onStrokeWithChange,
            activeColor = color,
            modifier = Modifier.padding(8.dp)
        )
    }
}