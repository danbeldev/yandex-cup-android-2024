package ru.danbeldev.yandex_cup.ui.screens.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrokeWidthSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    activeColor: Color = Color.Blue
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = 3f..40f,
        modifier = modifier,
        thumb = {
            Canvas(modifier = Modifier.size(20.dp)) {
                drawCircle(color = Color.White, radius = value)
            }
        },
        track = { sliderPositions ->
            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)) {
                val trackHeight = size.height * 0.5f
                val activeWidth = size.width * sliderPositions.activeRange.endInclusive

                drawRoundRect(
                    color = Color.Gray,
                    topLeft = Offset(0f, (size.height - trackHeight) / 2),
                    size = size.copy(height = trackHeight),
                    cornerRadius = CornerRadius(trackHeight / 2, trackHeight / 2)
                )

                drawRoundRect(
                    color = activeColor,
                    topLeft = Offset(0f, (size.height - trackHeight) / 2),
                    size = size.copy(width = activeWidth, height = trackHeight),
                    cornerRadius = CornerRadius(trackHeight / 2, trackHeight / 2)
                )
            }
        }
    )
}

@Composable
fun DrawPoint(
    modifier: Modifier = Modifier,
    pointSize: Float,
    borderSize: Float = 5f,
    color: Color = Color.Blue,
    borderColor: Color = Color.Black
) {
    Canvas(modifier = modifier) {
        drawContext.canvas.drawCircle(
            center = Offset(x = size.width / 2, y = size.height / 2),
            radius = pointSize + borderSize,
            paint = Paint().apply {
                this.color = borderColor
                style = PaintingStyle.Fill
            }
        )

        drawContext.canvas.drawCircle(
            center = Offset(x = size.width / 2, y = size.height / 2),
            radius = pointSize,
            paint = Paint().apply {
                this.color = color
                style = PaintingStyle.Fill
            }
        )
    }
}