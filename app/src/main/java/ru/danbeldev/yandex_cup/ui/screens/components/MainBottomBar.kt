package ru.danbeldev.yandex_cup.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.danbeldev.yandex_cup.R
import ru.danbeldev.yandex_cup.ui.theme.rejectColor
import ru.danbeldev.yandex_cup.ui.theme.tintColor

@Composable
fun MainBottomBar(
    currencyColor: Color = Color.Black,
    enablePencil: Boolean = false,
    enableEraser: Boolean = false,
    enableInstruments: Boolean = false,
    enableColor: Boolean = false,
    enableStrokeWidth: Boolean = false,
    enableZoom: Boolean = false,
    enableDashed: Boolean = false,
    animationIsRunning: Boolean = false,
    strokeWidth: Float = 15f,
    onPencil: () -> Unit = {},
    onEraser: () -> Unit = {},
    onInstruments: () -> Unit = {},
    onColor: () -> Unit = {},
    onStrokeWidth: () -> Unit = {},
    onZoom: () -> Unit = {},
    onDashed: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            if (!animationIsRunning)
                onZoom()
        }) {
            Icon(
                painter = painterResource(id = R.drawable.hands),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = if (animationIsRunning) rejectColor else if (enableZoom) tintColor else Color.White
            )
        }

        IconButton(onClick = {
            if (!animationIsRunning)
                onPencil()
        }) {
            Icon(
                painter = painterResource(id = R.drawable.pencil),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = if (animationIsRunning) rejectColor else if (enablePencil) tintColor else Color.White
            )
        }

        IconButton(onClick = {
            if (!animationIsRunning)
                onDashed()
        }) {
            Icon(
                painter = painterResource(id = R.drawable.group),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = if (animationIsRunning) rejectColor else if (enableDashed) tintColor else Color.White
            )
        }

        IconButton(onClick = {
            if (!animationIsRunning)
                onEraser()
        }) {
            Icon(
                painter = painterResource(id = R.drawable.erase),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = if (animationIsRunning) rejectColor else if (enableEraser) tintColor else Color.White
            )
        }

        IconButton(onClick = {
            if (!animationIsRunning) onInstruments()
        }) {
            Icon(
                painter = painterResource(id = R.drawable.instruments),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = if (animationIsRunning) rejectColor else if (enableInstruments) tintColor else Color.White
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        ColorItem(
            color = currencyColor,
            borderColor = if (animationIsRunning) rejectColor else if (enableColor) tintColor else currencyColor,
            onClick = {
                if (!animationIsRunning) onColor()
            }
        )

        Spacer(modifier = Modifier.width(14.dp))

        DrawPoint(
            pointSize = strokeWidth,
            color = Color.White,
            borderColor = if (animationIsRunning) rejectColor else if (enableStrokeWidth) tintColor else Color.Transparent,
            modifier = Modifier.padding(start = 5.dp).clickable {
                onStrokeWidth()
            }
        )
    }
}