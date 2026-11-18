package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun MiniEqualizerIcon(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "mini_eq")
    val targets = listOf(0.9f, 0.55f, 1.0f, 0.7f)
    val durations = listOf(380, 520, 440, 340)

    val animatedFractions = targets.mapIndexed { index, maxVal ->
        val raw by transition.animateFloat(
            initialValue = 0.2f,
            targetValue = maxVal,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = durations[index], easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "mini_bar_$index"
        )
        val settled by animateFloatAsState(
            targetValue = if (isPlaying) raw else 0.25f,
            animationSpec = tween(durationMillis = 220),
            label = "mini_settled_$index"
        )
        settled
    }

    Canvas(modifier = modifier.size(width = 22.dp, height = 20.dp)) {
        val barCount = animatedFractions.size
        val spacing = size.width * 0.14f
        val barWidth = (size.width - spacing * (barCount - 1)) / barCount
        val brush = Brush.verticalGradient(listOf(NeonCyan, NeonPink))

        animatedFractions.forEachIndexed { index, fraction ->
            val barHeight = (size.height * fraction).coerceAtLeast(4f)
            val left = index * (barWidth + spacing)
            val top = size.height - barHeight
            drawRoundRect(
                brush = brush,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}

@Composable
fun DjEqualizerVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "dj_eq_visualizer")
    val barConfigs = listOf(
        0.45f to 420,
        0.75f to 360,
        0.55f to 480,
        0.95f to 310,
        0.65f to 450,
        1.00f to 290,
        0.80f to 390,
        1.00f to 330,
        0.70f to 460,
        0.90f to 350,
        0.55f to 410,
        0.80f to 370,
        0.50f to 490,
        0.40f to 430
    )

    val levels = barConfigs.mapIndexed { index, (peak, duration) ->
        val animated by transition.animateFloat(
            initialValue = 0.14f,
            targetValue = peak,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "eq_bar_$index"
        )
        val level by animateFloatAsState(
            targetValue = if (isPlaying) animated else 0.14f,
            animationSpec = tween(durationMillis = 280),
            label = "eq_settled_$index"
        )
        level
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth(0.86f)
                .height(44.dp)
        ) {
            val count = levels.size
            val gap = size.width * 0.028f
            val barWidth = (size.width - gap * (count - 1)) / count
            val gradient = Brush.horizontalGradient(
                colors = listOf(NeonCyan, NeonPurple, NeonPink)
            )

            levels.forEachIndexed { index, level ->
                val barHeight = (size.height * level).coerceAtLeast(6f)
                val x = index * (barWidth + gap)
                val y = (size.height - barHeight) / 2f
                drawRoundRect(
                    brush = gradient,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )
            }
        }
    }
}
