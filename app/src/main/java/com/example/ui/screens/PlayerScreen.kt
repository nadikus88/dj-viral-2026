package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.DjEqualizerVisualizer
import com.example.ui.components.VinylDiscView
import com.example.ui.state.MusicUiState
import com.example.ui.state.RepeatModeOption
import com.example.ui.state.formatDuration
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardElevatedSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonTikTokDiagonalGradient
import com.example.ui.theme.NeonTikTokGradient
import com.example.ui.theme.ObsidianBackground

@Composable
fun PlayerScreen(
    uiState: MusicUiState,
    onBackClick: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onRetryError: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val song = uiState.currentSong ?: uiState.allSongs.first()
    val isFavorite = song.id in uiState.favoriteIds
    val snackbarHostState = remember { SnackbarHostState() }
    val retryLabel = stringResource(R.string.action_retry)

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage ?: return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = retryLabel,
            duration = SnackbarDuration.Long
        )
        if (result == SnackbarResult.ActionPerformed) {
            onRetryError()
        } else {
            onDismissError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ObsidianBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(NeonCyan.copy(alpha = 0.16f), Color.Transparent),
                            center = Offset(size.width * 0.2f, size.height * 0.25f),
                            radius = size.minDimension * 0.7f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(NeonPink.copy(alpha = 0.14f), Color.Transparent),
                            center = Offset(size.width * 0.85f, size.height * 0.65f),
                            radius = size.minDimension * 0.7f
                        )
                    )
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(CircleShape)
                            .background(CardElevatedSurface.copy(alpha = 0.8f))
                            .testTag("player_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.now_playing_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan
                        )
                        Text(
                            text = song.album,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = { onToggleFavorite(song.id) },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(CircleShape)
                            .background(CardElevatedSurface.copy(alpha = 0.8f))
                            .testTag("player_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) {
                                Icons.Rounded.Favorite
                            } else {
                                Icons.Rounded.FavoriteBorder
                            },
                            contentDescription = if (isFavorite) {
                                stringResource(R.string.cd_favorite_remove)
                            } else {
                                stringResource(R.string.cd_favorite_add)
                            },
                            tint = if (isFavorite) NeonPink else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    val isWideLayout = maxWidth >= 600.dp

                    if (isWideLayout) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                VinylDiscView(
                                    coverUrl = song.coverUrl,
                                    songTitle = song.title,
                                    isPlaying = uiState.isPlaying,
                                    discSize = 240.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                DjEqualizerVisualizer(isPlaying = uiState.isPlaying)
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .widthIn(max = 480.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                PlayerTrackDetailsAndControls(
                                    title = song.title,
                                    artist = song.artist,
                                    uiState = uiState,
                                    onSeekTo = onSeekTo,
                                    onToggleShuffle = onToggleShuffle,
                                    onPreviousClick = onPreviousClick,
                                    onPlayPauseClick = onPlayPauseClick,
                                    onNextClick = onNextClick,
                                    onCycleRepeat = onCycleRepeat
                                )
                            }
                        }
                    } else {
                        val compactDiscSize = if (maxHeight < 620.dp) 210.dp else 255.dp
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))

                            VinylDiscView(
                                coverUrl = song.coverUrl,
                                songTitle = song.title,
                                isPlaying = uiState.isPlaying,
                                discSize = compactDiscSize
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            DjEqualizerVisualizer(isPlaying = uiState.isPlaying)

                            Spacer(modifier = Modifier.height(14.dp))

                            PlayerTrackDetailsAndControls(
                                title = song.title,
                                artist = song.artist,
                                uiState = uiState,
                                onSeekTo = onSeekTo,
                                onToggleShuffle = onToggleShuffle,
                                onPreviousClick = onPreviousClick,
                                onPlayPauseClick = onPlayPauseClick,
                                onNextClick = onNextClick,
                                onCycleRepeat = onCycleRepeat
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerTrackDetailsAndControls(
    title: String,
    artist: String,
    uiState: MusicUiState,
    onSeekTo: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onPreviousClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onCycleRepeat: () -> Unit
) {
    val totalDuration = uiState.effectiveDurationMs
    var isDragging by remember { mutableStateOf(false) }
    var dragPositionMs by remember { mutableStateOf(0L) }

    val displayedPositionMs = if (isDragging) dragPositionMs else uiState.currentPositionMs
    val sliderFraction = if (totalDuration > 0L) {
        (displayedPositionMs.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    // Track Metadata
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .basicMarquee()
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = artist,
            style = MaterialTheme.typography.titleMedium,
            color = NeonCyan,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Seek Slider + Time Labels
    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = sliderFraction,
            onValueChange = { fraction ->
                isDragging = true
                dragPositionMs = (fraction * totalDuration).toLong()
            },
            onValueChangeFinished = {
                isDragging = false
                onSeekTo(dragPositionMs)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("seek_slider"),
            colors = SliderDefaults.colors(
                thumbColor = NeonPink,
                activeTrackColor = NeonCyan,
                inactiveTrackColor = BorderSubtle
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(displayedPositionMs),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = if (uiState.isBuffering) {
                    stringResource(R.string.buffering_label)
                } else {
                    formatDuration(totalDuration)
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (uiState.isBuffering) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Playback Controls Row: Shuffle, Previous, Play/Pause, Next, Repeat
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = onToggleShuffle,
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("shuffle_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.Shuffle,
                contentDescription = stringResource(R.string.cd_shuffle),
                tint = if (uiState.shuffleEnabled) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(
            onClick = onPreviousClick,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(CardElevatedSurface)
                .testTag("prev_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.SkipPrevious,
                contentDescription = stringResource(R.string.cd_previous),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(30.dp)
            )
        }

        // Large Neon Gradient Play/Pause Button
        Box(
            modifier = Modifier
                .size(74.dp)
                .clip(CircleShape)
                .background(NeonTikTokDiagonalGradient)
                .border(2.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                .clickable(onClick = onPlayPauseClick)
                .testTag("play_pause_button"),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isBuffering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp),
                    color = ObsidianBackground,
                    strokeWidth = 3.dp
                )
            } else {
                Icon(
                    imageVector = if (uiState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (uiState.isPlaying) {
                        stringResource(R.string.cd_pause)
                    } else {
                        stringResource(R.string.cd_play)
                    },
                    tint = ObsidianBackground,
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        IconButton(
            onClick = onNextClick,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(CardElevatedSurface)
                .testTag("next_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.SkipNext,
                contentDescription = stringResource(R.string.cd_next),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(30.dp)
            )
        }

        IconButton(
            onClick = onCycleRepeat,
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("repeat_button")
        ) {
            val repeatActive = uiState.repeatMode != RepeatModeOption.OFF
            Icon(
                imageVector = if (uiState.repeatMode == RepeatModeOption.ONE) {
                    Icons.Rounded.RepeatOne
                } else {
                    Icons.Rounded.Repeat
                },
                contentDescription = stringResource(R.string.cd_repeat),
                tint = if (repeatActive) NeonPink else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Active Mode Status Pills
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusPill(
            text = if (uiState.shuffleEnabled) {
                stringResource(R.string.shuffle_on)
            } else {
                stringResource(R.string.shuffle_off)
            },
            active = uiState.shuffleEnabled,
            activeColor = NeonCyan
        )
        StatusPill(
            text = when (uiState.repeatMode) {
                RepeatModeOption.OFF -> stringResource(R.string.repeat_off)
                RepeatModeOption.ALL -> stringResource(R.string.repeat_all)
                RepeatModeOption.ONE -> stringResource(R.string.repeat_one)
            },
            active = uiState.repeatMode != RepeatModeOption.OFF,
            activeColor = NeonPink
        )
    }
}

@Composable
private fun StatusPill(
    text: String,
    active: Boolean,
    activeColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (active) activeColor.copy(alpha = 0.14f) else CardElevatedSurface)
            .border(
                width = 1.dp,
                color = if (active) activeColor.copy(alpha = 0.6f) else BorderSubtle,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (active) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
