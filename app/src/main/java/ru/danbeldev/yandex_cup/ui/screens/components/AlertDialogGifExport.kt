package ru.danbeldev.yandex_cup.ui.screens.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import ru.danbeldev.yandex_cup.ui.screens.ExportGifStatus
import java.io.File

@Composable
fun AlertDialogGifExport(
    currentProgress: Int,
    finalProgress: Int,
    exportGifStatus: ExportGifStatus,
    exportGifFile: File?,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Экспорт в GIF")
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when(exportGifStatus) {
                    ExportGifStatus.SUCCESS -> {
                        Text(text = "Файл сохранен в загрузках")
                    }
                    ExportGifStatus.FAILED -> {
                        Text(text = "Ошибка")
                    }
                    ExportGifStatus.PROCESS -> {
                        Text(text = "Прогресс: $currentProgress/$finalProgress")
                    }
                }
            }
        },
        confirmButton = {
            if (exportGifStatus == ExportGifStatus.SUCCESS) {
                Button(onClick = {
                    exportGifFile?.let {
                        shareGifFile(context, exportGifFile)
                    }
                }) {
                    Text("Поделиться GIF-файлом")
                }
            }
        }
    )
}

fun shareGifFile(context: Context, gifFile: File) {
    val gifUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        gifFile
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/gif"
        putExtra(Intent.EXTRA_STREAM, gifUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(
        Intent.createChooser(shareIntent, "Поделиться GIF")
    )
}