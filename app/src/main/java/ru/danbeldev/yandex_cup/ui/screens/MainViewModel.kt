package ru.danbeldev.yandex_cup.ui.screens

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.gifencoder.GifEncoder
import com.squareup.gifencoder.ImageOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class ExportGifStatus {
    SUCCESS,
    FAILED,
    PROCESS
}

class MainViewModel : ViewModel() {
    var drawMode by mutableStateOf(DrawMode.Pencil)
    var barType by mutableStateOf(BarType.Disable)
    var color by mutableStateOf(Color.Blue)
    var strokeWidth by mutableFloatStateOf(10f)

    val paths = mutableStateListOf<PathProperties>()
    val redoStack = mutableStateListOf<PathProperties>()
    val frames = mutableStateListOf<Frame>()
    private var currentFrame by mutableStateOf(Frame())
    private var currentIndexFrame by mutableIntStateOf(0)

    var enableAlertDialogFrames by mutableStateOf(false)
    var enableAlertDialogGenerationFrames by mutableStateOf(false)
    var enableAlertDialogGifExport by mutableStateOf(false)

    var animationIsRunning by mutableStateOf(false)
    var animationSpeed by mutableLongStateOf(300L)

    var maxWidthFrame by mutableFloatStateOf(0.0f)
    var maxHeightFrame by mutableFloatStateOf(0.0f)
    var minDimensionFrame by mutableFloatStateOf(0.0f)

    var scale by mutableFloatStateOf(1f)
    var offsetX by mutableFloatStateOf(0f)
    var offsetY by mutableFloatStateOf(0f)

    var initialWidth by mutableFloatStateOf(0f)
    var initialHeight by mutableFloatStateOf(0f)

    var currentProgressExportGif by mutableIntStateOf(0)
    var finalProgressExportGif by mutableIntStateOf(0)
    var exportGifStatus by mutableStateOf(ExportGifStatus.PROCESS)
    var exportGifFile by mutableStateOf<File?>(null)

    init {
        frames.add(currentFrame)
    }

    fun save() {
        currentFrame.paths = paths.toList()
        currentFrame.redoStack = redoStack.toList()
        frames[currentIndexFrame] = currentFrame
    }

    fun changeCurrentFrame(frame: Frame, index: Int) {
        currentFrame = frame
        currentIndexFrame = index

        paths.clear()
        paths.addAll(currentFrame.paths.toList())
        redoStack.clear()
        redoStack.addAll(currentFrame.redoStack.toList())
    }

    fun backActive() {
        if (paths.isNotEmpty()) {
            val lastElement = paths.last()
            redoStack.add(lastElement)
            paths.remove(lastElement)
            save()
        }
    }

    fun forwardActive() {
        if (redoStack.isNotEmpty()) {
            val lastElement = redoStack.last()
            paths.add(lastElement)
            redoStack.remove(lastElement)
            save()
        }
    }

    fun clearRedoStack() {
        redoStack.clear()
    }

    fun clearCurrentFrame() {
        if (frames.isEmpty()) {
            paths.clear()
            redoStack.clear()
        } else if (frames.size == 1) {
            frames.remove(currentFrame)
            frames.add(Frame())
            changeCurrentFrame(Frame(), 0)
        } else {
            val index = if (currentIndexFrame == 0)
                currentIndexFrame
            else
                currentIndexFrame - 1
            frames.remove(currentFrame)
            changeCurrentFrame(
                frames.getOrNull(index) ?: Frame(),
                index
            )
        }
    }

    fun addNewFrame() {
        val newFrame = Frame()
        val index = currentIndexFrame + 1
        frames.add(index, newFrame)
        changeCurrentFrame(newFrame, index)
    }

    fun duplicateFrame(frame: Frame, index: Int) {
        val newFrame = frame.copy()
        frames.add(index + 1, newFrame)
        changeCurrentFrame(newFrame, index + 1)
    }

    fun clearFrames() {
        val baseFrame = Frame()
        frames.clear()
        frames.add(baseFrame)
        changeCurrentFrame(baseFrame, 0)
    }

    fun toggleAlertDialogFrames() {
        enableAlertDialogFrames = !enableAlertDialogFrames
    }

    fun startAnimation() {
        viewModelScope.launch {
            scale = 1f
            offsetX = 0f
            offsetY = 0f
            if (drawMode == DrawMode.Zoom) drawMode = DrawMode.Pencil
            animationIsRunning = true
            while (animationIsRunning) {
                frames.forEachIndexed { index, frame ->
                    if (!animationIsRunning) return@forEachIndexed
                    changeCurrentFrame(frame, index)
                    delay(animationSpeed)
                }
            }
        }
    }

    fun stopAnimation() {
        animationIsRunning = false
        changeCurrentFrame(frames.last(), frames.size-1)
    }

    fun animationSpeedMinus() {
        if (animationSpeed > 100)
            animationSpeed -= 100
    }

    fun animationSpeedPlus() {
        animationSpeed += 100
    }

    fun getSketch(): List<PathProperties> {
        if (currentIndexFrame == 0) return emptyList()
        return frames.getOrNull(currentIndexFrame - 1)?.paths ?: emptyList()
    }

    fun generationFrames(frameCount: Int) {
        for (i in 0 until frameCount) {
            generationFrame()
        }
        changeCurrentFrame(frames.last(), frames.size - 1)
    }

    private fun generationFrame() {
        val frame = Frame()
        val paths = mutableListOf<PathProperties>()
        repeat((5..20).random()) {
            paths.add(generatePathProps())
        }
        frame.paths = paths
        frame.redoStack = frame.paths.toList()
        frames.add(frame)
    }

    private fun generatePathProps(): PathProperties {
        val (paths, mode) = generationPaths()
        return PathProperties(
            color = randomColor(),
            strokeWidth = randomFloatInRange(3f, 40f),
            paths = paths,
            enableInstrumentMode = enableInstrumentMode(mode),
            enableDashedMode = (0..1).random() == 0
        )
    }

    private fun randomFloatInRange(min: Float, max: Float): Float {
        return Random.nextFloat() * (max - min) + min
    }

    private fun randomColor(): Color {
        return Color(
            (0..255).random(),
            (0..255).random(),
            (0..255).random()
        )
    }

    private fun generationPaths(): Pair<List<Path>, DrawMode> {
        return when ((0..4).random()) {
            0 -> listOf(generateRandomLine()) to DrawMode.Pencil
            1 -> listOf(generateRandomCircle()) to DrawMode.Circle
            2 -> listOf(generateRandomSquare()) to DrawMode.Square
            3 -> listOf(generateRandomTriangle()) to DrawMode.Triangle
            else -> listOf(generateRandomArrow()) to DrawMode.Arrow
        }
    }

    private fun generateRandomLine(): Path {

        val startX = Random.nextFloat() * maxWidthFrame
        val startY = Random.nextFloat() * maxHeightFrame
        val endX = Random.nextFloat() * maxWidthFrame
        val endY = Random.nextFloat() * maxHeightFrame

        return Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
    }

    private fun generateRandomCircle(): Path {
        val radius = Random.nextFloat() * (minDimensionFrame / 4)
        val centerX = Random.nextFloat() * maxWidthFrame
        val centerY = Random.nextFloat() * maxHeightFrame

        return Path().apply {
            addOval(
                androidx.compose.ui.geometry.Rect(
                    centerX - radius,
                    centerY - radius,
                    centerX + radius,
                    centerY + radius
                )
            )
        }
    }

    private fun generateRandomSquare(): Path {
        val side = Random.nextFloat() * (minDimensionFrame / 4)
        val topX = Random.nextFloat() * (maxWidthFrame - side)
        val topY = Random.nextFloat() * (maxHeightFrame - side)

        return Path().apply {
            addRect(androidx.compose.ui.geometry.Rect(topX, topY, topX + side, topY + side))
        }
    }

    private fun generateRandomTriangle(): Path {
        val x1 = Random.nextFloat() * maxWidthFrame
        val y1 = Random.nextFloat() * maxHeightFrame
        val x2 = Random.nextFloat() * maxWidthFrame
        val y2 = Random.nextFloat() * maxHeightFrame
        val x3 = Random.nextFloat() * maxWidthFrame
        val y3 = Random.nextFloat() * maxHeightFrame

        return Path().apply {
            moveTo(x1, y1)
            lineTo(x2, y2)
            lineTo(x3, y3)
            close()
        }
    }

    private fun generateRandomArrow(): Path {
        val startX = Random.nextFloat() * maxWidthFrame
        val startY = Random.nextFloat() * maxHeightFrame
        val endX = Random.nextFloat() * maxWidthFrame
        val endY = Random.nextFloat() * maxHeightFrame

        val angle = atan2(endY - startY, endX - startX)

        val arrowHeadLength = 20f
        val arrowHeadAngle = Math.PI / 6

        val arrowHeadX1 = endX - arrowHeadLength * cos(angle - arrowHeadAngle).toFloat()
        val arrowHeadY1 = endY - arrowHeadLength * sin(angle - arrowHeadAngle).toFloat()
        val arrowHeadX2 = endX - arrowHeadLength * cos(angle + arrowHeadAngle).toFloat()
        val arrowHeadY2 = endY - arrowHeadLength * sin(angle + arrowHeadAngle).toFloat()

        return Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)

            moveTo(endX, endY)
            lineTo(arrowHeadX1, arrowHeadY1)
            moveTo(endX, endY)
            lineTo(arrowHeadX2, arrowHeadY2)
        }
    }

    fun createGif(background: Bitmap, onSuccess: () -> Unit, onFailed: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    scale = 1f
                    offsetX = 0f
                    offsetY = 0f

                    finalProgressExportGif = frames.size + 1
                    exportGifStatus = ExportGifStatus.PROCESS
                    currentProgressExportGif = 0
                    exportGifFile = null

                    val bitmaps = frames.map { drawFrameToBitmap(it, background) }

                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val gifFile = File(downloadsDir, "${UUID.randomUUID()}.gif")

                    val outputStream = FileOutputStream(gifFile)

                    val encoder = GifEncoder(outputStream, bitmaps[0].width, bitmaps[0].height, 0)

                    bitmaps.forEach{
                        currentProgressExportGif += 1
                        val options = ImageOptions()
                        options.setDelay(animationSpeed, TimeUnit.MILLISECONDS)
                        encoder.addImage(bitmapToRGBArray(it), options)
                    }

                    encoder.finishEncoding()

                    outputStream.close()
                    currentProgressExportGif += 1
                    exportGifFile = gifFile
                    exportGifStatus = ExportGifStatus.SUCCESS
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }catch (e: Exception) {
                    e.printStackTrace()
                    exportGifStatus = ExportGifStatus.FAILED
                    withContext(Dispatchers.Main) {
                        onFailed()
                    }
                }
            }
        }
    }

    private fun bitmapToRGBArray(bitmap: Bitmap): Array<IntArray> {
        val width = bitmap.width
        val height = bitmap.height
        val rgbData = Array(height) { IntArray(width) }

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                rgbData[y][x] = pixels[y * width + x]
            }
        }

        return rgbData
    }

    private fun drawFrameToBitmap(frame: Frame, background: Bitmap): Bitmap {
        val bitmap = Bitmap.createBitmap(initialWidth.toInt(), initialHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawBitmap(background, 0f, 0f, null)
        Canvas(canvas).apply {
            frame.paths.forEach { pt ->
                pt.paths.forEach { p ->
                    drawPath(
                        path = p,
                        paint = Paint().apply {
                            this.color = pt.color
                            this.strokeCap = StrokeCap.Round
                            this.strokeWidth = pt.strokeWidth
                            this.strokeJoin = StrokeJoin.Round
                            pathEffect = if (pt.enableDashedMode) {
                                PathEffect.dashPathEffect(floatArrayOf(50f, 50f), 0f)
                            } else null
                        }
                    )
                }
            }
        }

        return bitmap
    }
}