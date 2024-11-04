package ru.danbeldev.yandex_cup.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.danbeldev.yandex_cup.R
import ru.danbeldev.yandex_cup.ui.screens.BarType.Disable
import ru.danbeldev.yandex_cup.ui.screens.BarType.Instrument
import ru.danbeldev.yandex_cup.ui.screens.components.AlertDialogFrames
import ru.danbeldev.yandex_cup.ui.screens.components.AlertDialogGenerationFrames
import ru.danbeldev.yandex_cup.ui.screens.components.AlertDialogGifExport
import ru.danbeldev.yandex_cup.ui.screens.components.ColorBar
import ru.danbeldev.yandex_cup.ui.screens.components.InstrumentsBar
import ru.danbeldev.yandex_cup.ui.screens.components.MainBottomBar
import ru.danbeldev.yandex_cup.ui.screens.components.MainTopBar
import ru.danbeldev.yandex_cup.ui.screens.components.StrokeWidthBar

enum class BarType { Instrument, Color, StrokeWith, Disable }

data class Frame(
    var paths: List<PathProperties> = emptyList(),
    var redoStack: List<PathProperties> = emptyList()
)

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        val allPermissionsGranted = permissionsResult.all { it.value }
        if (allPermissionsGranted) {
            Toast.makeText(context, "Начинаю экспорт в GIF", Toast.LENGTH_SHORT).show()
            getScaledBitmap(context, R.drawable.canvas, viewModel.initialWidth.toInt(), viewModel.initialHeight.toInt())?.let { background ->
                viewModel.createGif(
                    background = background,
                    onSuccess = {
                        Toast.makeText(context, "Файл сохранен в загрузках", Toast.LENGTH_SHORT).show()
                    },
                    onFailed = {
                        Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            viewModel.enableAlertDialogFrames = false
            viewModel.enableAlertDialogGifExport = true
        } else {
            Toast.makeText(context, "Требуются разрешения", Toast.LENGTH_SHORT).show()
        }
    }

    if (viewModel.enableAlertDialogFrames) {
        AlertDialogFrames(
            frames = viewModel.frames,
            onDismissRequest = {
                viewModel.toggleAlertDialogFrames()
            },
            onClick = { index, selectedFrame ->
                viewModel.changeCurrentFrame(selectedFrame, index)
                viewModel.toggleAlertDialogFrames()
            },
            onDuplicate = { index, frame ->
                viewModel.duplicateFrame(frame, index)
            },
            onClearFrames = {
                viewModel.clearFrames()
            },
            onGeneration = {
                viewModel.enableAlertDialogFrames = false
                viewModel.enableAlertDialogGenerationFrames = true
            },
            onGif = {
                permissionLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    )
                )
            }
        )
    }

    if (viewModel.enableAlertDialogGenerationFrames) {
        AlertDialogGenerationFrames(
            onDismissRequest = {
                viewModel.enableAlertDialogGenerationFrames = false
            },
            onGeneration = { count ->
                viewModel.generationFrames(count)
                viewModel.enableAlertDialogGenerationFrames = false
            }
        )
    }

    if (viewModel.enableAlertDialogGifExport) {
        AlertDialogGifExport(
            currentProgress = viewModel.currentProgressExportGif,
            finalProgress = viewModel.finalProgressExportGif,
            exportGifStatus = viewModel.exportGifStatus,
            exportGifFile = viewModel.exportGifFile,
            onDismissRequest = {
                viewModel.enableAlertDialogGifExport = false
            }
        )
    }

    Scaffold(
        topBar = {
            MainTopBar(
                enableBackActive = viewModel.paths.isNotEmpty(),
                enableForwardActive = viewModel.redoStack.isNotEmpty(),
                animationIsRunning = viewModel.animationIsRunning,
                speed = viewModel.animationSpeed,
                onBackActive = {
                    viewModel.backActive()
                },
                onForwardActive = {
                    viewModel.forwardActive()
                },
                onClearFrame = {
                    viewModel.clearCurrentFrame()
                },
                onAddFrame = {
                    viewModel.addNewFrame()
                },
                onFrames = {
                    viewModel.toggleAlertDialogFrames()
                },
                onStart = {
                    viewModel.startAnimation()
                },
                onStop = {
                    viewModel.stopAnimation()
                },
                onSpeedMinus = {
                    viewModel.animationSpeedMinus()
                },
                onSpeedPlus = {
                    viewModel.animationSpeedPlus()
                }
            )
        },
        bottomBar = {
            MainBottomBar(
                currencyColor = viewModel.color,
                enablePencil = viewModel.drawMode == DrawMode.Pencil && viewModel.barType != Instrument,
                enableEraser = viewModel.drawMode == DrawMode.Eraser && viewModel.barType != Instrument,
                enableDashed = viewModel.drawMode == DrawMode.Dashed && viewModel.barType != Instrument,
                enableInstruments = viewModel.barType == Instrument || (viewModel.drawMode != DrawMode.Pencil && viewModel.drawMode != DrawMode.Eraser && viewModel.drawMode != DrawMode.Dashed),
                enableColor = viewModel.barType == BarType.Color,
                animationIsRunning = viewModel.animationIsRunning,
                strokeWidth = viewModel.strokeWidth,
                enableStrokeWidth = viewModel.barType == BarType.StrokeWith,
                enableZoom = viewModel.drawMode == DrawMode.Zoom,
                onPencil = {
                    viewModel.drawMode = DrawMode.Pencil
                },
                onEraser = {
                    viewModel.drawMode = DrawMode.Eraser
                },
                onInstruments = {
                    viewModel.barType = if (viewModel.barType == Instrument) Disable else Instrument
                },
                onColor = {
                    viewModel.barType =
                        if (viewModel.barType == BarType.Color) Disable else BarType.Color
                },
                onStrokeWidth = {
                    viewModel.barType =
                        if (viewModel.barType == BarType.StrokeWith) Disable else BarType.StrokeWith
                },
                onZoom = {
                    viewModel.drawMode = DrawMode.Zoom
                },
                onDashed = {
                    viewModel.drawMode = DrawMode.Dashed
                }
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.canvas),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .graphicsLayer(
                            scaleX = viewModel.scale,
                            scaleY = viewModel.scale,
                            translationX = viewModel.offsetX,
                            translationY = viewModel.offsetY
                        )
                        .onGloballyPositioned { layoutCoordinates ->
                            if (viewModel.initialWidth == 0f && viewModel.initialHeight == 0f) {
                                viewModel.initialWidth = layoutCoordinates.size.width.toFloat()
                                viewModel.initialHeight = layoutCoordinates.size.height.toFloat()
                            }
                        }
                        .pointerInput(Unit) {
                            if (viewModel.drawMode == DrawMode.Zoom) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    viewModel.scale = (viewModel.scale * zoom).coerceIn(0.5f, 3f)
                                    viewModel.offsetX += pan.x
                                    viewModel.offsetY += pan.y
                                }
                            }
                        }
                )

                if (viewModel.drawMode != DrawMode.Zoom) {
                    DrawingCanvas(
                        drawMode = viewModel.drawMode,
                        color = viewModel.color,
                        strokeWidth = viewModel.strokeWidth,
                        paths = viewModel.paths,
                        enableDrawing = !viewModel.animationIsRunning,
                        sketch = viewModel.getSketch(),
                        enableSketch = !viewModel.animationIsRunning,
                        onUpdatePath = {
                            viewModel.save()
                            viewModel.clearRedoStack()
                        },
                        onDrawScope = {
                            viewModel.maxWidthFrame = size.width
                            viewModel.maxHeightFrame = size.height
                            viewModel.minDimensionFrame = size.minDimension
                        }
                    )
                }
            }

            AnimatedVisibility(
                visible = viewModel.barType != Disable && !viewModel.animationIsRunning,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (viewModel.barType) {
                        Instrument -> InstrumentsBar(
                            drawMode = viewModel.drawMode,
                            onDrawModeChange = {
                                viewModel.drawMode = it
                                viewModel.barType = Disable
                            }
                        )

                        BarType.Color -> ColorBar(
                            currentColor = viewModel.color,
                            onColorChange = { color, hide ->
                                viewModel.color = color
                                if (hide) viewModel.barType = Disable
                            }
                        )

                        BarType.StrokeWith -> StrokeWidthBar(
                            strokeWidth = viewModel.strokeWidth,
                            onStrokeWithChange = {
                                viewModel.strokeWidth = it
                            }
                        )

                        Disable -> Unit
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

fun getScaledBitmap(context: Context, resourceId: Int, reqWidth: Int, reqHeight: Int): Bitmap? {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeResource(context.resources, resourceId, options)

    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

    options.inJustDecodeBounds = false
    return BitmapFactory.decodeResource(context.resources, resourceId, options)
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (height: Int, width: Int) = options.run { outHeight to outWidth }

    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}