package ru.danbeldev.yandex_cup.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.danbeldev.yandex_cup.R
import ru.danbeldev.yandex_cup.ui.screens.DrawMode
import ru.danbeldev.yandex_cup.ui.theme.tintColor

@Composable
fun InstrumentsBar(
    drawMode: DrawMode,
    onDrawModeChange: (DrawMode) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(AbsoluteRoundedCornerShape(3.dp))
            .background(Color(0xFFB0B0B0))
    ) {
        Row {
            IconButton(onClick = { onDrawModeChange(DrawMode.Square) }) {
                Icon(
                    painter = painterResource(id = R.drawable.square),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = if (drawMode == DrawMode.Square) tintColor else Color.White
                )
            }

            IconButton(onClick = { onDrawModeChange(DrawMode.Circle) }) {
                Icon(
                    painter = painterResource(id = R.drawable.circle),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = if (drawMode == DrawMode.Circle) tintColor else Color.White
                )
            }

            IconButton(onClick = { onDrawModeChange(DrawMode.Triangle) }) {
                Icon(
                    painter = painterResource(id = R.drawable.triangle),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = if (drawMode == DrawMode.Triangle) tintColor else Color.White
                )
            }

            IconButton(onClick = { onDrawModeChange(DrawMode.Arrow) }) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_up),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = if (drawMode == DrawMode.Arrow) tintColor else Color.White
                )
            }
        }
    }
}