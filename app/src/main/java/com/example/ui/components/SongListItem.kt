package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.Song
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardDarkSurface
import com.example.ui.theme.CardElevatedSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonTikTokGradient

@Composable
fun SongListItem(
    song: Song,
    isCurrentSong: Boolean,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onSongClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (isCurrentSong) CardElevatedSurface else CardDarkSurface,
        label = "song_card_bg"
    )
    val cardShape = RoundedCornerShape(16.dp)
    val borderBrush = if (isCurrentSong) {
        NeonTikTokGradient
    } else {
        Brush.linearGradient(listOf(BorderSubtle, BorderSubtle))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(containerColor)
            .border(
                width = if (isCurrentSong) 1.5.dp else 1.dp,
                brush = borderBrush,
                shape = cardShape
            )
            .clickable(onClick = onSongClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("song_card_${song.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Track Index or Active Animated Equalizer
        Box(
            modifier = Modifier.width(30.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isCurrentSong) {
                MiniEqualizerIcon(isPlaying = isPlaying)
            } else {
                Text(
                    text = "%02d".format(song.trackNumber),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Thumbnail Cover
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .then(
                    if (isCurrentSong) {
                        Modifier.border(1.5.dp, NeonTikTokGradient, RoundedCornerShape(12.dp))
                    } else {
                        Modifier
                    }
                )
        ) {
            AsyncImage(
                model = song.coverUrl,
                contentDescription = stringResource(R.string.cd_album_cover, song.title),
                placeholder = painterResource(R.drawable.img_dj_cover_default),
                error = painterResource(R.drawable.img_dj_cover_default),
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Song Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isCurrentSong) NeonCyan else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isCurrentSong) NeonPink.copy(alpha = 0.18f)
                            else Color.White.copy(alpha = 0.06f)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = song.duration,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isCurrentSong) NeonPink else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Favorite Heart Button (minimum 48dp touch target)
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("favorite_button_${song.id}")
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = if (isFavorite) {
                    stringResource(R.string.cd_favorite_remove)
                } else {
                    stringResource(R.string.cd_favorite_add)
                },
                tint = if (isFavorite) NeonPink else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
