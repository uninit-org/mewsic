package dev.uninit.mewsic.media.provider

import dev.uninit.mewsic.media.playlist.MediaPlaylist
import dev.uninit.mewsic.media.track.MediaTrack
import dev.uninit.mewsic.utils.searchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
sealed interface MediaProvider {
    suspend fun allTracks(): Flow<MediaTrack>
    suspend fun allPlaylists(): Flow<MediaPlaylist>

    // Naive implementation of search for providers that don't support it
    suspend fun searchTracks(query: String) = allTracks().searchQuery(query) { listOfNotNull(title, artist, album).toTypedArray() }
    suspend fun searchPlaylists(query: String) = allPlaylists().searchQuery(query) { arrayOf(name) }

    // Return an empty flow if the provider doesn't support this type of track
    suspend fun getRelatedTracks(track: MediaTrack): Flow<MediaTrack>
}
