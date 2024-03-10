package dev.uninit.mewsic.media.track

import dev.uninit.mewsic.client.soundcloud.SoundCloudClient
import dev.uninit.mewsic.client.soundcloud.response.SoundCloudTrack
import dev.uninit.mewsic.media.stream.MediaStream

class SoundCloudMediaTrack(
    private val client: SoundCloudClient,
    val track: SoundCloudTrack
) : MediaTrack {
    override val artist = track.artist
    override val title = track.title
    override val thumbnailBase64 = track.thumbnailBase64
    override val album = null
    override val albumArtist = null
    override val albumTrack = null
    override val albumTrackTotal = null
    override val year = track.releaseDate?.year
    override val duration = track.duration

    override suspend fun createStream(): MediaStream {
        // TODO: Consider downloading and converting to an easier format to handle depending on duration?
        // TODO: Perhaps write a caching system for this?
        val url = client.getDownloadUrl(track)
        return UrlMediaTrack(url).createStream()
    }
}
