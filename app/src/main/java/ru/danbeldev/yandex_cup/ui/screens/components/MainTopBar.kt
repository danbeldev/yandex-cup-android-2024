package ru.danbeldev.yandex_cup.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.danbeldev.yandex_cup.R
import ru.danbeldev.yandex_cup.ui.theme.rejectColor

@Composable
fun MainTopBar(
    speed: Long,
    enableBackActive: Boolean,
    enableForwardActive: Boolean,
    animationIsRunning: Boolean,
    onBackActive: () -> Unit,
    onForwardActive: () -> Unit,
    onClearFrame: () -> Unit,
    onAddFrame: () -> Unit,
    onFrames: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onSpeedPlus: () -> Unit,
    onSpeedMinus: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row {
                IconButton(onClick = { if (enableBackActive && !animationIsRunning) onBackActive() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.right_active),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = if (enableBackActive && !animationIsRunning) Color(0xFFFFFFFF) else rejectColor
                    )
                }

                IconButton(onClick = { if (enableForwardActive && !animationIsRunning) onForwardActive() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.left_active),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = if (enableForwardActive && !animationIsRunning) Color(0xFFFFFFFF) else rejectColor
                    )
                }
            }

            Row {
                IconButton(onClick = {
                    if (!animationIsRunning)
                        onClearFrame()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.bin),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = if (!animationIsRunning) Color(0xFFFFFFFF) else rejectColor
                    )
                }

                IconButton(onClick = {
                    if (!animationIsRunning)
                        onAddFrame()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.file_plus),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = if (!animationIsRunning) Color(0xFFFFFFFF) else rejectColor
                    )
                }

                IconButton(onClick = {
                    if (!animationIsRunning)
                        onFrames()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.layers),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = if (!animationIsRunning) Color(0xFFFFFFFF) else rejectColor
                    )
                }
            }

            Row {
                IconButton(onClick = { if (!animationIsRunning) onStart() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.active),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = if (!animationIsRunning) Color(0xFFFFFFFF) else rejectColor
                    )
                }

                IconButton(onClick = { if (animationIsRunning) onStop() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.active_2),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = if (animationIsRunning) Color(0xFFFFFFFF) else rejectColor
                    )
                }
            }
        }

        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSpeedMinus) {
                Icon(
                    painter = painterResource(id = R.drawable.minus),
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    tint = if (speed > 100) Color.White else rejectColor
                )
            }

            Text(
                text = "${speed / 1000.0} с.",
                color = Color.White
            )

            IconButton(onClick = onSpeedPlus) {
                Icon(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    tint = Color.White
                )
            }
        }
    }
}