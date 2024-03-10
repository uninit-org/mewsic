package dev.uninit.mewsic.client.soundcloud

import dev.uninit.mewsic.client.common.OAuthClient
import dev.uninit.mewsic.client.soundcloud.response.SoundCloudTrack
import dev.uninit.mewsic.client.soundcloud.response.api.SoundCloudAPITrack
import kotlinx.coroutines.flow.Flow

class AuthenticatedSoundCloudClient : SoundCloudClient(), OAuthClient {
    override fun isAuthenticated() = true

    override suspend fun getTrack(id: Int): SoundCloudAPITrack? {
        TODO("Not yet implemented")
    }

    override suspend fun searchTracks(query: String): Flow<SoundCloudAPITrack> {
        TODO("Not yet implemented")
    }

    override suspend fun getDownloadUrl(track: SoundCloudTrack): String {
        TODO("Not yet implemented")
    }

    override suspend fun getRelatedTracks(track: SoundCloudTrack): Flow<SoundCloudTrack> {
        TODO("Not yet implemented")
    }
}
