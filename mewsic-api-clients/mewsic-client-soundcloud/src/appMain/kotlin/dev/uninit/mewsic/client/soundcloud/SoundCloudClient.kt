package dev.uninit.mewsic.client.soundcloud

import dev.uninit.mewsic.client.common.BaseClient
import dev.uninit.mewsic.client.soundcloud.response.SoundCloudTrack
import kotlinx.coroutines.flow.Flow

abstract class SoundCloudClient : BaseClient() {
    abstract fun isAuthenticated(): Boolean

    abstract suspend fun getTrack(id: Int): SoundCloudTrack?
    abstract suspend fun searchTracks(query: String): Flow<SoundCloudTrack>

    abstract suspend fun getDownloadUrl(track: SoundCloudTrack): String

    abstract suspend fun getRelatedTracks(track: SoundCloudTrack): Flow<SoundCloudTrack>
}
