package ru.danbeldev.yandex_cup.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.danbeldev.yandex_cup.R
import ru.danbeldev.yandex_cup.ui.screens.Frame

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogFrames(
    frames: List<Frame>,
    onDismissRequest: () -> Unit,
    onClick: (Int, Frame) -> Unit,
    onDuplicate: (Int, Frame) -> Unit,
    onClearFrames: () -> Unit,
    onGeneration: () -> Unit,
    onGif: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismissRequest) {
        LazyColumn {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Раскадровка (${frames.size})",
                        modifier = Modifier.padding(start = 8.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp,
                        color = Color.White
                    )

                    Row {
                        IconButton(onClick = onClearFrames) {
                            Icon(
                                painter = painterResource(id = R.drawable.broom),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = Color.White
                            )
                        }

                        IconButton(onClick = onGeneration) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit_tools),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = Color.White
                            )
                        }

                        IconButton(onClick = onGif) {
                            Icon(
                                painter = painterResource(id = R.drawable.file),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
            itemsIndexed(frames) {index, frame ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    shape = AbsoluteRoundedCornerShape(15.dp),
                    onClick = { onClick(index, frame) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Кадр #${(index + 1)}",
                            modifier = Modifier.padding(start = 8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp
                        )

                        IconButton(onClick = { onDuplicate(index, frame) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.duplicate),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = Color(0xFF000000)
                            )
                        }
                    }
                }
            }
        }
    }
}