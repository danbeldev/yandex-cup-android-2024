package ru.danbeldev.yandex_cup.ui.screens.components

import android.widget.Toast
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun AlertDialogGenerationFrames(
    onDismissRequest: () -> Unit,
    onGeneration: (count: Int) -> Unit
) {
    val context = LocalContext.current
    var count by remember { mutableStateOf("5") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Генерация кадров")
        },
        text = {
            OutlinedTextField(
                value = count,
                onValueChange = { count = it },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                )
            )
        },
        confirmButton = {
            Button(onClick = {
                val countInt = count.toIntOrNull()
                if (countInt == null) {
                    Toast.makeText(context, "Введите целое число", Toast.LENGTH_SHORT).show()
                }else if (countInt <= 0) {
                    Toast.makeText(context, "Число должно быть больше нуля", Toast.LENGTH_SHORT).show()
                }else {
                    onGeneration(countInt)
                }
            }) {
                Text(text = "Генерировать")
            }
        }
    )
}