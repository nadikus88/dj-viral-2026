package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.CardDarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonTikTokDiagonalGradient
import com.example.ui.theme.ObsidianBackground

@Composable
fun VinylDiscView(
    coverUrl: String,
    songTitle: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    discSize: Dp = 260.dp
) {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        var lastFrameNanos = 0L
        while (true) {
            val frameNanos = withFrameNanos { it }
            if (lastFrameNanos != 0L) {
                val deltaSeconds = (frameNanos - lastFrameNanos) / 1_000_000_000f
                val nextAngle = (rotation.value + deltaSeconds * 36f) % 360f
                rotation.snapTo(nextAngle)
            }
            lastFrameNanos = frameNanos
        }
    }

    Box(
        modifier = modifier.size(discSize + 36.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer Neon Glow Aura
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = if (isPlaying) 0.30f else 0.12f),
                        NeonPink.copy(alpha = if (isPlaying) 0.18f else 0.06f),
                        Color.Transparent
                    ),
                    radius = radius
                )
            )
        }

        // Rotating Vinyl Platter + Center Cover
        Box(
            modifier = Modifier
                .size(discSize)
                .graphicsLayer {
                    rotationZ = rotation.value
                }
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Vinyl grooves & specular shine
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1E1E28),
                            Color(0xFF0A0A0F),
                            Color(0xFF171722),
                            Color(0xFF09090D)
                        )
                    ),
                    radius = radius
                )

                // Sheen reflection sweep
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    ),
                    radius = radius
                )

                // Concentric vinyl grooves
                val grooveCount = 10
                val minGroove = radius * 0.56f
                val maxGroove = radius * 0.94f
                for (i in 0 until grooveCount) {
                    val r = minGroove + (maxGroove - minGroove) * (i / grooveCount.toFloat())
                    drawCircle(
                        color = Color.White.copy(alpha = if (i % 2 == 0) 0.07f else 0.03f),
                        radius = r,
                        style = Stroke(width = 1.2f)
                    )
                }

                // Neon outer edge ring
                drawCircle(
                    brush = NeonTikTokDiagonalGradient,
                    radius = radius - 2f,
                    style = Stroke(width = 3.5f)
                )
            }

            // Circular Album Cover Artwork in Center
            AsyncImage(
                model = coverUrl,
                contentDescription = stringResource(R.string.cd_album_cover, songTitle),
                placeholder = painterResource(R.drawable.img_dj_cover_default),
                error = painterResource(R.drawable.img_dj_cover_default),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(discSize * 0.48f)
                    .clip(CircleShape)
                    .border(2.5.dp, NeonTikTokDiagonalGradient, CircleShape)
            )

            // Center Vinyl Spindle Ring & Hole
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(CardDarkSurface)
                    .border(2.dp, NeonCyan.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(ObsidianBackground)
                )
            }
        }
    }
}
