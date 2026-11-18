package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicOff
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.Song
import com.example.ui.components.MiniPlayer
import com.example.ui.components.SongListItem
import com.example.ui.state.MusicUiState
import com.example.ui.state.PlaylistTab
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardDarkSurface
import com.example.ui.theme.CardElevatedSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonTikTokGradient
import com.example.ui.theme.ObsidianBackground

@Composable
fun HomeScreen(
    uiState: MusicUiState,
    onSearchQueryChange: (String) -> Unit,
    onSelectTab: (PlaylistTab) -> Unit,
    onSongClick: (Song) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onMiniPlayerClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onRetryError: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = if (uiState.currentSong != null) 92.dp else 12.dp)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .drawBehind {
                    // Top atmospheric neon radial glows
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(NeonCyan.copy(alpha = 0.14f), Color.Transparent),
                            center = Offset(size.width * 0.12f, size.height * 0.06f),
                            radius = size.minDimension * 0.65f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(NeonPink.copy(alpha = 0.12f), Color.Transparent),
                            center = Offset(size.width * 0.90f, size.height * 0.12f),
                            radius = size.minDimension * 0.55f
                        )
                    )
                },
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 720.dp)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                HomeHeader()

                Spacer(modifier = Modifier.height(16.dp))

                // Real-time Search Bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input"),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_placeholder),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = NeonCyan
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onSearchQueryChange("") },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("clear_search_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = stringResource(R.string.clear_search_desc),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardElevatedSurface,
                        unfocusedContainerColor = CardDarkSurface,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderSubtle,
                        cursorColor = NeonPink
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Filter Tabs: Semua Lagu (10) & Favorit Saya
                PlaylistFilterTabs(
                    selectedTab = uiState.selectedTab,
                    totalCount = uiState.allSongs.size,
                    favoriteCount = uiState.favoriteCount,
                    onSelectTab = onSelectTab
                )

                Spacer(modifier = Modifier.height(12.dp))

                val songs = uiState.filteredSongs
                if (songs.isEmpty()) {
                    EmptyPlaylistState(
                        isFavoritesTab = uiState.selectedTab == PlaylistTab.FAVORITES &&
                            uiState.searchQuery.isBlank(),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .testTag("song_list"),
                        contentPadding = PaddingValues(
                            top = 4.dp,
                            bottom = if (uiState.currentSong != null) 112.dp else 28.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = songs,
                            key = { it.id }
                        ) { song ->
                            val isCurrent = uiState.currentSong?.id == song.id
                            SongListItem(
                                song = song,
                                isCurrentSong = isCurrent,
                                isPlaying = isCurrent && uiState.isPlaying,
                                isFavorite = song.id in uiState.favoriteIds,
                                onSongClick = { onSongClick(song) },
                                onFavoriteClick = { onToggleFavorite(song.id) }
                            )
                        }
                    }
                }
            }

            // Sticky Bottom Mini Player
            AnimatedVisibility(
                visible = uiState.currentSong != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .widthIn(max = 720.dp)
            ) {
                uiState.currentSong?.let { activeSong ->
                    MiniPlayer(
                        song = activeSong,
                        isPlaying = uiState.isPlaying,
                        isBuffering = uiState.isBuffering,
                        progressFraction = uiState.progressFraction,
                        onPlayerClick = onMiniPlayerClick,
                        onPlayPauseClick = onPlayPauseClick,
                        onNextClick = onNextClick
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader() {
    val pulseTransition = rememberInfiniteTransition(label = "live_badge")
    val dotAlpha by pulseTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(CardElevatedSurface)
                    .border(1.dp, NeonCyan.copy(alpha = 0.45f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(NeonPink.copy(alpha = dotAlpha))
                )
                Text(
                    text = stringResource(R.string.header_badge),
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan
                )
            }

            Icon(
                imageVector = Icons.Rounded.GraphicEq,
                contentDescription = null,
                tint = NeonPink,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = stringResource(R.string.header_title),
            style = MaterialTheme.typography.displaySmall.copy(
                brush = NeonTikTokGradient
            )
        )

        Text(
            text = stringResource(R.string.header_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PlaylistFilterTabs(
    selectedTab: PlaylistTab,
    totalCount: Int,
    favoriteCount: Int,
    onSelectTab: (PlaylistTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDarkSurface)
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FilterTabChip(
            label = stringResource(R.string.tab_all_songs, totalCount),
            isSelected = selectedTab == PlaylistTab.ALL_SONGS,
            onClick = { onSelectTab(PlaylistTab.ALL_SONGS) },
            modifier = Modifier
                .weight(1f)
                .testTag("tab_all_songs")
        )
        FilterTabChip(
            label = stringResource(R.string.tab_favorites, favoriteCount),
            isSelected = selectedTab == PlaylistTab.FAVORITES,
            onClick = { onSelectTab(PlaylistTab.FAVORITES) },
            modifier = Modifier
                .weight(1f)
                .testTag("tab_favorites")
        )
    }
}

@Composable
private fun FilterTabChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(11.dp)
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .then(
                if (isSelected) {
                    Modifier
                        .background(CardElevatedSurface)
                        .border(1.3.dp, NeonTikTokGradient, shape)
                } else {
                    Modifier.background(Color.Transparent)
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyPlaylistState(
    isFavoritesTab: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(CardElevatedSurface)
                .border(1.dp, NeonPink.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFavoritesTab) {
                    Icons.Rounded.FavoriteBorder
                } else {
                    Icons.Rounded.MusicOff
                },
                contentDescription = null,
                tint = NeonPink,
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(
                if (isFavoritesTab) R.string.empty_favorites_title
                else R.string.empty_search_title
            ),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(
                if (isFavoritesTab) R.string.empty_favorites_subtitle
                else R.string.empty_search_subtitle
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
