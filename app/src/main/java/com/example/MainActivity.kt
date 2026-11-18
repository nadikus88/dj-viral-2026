package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.MusicViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.theme.DjViralTheme

const val ROUTE_HOME = "home"
const val ROUTE_PLAYER = "player"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            DjViralTheme {
                DjViralApp()
            }
        }
    }
}

@Composable
fun DjViralApp(
    musicViewModel: MusicViewModel = viewModel()
) {
    val uiState by musicViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Playback continues regardless of notification permission outcome
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = ROUTE_HOME
    ) {
        composable(route = ROUTE_HOME) {
            HomeScreen(
                uiState = uiState,
                onSearchQueryChange = musicViewModel::updateSearchQuery,
                onSelectTab = musicViewModel::selectTab,
                onSongClick = { song ->
                    musicViewModel.playSong(song)
                },
                onToggleFavorite = musicViewModel::toggleFavorite,
                onMiniPlayerClick = {
                    if (uiState.currentSong != null) {
                        navController.navigate(ROUTE_PLAYER) {
                            launchSingleTop = true
                        }
                    }
                },
                onPlayPauseClick = musicViewModel::togglePlayPause,
                onNextClick = musicViewModel::playNext,
                onRetryError = musicViewModel::retryCurrent,
                onDismissError = musicViewModel::clearError
            )
        }

        composable(
            route = ROUTE_PLAYER,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(280)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(260)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(260)
                )
            }
        ) {
            PlayerScreen(
                uiState = uiState,
                onBackClick = { navController.popBackStack() },
                onToggleFavorite = musicViewModel::toggleFavorite,
                onPlayPauseClick = musicViewModel::togglePlayPause,
                onNextClick = musicViewModel::playNext,
                onPreviousClick = musicViewModel::playPrevious,
                onSeekTo = musicViewModel::seekTo,
                onToggleShuffle = musicViewModel::toggleShuffle,
                onCycleRepeat = musicViewModel::cycleRepeatMode,
                onRetryError = musicViewModel::retryCurrent,
                onDismissError = musicViewModel::clearError
            )
        }
    }
}
