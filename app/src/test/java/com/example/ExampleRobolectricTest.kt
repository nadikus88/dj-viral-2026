package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PlaylistData
import com.example.ui.state.MusicUiState
import com.example.ui.state.PlaylistTab
import com.example.ui.state.formatDuration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DJ Viral", appName)
    }

    @Test
    fun `playlist contains 10 dj songs`() {
        assertEquals(10, PlaylistData.songs.size)
        assertEquals("DJ Opus Remix", PlaylistData.songs.first().artist)
        assertEquals("DJ Pantura Bass", PlaylistData.songs.last().artist)
    }

    @Test
    fun `ui state filters by search query and favorites tab`() {
        val state = MusicUiState(
            searchQuery = "komang",
            favoriteIds = setOf("dj_01", "dj_04")
        )
        assertEquals(2, state.filteredSongs.size)

        val favState = state.copy(
            selectedTab = PlaylistTab.FAVORITES,
            searchQuery = "Opus"
        )
        assertEquals(1, favState.filteredSongs.size)
        assertEquals("dj_01", favState.filteredSongs.first().id)
        assertTrue(formatDuration(225_000L) == "03:45")
    }
}
