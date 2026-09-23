package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Difficulty

@Composable
fun DifficultyBadge(
    difficulty: Difficulty,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    showLabel: Boolean = true,
    stars: Int = difficulty.defaultStars
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Face Canvas
        Box(
            modifier = Modifier
                .size(size)
                .background(
                    color = Color(difficulty.colorHex).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size * 0.85f)) {
                val w = this.size.width
                val h = this.size.height
                val center = Offset(w / 2f, h / 2f)
                val diffColor = Color(difficulty.colorHex)

                // Outer circle / face background
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(diffColor, diffColor.copy(alpha = 0.7f)),
                        center = center,
                        radius = w / 2f
                    ),
                    radius = w / 2.2f,
                    center = center
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = w / 2.2f,
                    center = center,
                    style = Stroke(width = 2.5f)
                )

                when (difficulty) {
                    Difficulty.AUTO -> {
                        // Visor
                        drawRoundRect(
                            color = Color.Cyan,
                            topLeft = Offset(w * 0.2f, h * 0.35f),
                            size = Size(w * 0.6f, h * 0.3f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                        )
                    }
                    Difficulty.EASY -> {
                        // Round happy eyes & smile
                        drawCircle(Color.Black, radius = w * 0.08f, center = Offset(w * 0.35f, h * 0.4f))
                        drawCircle(Color.Black, radius = w * 0.08f, center = Offset(w * 0.65f, h * 0.4f))
                        val mouth = Path().apply {
                            moveTo(w * 0.3f, h * 0.6f)
                            quadraticTo(w * 0.5f, h * 0.8f, w * 0.7f, h * 0.6f)
                        }
                        drawPath(mouth, Color.Black, style = Stroke(width = 3f))
                    }
                    Difficulty.NORMAL -> {
                        // Cheeky grin
                        drawCircle(Color.Black, radius = w * 0.08f, center = Offset(w * 0.33f, h * 0.38f))
                        drawCircle(Color.Black, radius = w * 0.08f, center = Offset(w * 0.67f, h * 0.38f))
                        val smile = Path().apply {
                            moveTo(w * 0.28f, h * 0.58f)
                            quadraticTo(w * 0.5f, h * 0.78f, w * 0.72f, h * 0.58f)
                            close()
                        }
                        drawPath(smile, Color.Black)
                    }
                    Difficulty.HARD -> {
                        // Fierce slanted eyes
                        val leftEye = Path().apply {
                            moveTo(w * 0.25f, h * 0.35f)
                            lineTo(w * 0.45f, h * 0.42f)
                            lineTo(w * 0.3f, h * 0.45f)
                            close()
                        }
                        val rightEye = Path().apply {
                            moveTo(w * 0.75f, h * 0.35f)
                            lineTo(w * 0.55f, h * 0.42f)
                            lineTo(w * 0.7f, h * 0.45f)
                            close()
                        }
                        drawPath(leftEye, Color.Black)
                        drawPath(rightEye, Color.Black)
                        drawLine(Color.Black, Offset(w * 0.35f, h * 0.68f), Offset(w * 0.65f, h * 0.68f), strokeWidth = 3f)
                    }
                    Difficulty.HARDER -> {
                        // Angry angled eyes & jagged mouth
                        val leftEye = Path().apply {
                            moveTo(w * 0.22f, h * 0.32f)
                            lineTo(w * 0.45f, h * 0.42f)
                            lineTo(w * 0.28f, h * 0.46f)
                            close()
                        }
                        val rightEye = Path().apply {
                            moveTo(w * 0.78f, h * 0.32f)
                            lineTo(w * 0.55f, h * 0.42f)
                            lineTo(w * 0.72f, h * 0.46f)
                            close()
                        }
                        drawPath(leftEye, Color.Black)
                        drawPath(rightEye, Color.Black)
                        val mouth = Path().apply {
                            moveTo(w * 0.28f, h * 0.68f)
                            lineTo(w * 0.4f, h * 0.62f)
                            lineTo(w * 0.5f, h * 0.72f)
                            lineTo(w * 0.6f, h * 0.62f)
                            lineTo(w * 0.72f, h * 0.68f)
                        }
                        drawPath(mouth, Color.Black, style = Stroke(width = 3f))
                    }
                    Difficulty.INSANE -> {
                        // Fiery red eyes & teeth
                        val leftEye = Path().apply {
                            moveTo(w * 0.2f, h * 0.3f)
                            lineTo(w * 0.46f, h * 0.42f)
                            lineTo(w * 0.3f, h * 0.48f)
                            close()
                        }
                        val rightEye = Path().apply {
                            moveTo(w * 0.8f, h * 0.3f)
                            lineTo(w * 0.54f, h * 0.42f)
                            lineTo(w * 0.7f, h * 0.48f)
                            close()
                        }
                        drawPath(leftEye, Color(0xFFFFEB3B))
                        drawPath(rightEye, Color(0xFFFFEB3B))
                        drawRect(Color.Black, topLeft = Offset(w * 0.25f, h * 0.64f), size = Size(w * 0.5f, h * 0.16f))
                    }
                    Difficulty.DEMON -> {
                        // Demon horns
                        val leftHorn = Path().apply {
                            moveTo(w * 0.2f, h * 0.25f)
                            lineTo(w * 0.05f, h * 0.05f)
                            lineTo(w * 0.35f, h * 0.15f)
                            close()
                        }
                        val rightHorn = Path().apply {
                            moveTo(w * 0.8f, h * 0.25f)
                            lineTo(w * 0.95f, h * 0.05f)
                            lineTo(w * 0.65f, h * 0.15f)
                            close()
                        }
                        drawPath(leftHorn, Color(0xFFFF1744))
                        drawPath(rightHorn, Color(0xFFFF1744))

                        // Demon glowing eyes
                        val leftEye = Path().apply {
                            moveTo(w * 0.2f, h * 0.32f)
                            lineTo(w * 0.45f, h * 0.44f)
                            lineTo(w * 0.26f, h * 0.48f)
                            close()
                        }
                        val rightEye = Path().apply {
                            moveTo(w * 0.8f, h * 0.32f)
                            lineTo(w * 0.55f, h * 0.44f)
                            lineTo(w * 0.74f, h * 0.48f)
                            close()
                        }
                        drawPath(leftEye, Color.Cyan)
                        drawPath(rightEye, Color.Cyan)

                        // Demon fangs mouth
                        val mouth = Path().apply {
                            moveTo(w * 0.22f, h * 0.65f)
                            lineTo(w * 0.35f, h * 0.78f)
                            lineTo(w * 0.5f, h * 0.65f)
                            lineTo(w * 0.65f, h * 0.78f)
                            lineTo(w * 0.78f, h * 0.65f)
                            close()
                        }
                        drawPath(mouth, Color.Black)
                    }
                }
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = difficulty.displayName,
                color = Color(difficulty.colorHex),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Stars",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "$stars",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
