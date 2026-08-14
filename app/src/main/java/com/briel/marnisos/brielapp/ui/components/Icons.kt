package com.briel.marnisos.brielapp.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// Edit Icon (Pencil)
val EditIcon: ImageVector = ImageVector.Builder(
    name = "Edit",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = null,
        stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
    ) {
        moveTo(11f, 4f)
        horizontalLineTo(4f)
        arcToRelative(2f, 2f, 0f, false, false, -2f, 2f)
        verticalLineToRelative(14f)
        arcToRelative(2f, 2f, 0f, false, false, 2f, 2f)
        horizontalLineToRelative(14f)
        arcToRelative(2f, 2f, 0f, false, false, 2f, -2f)
        verticalLineToRelative(-7f)
    }
    path(
        fill = null,
        stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
    ) {
        moveTo(18.5f, 2.5f)
        arcToRelative(2.121f, 2.121f, 0f, false, true, 3f, 3f)
        lineTo(12f, 15f)
        lineToRelative(-4f, 1f)
        lineToRelative(1f, -4f)
        lineToRelative(9.5f, -9.5f)
        close()
    }
}.build()

// Delete Icon (Trash)
val DeleteIcon: ImageVector = ImageVector.Builder(
    name = "Delete",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = null,
        stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
    ) {
        moveTo(3f, 6f)
        horizontalLineToRelative(18f)
    }
    path(
        fill = null,
        stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
    ) {
        moveTo(19f, 6f)
        verticalLineToRelative(14f)
        arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
        horizontalLineTo(7f)
        arcToRelative(2f, 2f, 0f, false, true, -2f, -2f)
        verticalLineTo(6f)
        moveToRelative(3f, 0f)
        verticalLineTo(4f)
        arcToRelative(2f, 2f, 0f, false, true, 2f, -2f)
        horizontalLineToRelative(4f)
        arcToRelative(2f, 2f, 0f, false, true, 2f, 2f)
        verticalLineToRelative(2f)
    }
    path(
        fill = null,
        stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
    ) {
        moveTo(10f, 11f)
        verticalLineToRelative(6f)
    }
    path(
        fill = null,
        stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
    ) {
        moveTo(14f, 11f)
        verticalLineToRelative(6f)
    }
}.build()

// Export Icon (Download)
val ExportIcon: ImageVector = ImageVector.Builder(
    name = "Export",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = null,
        stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
    ) {
        moveTo(21f, 15f)
        verticalLineToRelative(4f)
        arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
        horizontalLineTo(5f)
        arcToRelative(2f, 2f, 0f, false, true, -2f, -2f)
        verticalLineToRelative(-4f)
    }
    path(
        fill = null,
        stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
    ) {
        moveTo(7f, 10f)
        lineToRelative(5f, 5f)
        lineToRelative(5f, -5f)
    }
    path(
        fill = null,
        stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
    ) {
        moveTo(12f, 15f)
        verticalLineTo(3f)
    }
}.build()

// Menu Icon (Hamburger)
val MenuIcon: ImageVector = ImageVector.Builder(
    name = "Menu",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = null,
        stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
    ) {
        moveTo(3f, 12f)
        horizontalLineToRelative(18f)
    }
    path(
        fill = null,
        stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
    ) {
        moveTo(3f, 6f)
        horizontalLineToRelative(18f)
    }
    path(
        fill = null,
        stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
    ) {
        moveTo(3f, 18f)
        horizontalLineToRelative(18f)
    }
}.build()

// Plus Icon (Add)
val PlusIcon: ImageVector = ImageVector.Builder(
    name = "Plus",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = null,
        stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
    ) {
        moveTo(12f, 5f)
        verticalLineToRelative(14f)
    }
    path(
        fill = null,
        stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
    ) {
        moveTo(5f, 12f)
        horizontalLineToRelative(14f)
    }
}.build()

// --- Fee-first flow icons -------------------------------------------------------

private fun strokeIcon(
    name: String,
    build: ImageVector.Builder.() -> Unit,
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply(build).build()

private fun ImageVector.Builder.strokedPath(pathBuilder: PathBuilder.() -> Unit) {
    path(
        fill = null,
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
        pathBuilder = pathBuilder,
    )
}

private fun PathBuilder.circle24() {
    moveTo(12f, 3f)
    arcToRelative(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 18f)
    arcToRelative(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, -18f)
}

/** Padlock — marks a destination blocked by the fee-first gate. */
val LockIcon: ImageVector = strokeIcon(name = "Lock") {
    strokedPath {
        moveTo(5f, 11f)
        horizontalLineTo(19f)
        verticalLineTo(21f)
        horizontalLineTo(5f)
        close()
    }
    strokedPath {
        moveTo(8f, 11f)
        verticalLineTo(7f)
        arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8f, 0f)
        verticalLineTo(11f)
    }
}

/** Check inside a circle — the gate is satisfied. */
val CheckCircleIcon: ImageVector = strokeIcon(name = "CheckCircle") {
    strokedPath { circle24() }
    strokedPath {
        moveTo(8.5f, 12.5f)
        lineTo(11f, 15f)
        lineTo(15.5f, 9.5f)
    }
}

/** Scan frame — start a study by reading the CUPS code. */
val ScanIcon: ImageVector = strokeIcon(name = "Scan") {
    strokedPath {
        moveTo(3f, 8f)
        verticalLineTo(4f)
        horizontalLineTo(7f)
    }
    strokedPath {
        moveTo(17f, 4f)
        horizontalLineTo(21f)
        verticalLineTo(8f)
    }
    strokedPath {
        moveTo(21f, 16f)
        verticalLineTo(20f)
        horizontalLineTo(17f)
    }
    strokedPath {
        moveTo(7f, 20f)
        horizontalLineTo(3f)
        verticalLineTo(16f)
    }
    strokedPath {
        moveTo(3f, 12f)
        horizontalLineTo(21f)
    }
}

/** Upload tray — start a study from a bill PDF. */
val FileUploadIcon: ImageVector = strokeIcon(name = "FileUpload") {
    strokedPath {
        moveTo(4f, 17f)
        verticalLineTo(20f)
        horizontalLineTo(20f)
        verticalLineTo(17f)
    }
    strokedPath {
        moveTo(12f, 15f)
        verticalLineTo(4f)
    }
    strokedPath {
        moveTo(7f, 9f)
        lineTo(12f, 4f)
        lineTo(17f, 9f)
    }
}

/** Info circle — explains why a destination is locked. */
val InfoIcon: ImageVector = strokeIcon(name = "Info") {
    strokedPath { circle24() }
    strokedPath {
        moveTo(12f, 11f)
        verticalLineTo(16.5f)
    }
    strokedPath {
        moveTo(12f, 7.5f)
        verticalLineTo(8.5f)
    }
}

/** Chevron pointing right — affordance on the consumption method options. */
val ChevronRightIcon: ImageVector = strokeIcon(name = "ChevronRight") {
    strokedPath {
        moveTo(9f, 6f)
        lineTo(15f, 12f)
        lineTo(9f, 18f)
    }
}

@Composable
fun ActionIcon(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = Color.Gray,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}
