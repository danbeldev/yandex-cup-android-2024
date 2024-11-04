package ru.danbeldev.yandex_cup.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

data class PathProperties(
    val color: Color,
    val strokeWidth: Float = 5f,
    val enableInstrumentMode: Boolean,
    val enableDashedMode: Boolean,
    val paths: List<Path>
)

enum class DrawMode { Pencil, Eraser, Square, Circle, Triangle, Arrow, Zoom, Dashed  }

@Composable
fun DrawingCanvas(
    paths: SnapshotStateList<PathProperties>,
    sketch: List<PathProperties>,
    enableSketch: Boolean = true,
    drawMode: DrawMode = DrawMode.Pencil,
    color: Color = Color.Black,
    strokeWidth: Float = 5f,
    enableDrawing: Boolean = false,
    onUpdatePath: () -> Unit,
    onDrawScope: DrawScope.() -> Unit
) {
    val dragonPath = remember { mutableStateListOf<Path>() }
    var path by remember { mutableStateOf(Path()) }
    var startOffset by remember { mutableStateOf<Offset?>(null) }
    var endOffset by remember { mutableStateOf<Offset?>(null) }
    var currentShapePath by remember { mutableStateOf<Path?>(null) }

    Column {
        Canvas(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clip(AbsoluteRoundedCornerShape(20.dp))
                .fillMaxHeight()
                .fillMaxWidth()
                .shadow(1.dp)
                .weight(1f)
                .pointerInput(color, drawMode, enableDrawing, strokeWidth) {
                    if (drawMode != DrawMode.Zoom) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                if (!enableDrawing) return@detectDragGestures
                                startOffset = offset
                                endOffset = offset
                                path = Path().apply { moveTo(offset.x, offset.y) }
                            },
                            onDrag = { change, _ ->
                                if (!enableDrawing) return@detectDragGestures
                                endOffset = change.position
                                currentShapePath = Path().apply {
                                    startOffset?.let { start ->
                                        when (drawMode) {
                                            DrawMode.Square -> addRect(
                                                androidx.compose.ui.geometry.Rect(
                                                    start.x,
                                                    start.y,
                                                    endOffset!!.x,
                                                    endOffset!!.y
                                                )
                                            )
                                            DrawMode.Circle -> {
                                                val radius = (start - endOffset!!).getDistance() / 2
                                                val center = Offset(
                                                    (start.x + endOffset!!.x) / 2,
                                                    (start.y + endOffset!!.y) / 2
                                                )
                                                addOval(
                                                    androidx.compose.ui.geometry.Rect(
                                                        center.x - radius,
                                                        center.y - radius,
                                                        center.x + radius,
                                                        center.y + radius
                                                    )
                                                )
                                            }
                                            DrawMode.Triangle -> {
                                                moveTo(start.x, endOffset!!.y)
                                                lineTo(endOffset!!.x, endOffset!!.y)
                                                lineTo((start.x + endOffset!!.x) / 2, start.y)
                                                close()
                                            }
                                            DrawMode.Arrow -> {
                                                moveTo(start.x, start.y)
                                                lineTo(endOffset!!.x, endOffset!!.y)

                                                val angle = kotlin.math.atan2(
                                                    endOffset!!.y - start.y,
                                                    endOffset!!.x - start.x
                                                )
                                                val arrowHeadLength = 20f
                                                val angleOffset = Math.toRadians(30.0)

                                                val arrowPoint1 = Offset(
                                                    x = endOffset!!.x - arrowHeadLength * kotlin.math
                                                        .cos(angle - angleOffset)
                                                        .toFloat(),
                                                    y = endOffset!!.y - arrowHeadLength * kotlin.math
                                                        .sin(angle - angleOffset)
                                                        .toFloat()
                                                )
                                                val arrowPoint2 = Offset(
                                                    x = endOffset!!.x - arrowHeadLength * kotlin.math
                                                        .cos(angle + angleOffset)
                                                        .toFloat(),
                                                    y = endOffset!!.y - arrowHeadLength * kotlin.math
                                                        .sin(angle + angleOffset)
                                                        .toFloat()
                                                )

                                                lineTo(arrowPoint1.x, arrowPoint1.y)
                                                moveTo(endOffset!!.x, endOffset!!.y)
                                                lineTo(arrowPoint2.x, arrowPoint2.y)
                                            }
                                            else -> Unit
                                        }
                                    }
                                }
                                if (drawMode == DrawMode.Pencil || drawMode == DrawMode.Dashed) {
                                    path.lineTo(change.position.x, change.position.y)
                                    dragonPath.add(path)
                                } else if (drawMode == DrawMode.Eraser) {
                                    paths.removeAll { element ->
                                        element.paths.any { path ->
                                            isPointInPath(path, change.position)
                                        }
                                    }
                                }
                            },
                            onDragEnd = {
                                if (!enableDrawing) return@detectDragGestures
                                if (currentShapePath != null && drawMode != DrawMode.Eraser && dragonPath.isNotEmpty()) {
                                    paths.add(
                                        PathProperties(
                                            color = color,
                                            strokeWidth = strokeWidth,
                                            enableDashedMode= drawMode == DrawMode.Dashed,
                                            paths = dragonPath.toList(),
                                            enableInstrumentMode = enableInstrumentMode(drawMode)
                                        )
                                    )
                                    dragonPath.clear()
                                    onUpdatePath()
                                } else if (currentShapePath != null && drawMode != DrawMode.Eraser) {
                                    paths.add(
                                        PathProperties(
                                            color = color,
                                            strokeWidth = strokeWidth,
                                            enableDashedMode= drawMode == DrawMode.Dashed,
                                            paths = listOf(currentShapePath!!),
                                            enableInstrumentMode = enableInstrumentMode(drawMode)
                                        )
                                    )
                                    onUpdatePath()
                                }
                                startOffset = null
                                endOffset = null
                                currentShapePath = null
                                path = Path()
                            }
                        )
                    }
                }
        ) {
            onDrawScope()

            paths.forEach { pt ->
                pt.paths.forEach { p ->
                    drawPath(
                        path = p,
                        color = pt.color,
                        style = Stroke(
                            width = pt.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                            pathEffect = if (pt.enableDashedMode) {
                                PathEffect.dashPathEffect(floatArrayOf(50f, 50f), 0f)
                            } else null
                        )
                    )
                }
            }

            if (enableSketch) {
                sketch.forEach { pt ->
                    pt.paths.forEach { p ->
                        drawPath(
                            path = p,
                            color = pt.color.copy(if (pt.enableInstrumentMode) 0.1f else 0.01f),
                            style = Stroke(
                                width = pt.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round,
                                pathEffect = if (pt.enableDashedMode) {
                                    PathEffect.dashPathEffect(floatArrayOf(50f, 50f), 0f)
                                } else null
                            )
                        )
                    }
                }
            }

            dragonPath.forEach { p ->
                drawPath(
                    path = p,
                    color = color,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                        pathEffect = if (drawMode == DrawMode.Dashed) {
                            PathEffect.dashPathEffect(floatArrayOf(50f, 50f), 0f)
                        } else null
                    )
                )
            }
            currentShapePath?.let {
                drawPath(
                    path = it,
                    color = color,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                        pathEffect = if (drawMode == DrawMode.Dashed) {
                            PathEffect.dashPathEffect(floatArrayOf(50f, 50f), 0f)
                        } else null
                    )
                )
            }
            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = if (drawMode == DrawMode.Dashed) {
                        PathEffect.dashPathEffect(floatArrayOf(50f, 50f), 0f)
                    } else null
                )
            )
        }
    }
}

private fun isPointInPath(path: Path, point: Offset, tolerance: Float = 10f): Boolean {
    val pathBounds = path.getBounds()
    return pathBounds.inflate(tolerance).contains(point)
}

fun enableInstrumentMode(drawMode: DrawMode): Boolean =
    drawMode == DrawMode.Square || drawMode == DrawMode.Circle || drawMode == DrawMode.Triangle ||
            drawMode == DrawMode.Arrow