package dev.uninit.mewsic.media.provider

import dev.uninit.mewsic.client.soundcloud.SoundCloudClient
import dev.uninit.mewsic.media.playlist.MediaPlaylist
import dev.uninit.mewsic.media.track.MediaTrack
import dev.uninit.mewsic.media.track.SoundCloudMediaTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

class SoundCloudMediaProvider(private val client: SoundCloudClient) : MediaProvider {
    override suspend fun allTracks(): Flow<MediaTrack> {
        return client.searchTracks("").map {
            SoundCloudMediaTrack(client, it)
        }
    }

    override suspend fun searchTracks(query: String): Flow<MediaTrack> {
        return client.searchTracks(query).map {
            SoundCloudMediaTrack(client, it)
        }
    }

    override suspend fun allPlaylists(): Flow<MediaPlaylist> {
        return emptyFlow()
    }

    override suspend fun getRelatedTracks(track: MediaTrack): Flow<MediaTrack> {
        if (track is SoundCloudMediaTrack) {
            return client.getRelatedTracks(track.track).map {
                SoundCloudMediaTrack(client, it)
            }
        }
        return emptyFlow()
    }
}
