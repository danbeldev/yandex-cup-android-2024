package ru.danbeldev.yandex_cup.ui.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.danbeldev.yandex_cup.R
import ru.danbeldev.yandex_cup.ui.theme.tintColor

private val shortColors = listOf(
    Color.White,
    Color.Red,
    Color.Black,
    Color.Blue
)

@Composable
fun ColorBar(
    currentColor: Color,
    onColorChange: (Color, Boolean) -> Unit = { _, _ -> }
) {
    var enableFullColor by remember { mutableStateOf(false) }

    AnimatedVisibility(visible = enableFullColor) {
        ColorBarFull(
            initialColor = currentColor,
            onColorChange = onColorChange
        )
    }

    Spacer(modifier = Modifier.padding(5.dp))

    ColorBarShort(
        enableFullColor = enableFullColor,
        onColorChange = onColorChange,
        onEnableFullColorChange = { enableFullColor = it }
    )
}

@Composable
private fun ColorBarShort(
    enableFullColor: Boolean,
    onColorChange: (Color, Boolean) -> Unit,
    onEnableFullColorChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(AbsoluteRoundedCornerShape(3.dp))
            .background(Color(0xFFB0B0B0))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onEnableFullColorChange(!enableFullColor)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.color_palette),
                    contentDescription = null,
                    tint = if (enableFullColor) tintColor else Color.White,
                    modifier = Modifier.size(35.dp)
                )
            }

            shortColors.forEach { color ->
                ColorItem(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    color = color,
                    onClick = {
                        onColorChange(color, true)
                    }
                )
            }
        }
    }
}

@Composable
private fun ColorBarFull(
    initialColor: Color = Color.Black,
    onColorChange: (Color, Boolean) -> Unit
) {
    var red by remember { mutableFloatStateOf(initialColor.red) }
    var green by remember { mutableFloatStateOf(initialColor.green) }
    var blue by remember { mutableFloatStateOf(initialColor.blue) }

    LaunchedEffect(red, green, blue) {
        onColorChange(Color(red, green, blue), false)
    }

    Box(
        modifier = Modifier
            .widthIn(max = 300.dp)
            .clip(AbsoluteRoundedCornerShape(8.dp))
            .background(Color(0xFFB0B0B0))
    ) {
        Column(
            modifier = Modifier.padding(3.dp)
        ) {
            ColorSlider(
                label = "Red",
                color = Color.Red,
                value = red,
                onValueChange = { red = it }
            )
            ColorSlider(
                label = "Green",
                color = Color.Green,
                value = green,
                onValueChange = { green = it }
            )
            ColorSlider(
                label = "Blue",
                color = Color.Blue,
                value = blue,
                onValueChange = { blue = it }
            )
        }
    }
}

@Composable
fun ColorItem(
    modifier: Modifier = Modifier,
    color: Color,
    borderColor: Color = color,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(color)
            .border(BorderStroke(2.dp, borderColor), CircleShape)
            .clickable { onClick() }
    )
}