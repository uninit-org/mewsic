package dev.uninit.mewsic.media.track

import dev.uninit.mewsic.media.stream.LibAVMediaStream
import dev.uninit.mewsic.media.stream.MediaStream

class UrlMediaTrack(
    private val url: String,
    artist: String = "Unknown",
    title: String = url,
    album: String? = null,
    albumArtist: String? = null,
    albumArtBase64: String? = null,
    albumTrack: Int = -1,
    albumTrackTotal: Int = -1,
    year: Int = -1,
    duration: Long = -1
) : MediaTrack {
    override var artist = artist
        private set
    override var title = title
        private set
    override var thumbnailBase64 = albumArtBase64
        private set
    override var album = album
        private set
    override var albumArtist = albumArtist
        private set
    override var albumTrack: Int? = albumTrack
        private set
    override var albumTrackTotal: Int? = albumTrackTotal
        private set
    override var year: Int? = year
        private set
    override var duration = duration
        private set

    init {
        val metadata = MediaTrack.collectMetadata(url)
        this.artist = metadata.artist ?: artist
        this.title = metadata.title ?: title
        this.album = metadata.album
        this.albumArtist = metadata.albumArtist
        this.thumbnailBase64 = metadata.albumArtBase64
        this.albumTrack = metadata.albumTrack ?: albumTrack
        this.albumTrackTotal = metadata.albumTrackTotal ?: albumTrackTotal
        this.year = metadata.year ?: year
        this.duration = metadata.duration ?: duration
    }

    override suspend fun createStream(): MediaStream {
        return LibAVMediaStream(url)
    }
}
