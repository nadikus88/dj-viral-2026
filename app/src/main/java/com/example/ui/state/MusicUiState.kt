package com.example.ui.state

import com.example.data.PlaylistData
import com.example.model.Song

enum class PlaylistTab {
    ALL_SONGS,
    FAVORITES
}

enum class RepeatModeOption {
    OFF,
    ALL,
    ONE
}

data class MusicUiState(
    val allSongs: List<Song> = PlaylistData.songs,
    val searchQuery: String = "",
    val selectedTab: PlaylistTab = PlaylistTab.ALL_SONGS,
    val favoriteIds: Set<String> = emptySet(),
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val shuffleEnabled: Boolean = false,
    val repeatMode: RepeatModeOption = RepeatModeOption.OFF,
    val errorMessage: String? = null
) {
    val favoriteCount: Int
        get() = favoriteIds.size

    val filteredSongs: List<Song>
        get() {
            val baseList = when (selectedTab) {
                PlaylistTab.ALL_SONGS -> allSongs
                PlaylistTab.FAVORITES -> allSongs.filter { it.id in favoriteIds }
            }
            val query = searchQuery.trim()
            if (query.isEmpty()) return baseList
            return baseList.filter { song ->
                song.title.contains(query, ignoreCase = true) ||
                    song.artist.contains(query, ignoreCase = true)
            }
        }

    val effectiveDurationMs: Long
        get() = when {
            durationMs > 0L -> durationMs
            currentSong != null -> currentSong.durationMs
            else -> 0L
        }

    val progressFraction: Float
        get() {
            val total = effectiveDurationMs
            if (total <= 0L) return 0f
            return (currentPositionMs.toFloat() / total.toFloat()).coerceIn(0f, 1f)
        }
}

fun formatDuration(ms: Long): String {
    if (ms <= 0L) return "00:00"
    val totalSeconds = ms / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%02d:%02d".format(minutes, seconds)
}
