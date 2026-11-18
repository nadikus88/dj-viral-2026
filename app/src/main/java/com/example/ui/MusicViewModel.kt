package com.example.ui

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.R
import com.example.data.FavoritesRepository
import com.example.data.PlaylistData
import com.example.model.Song
import com.example.player.PlaybackService
import com.example.ui.state.MusicUiState
import com.example.ui.state.PlaylistTab
import com.example.ui.state.RepeatModeOption
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val favoritesRepository = FavoritesRepository(application)

    private val _uiState = MutableStateFlow(MusicUiState())
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var positionPollingJob: Job? = null
    private var pendingSongToPlay: Song? = null
    private var hasUserInitiatedPlayback: Boolean = false

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.update { it.copy(isPlaying = isPlaying) }
            syncPositionState()
            if (isPlaying) {
                startPositionPolling()
            } else {
                stopPositionPolling()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val isBuffering = playbackState == Player.STATE_BUFFERING
            _uiState.update { state ->
                state.copy(
                    isBuffering = isBuffering,
                    errorMessage = if (playbackState == Player.STATE_READY) null else state.errorMessage
                )
            }
            syncPositionState()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            if (!hasUserInitiatedPlayback) return
            val song = mediaItem?.mediaId?.let { PlaylistData.findById(it) }
            _uiState.update { state ->
                state.copy(
                    currentSong = song ?: state.currentSong,
                    currentPositionMs = 0L,
                    durationMs = song?.durationMs ?: state.durationMs
                )
            }
            syncPositionState()
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _uiState.update { it.copy(shuffleEnabled = shuffleModeEnabled) }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            val mapped = when (repeatMode) {
                Player.REPEAT_MODE_ALL -> RepeatModeOption.ALL
                Player.REPEAT_MODE_ONE -> RepeatModeOption.ONE
                else -> RepeatModeOption.OFF
            }
            _uiState.update { it.copy(repeatMode = mapped) }
        }

        override fun onPlayerError(error: PlaybackException) {
            stopPositionPolling()
            val context = getApplication<Application>()
            val message = if (!isNetworkAvailable(context)) {
                context.getString(R.string.error_no_internet)
            } else {
                context.getString(R.string.error_playback_failed)
            }
            _uiState.update {
                it.copy(
                    isPlaying = false,
                    isBuffering = false,
                    errorMessage = message
                )
            }
        }
    }

    init {
        observeFavorites()
        connectMediaController()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesRepository.favoriteIds.collect { ids ->
                _uiState.update { it.copy(favoriteIds = ids) }
            }
        }
    }

    private fun connectMediaController() {
        val context = getApplication<Application>()
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java)
        )
        val future = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture = future
        future.addListener(
            {
                val controller = runCatching { future.get() }.getOrNull() ?: return@addListener
                mediaController = controller
                controller.addListener(playerListener)

                if (controller.mediaItemCount == 0) {
                    val mediaItems = PlaylistData.songs.map { it.toMediaItem() }
                    controller.setMediaItems(mediaItems, 0, 0L)
                } else {
                    // Reconnecting to an already active session
                    val activeMediaId = controller.currentMediaItem?.mediaId
                    val activeSong = activeMediaId?.let { PlaylistData.findById(it) }
                    val isAlreadyActive = controller.isPlaying ||
                        controller.playbackState == Player.STATE_READY ||
                        controller.playbackState == Player.STATE_BUFFERING ||
                        controller.currentPosition > 0L
                    if (isAlreadyActive && activeSong != null) {
                        hasUserInitiatedPlayback = true
                        _uiState.update {
                            it.copy(
                                currentSong = activeSong,
                                isPlaying = controller.isPlaying,
                                isBuffering = controller.playbackState == Player.STATE_BUFFERING,
                                shuffleEnabled = controller.shuffleModeEnabled,
                                repeatMode = when (controller.repeatMode) {
                                    Player.REPEAT_MODE_ALL -> RepeatModeOption.ALL
                                    Player.REPEAT_MODE_ONE -> RepeatModeOption.ONE
                                    else -> RepeatModeOption.OFF
                                }
                            )
                        }
                        syncPositionState()
                        if (controller.isPlaying) {
                            startPositionPolling()
                        }
                    }
                }

                pendingSongToPlay?.let { pending ->
                    pendingSongToPlay = null
                    playSong(pending)
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectTab(tab: PlaylistTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleFavorite(songId: String) {
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(songId)
        }
    }

    fun playSong(song: Song) {
        val context = getApplication<Application>()
        if (!isNetworkAvailable(context)) {
            _uiState.update {
                it.copy(
                    currentSong = song,
                    isPlaying = false,
                    isBuffering = false,
                    errorMessage = context.getString(R.string.error_no_internet)
                )
            }
            hasUserInitiatedPlayback = true
            return
        }

        val controller = mediaController
        if (controller == null) {
            pendingSongToPlay = song
            hasUserInitiatedPlayback = true
            _uiState.update {
                it.copy(
                    currentSong = song,
                    isBuffering = true,
                    errorMessage = null
                )
            }
            return
        }

        hasUserInitiatedPlayback = true
        if (controller.mediaItemCount == 0) {
            controller.setMediaItems(PlaylistData.songs.map { it.toMediaItem() })
        }

        val targetIndex = PlaylistData.songs.indexOfFirst { it.id == song.id }
            .coerceAtLeast(0)

        val sameTrack = controller.currentMediaItem?.mediaId == song.id &&
            controller.playbackState != Player.STATE_IDLE &&
            controller.playerError == null

        if (sameTrack) {
            if (controller.isPlaying) {
                controller.pause()
            } else {
                controller.play()
            }
            return
        }

        _uiState.update {
            it.copy(
                currentSong = song,
                isBuffering = true,
                currentPositionMs = 0L,
                durationMs = song.durationMs,
                errorMessage = null
            )
        }
        controller.seekTo(targetIndex, 0L)
        controller.prepare()
        controller.play()
    }

    fun togglePlayPause() {
        val controller = mediaController
        val activeSong = _uiState.value.currentSong ?: PlaylistData.songs.firstOrNull()
        if (controller == null) {
            if (activeSong != null) playSong(activeSong)
            return
        }

        if (!hasUserInitiatedPlayback && activeSong != null) {
            playSong(activeSong)
            return
        }

        if (controller.playerError != null || controller.playbackState == Player.STATE_IDLE) {
            retryCurrent()
            return
        }

        if (controller.isPlaying) {
            controller.pause()
        } else {
            val context = getApplication<Application>()
            if (!isNetworkAvailable(context) && controller.playbackState != Player.STATE_READY) {
                _uiState.update {
                    it.copy(errorMessage = context.getString(R.string.error_no_internet))
                }
                return
            }
            controller.play()
        }
    }

    fun playNext() {
        val controller = mediaController ?: return
        val context = getApplication<Application>()
        if (!isNetworkAvailable(context)) {
            _uiState.update { it.copy(errorMessage = context.getString(R.string.error_no_internet)) }
            return
        }

        hasUserInitiatedPlayback = true
        if (controller.hasNextMediaItem()) {
            controller.seekToNextMediaItem()
        } else if (PlaylistData.songs.isNotEmpty()) {
            controller.seekTo(0, 0L)
        }
        controller.prepare()
        controller.play()
    }

    fun playPrevious() {
        val controller = mediaController ?: return
        val context = getApplication<Application>()
        if (!isNetworkAvailable(context)) {
            _uiState.update { it.copy(errorMessage = context.getString(R.string.error_no_internet)) }
            return
        }

        hasUserInitiatedPlayback = true
        if (controller.currentPosition > 3_000L) {
            controller.seekTo(0L)
            return
        }
        if (controller.hasPreviousMediaItem()) {
            controller.seekToPreviousMediaItem()
        } else if (PlaylistData.songs.isNotEmpty()) {
            controller.seekTo(PlaylistData.songs.lastIndex, 0L)
        }
        controller.prepare()
        controller.play()
    }

    fun seekTo(positionMs: Long) {
        val controller = mediaController ?: return
        val clamped = positionMs.coerceIn(0L, _uiState.value.effectiveDurationMs.coerceAtLeast(1L))
        _uiState.update { it.copy(currentPositionMs = clamped) }
        controller.seekTo(clamped)
    }

    fun toggleShuffle() {
        val controller = mediaController ?: return
        controller.shuffleModeEnabled = !controller.shuffleModeEnabled
    }

    fun cycleRepeatMode() {
        val controller = mediaController ?: return
        val nextMode = when (controller.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        controller.repeatMode = nextMode
    }

    fun retryCurrent() {
        val context = getApplication<Application>()
        if (!isNetworkAvailable(context)) {
            _uiState.update {
                it.copy(errorMessage = context.getString(R.string.error_no_internet))
            }
            return
        }
        val controller = mediaController
        val song = _uiState.value.currentSong ?: PlaylistData.songs.firstOrNull()
        if (controller == null) {
            if (song != null) playSong(song)
            return
        }
        _uiState.update { it.copy(errorMessage = null, isBuffering = true) }
        if (controller.mediaItemCount == 0 && song != null) {
            controller.setMediaItems(PlaylistData.songs.map { it.toMediaItem() })
            val idx = PlaylistData.songs.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
            controller.seekTo(idx, _uiState.value.currentPositionMs)
        }
        controller.prepare()
        controller.play()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun startPositionPolling() {
        positionPollingJob?.cancel()
        positionPollingJob = viewModelScope.launch {
            while (isActive) {
                syncPositionState()
                delay(250L)
            }
        }
    }

    private fun stopPositionPolling() {
        positionPollingJob?.cancel()
        positionPollingJob = null
    }

    private fun syncPositionState() {
        val controller = mediaController ?: return
        if (!hasUserInitiatedPlayback) return
        val currentPos = controller.currentPosition.coerceAtLeast(0L)
        val rawDuration = controller.duration
        val validDuration = if (rawDuration > 0L) {
            rawDuration
        } else {
            _uiState.value.currentSong?.durationMs ?: 0L
        }
        _uiState.update {
            it.copy(
                currentPositionMs = currentPos,
                durationMs = validDuration
            )
        }
    }

    private fun Song.toMediaItem(): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setAlbumTitle(album)
            .setArtworkUri(Uri.parse(coverUrl))
            .build()

        return MediaItem.Builder()
            .setMediaId(id)
            .setUri(audioUrl)
            .setMediaMetadata(metadata)
            .build()
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return true
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onCleared() {
        stopPositionPolling()
        mediaController?.removeListener(playerListener)
        controllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController = null
        controllerFuture = null
        super.onCleared()
    }
}
