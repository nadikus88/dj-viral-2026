package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.djPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "dj_viral_preferences"
)

class FavoritesRepository(private val context: Context) {

    private val favoriteIdsKey = stringSetPreferencesKey("favorite_song_ids")

    val favoriteIds: Flow<Set<String>> = context.djPreferencesDataStore.data.map { preferences ->
        preferences[favoriteIdsKey] ?: emptySet()
    }

    suspend fun toggleFavorite(songId: String) {
        context.djPreferencesDataStore.edit { preferences ->
            val current = preferences[favoriteIdsKey] ?: emptySet()
            preferences[favoriteIdsKey] = if (songId in current) {
                current - songId
            } else {
                current + songId
            }
        }
    }
}
