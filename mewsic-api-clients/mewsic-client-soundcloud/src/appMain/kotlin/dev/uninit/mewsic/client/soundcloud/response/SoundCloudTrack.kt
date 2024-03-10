package dev.uninit.mewsic.client.soundcloud.response

import kotlinx.datetime.LocalDate

interface SoundCloudTrack {
    val id: Int
    val artist: String
    val title: String
    val thumbnailBase64: String?
    val duration: Long
    val releaseDate: LocalDate?
}
