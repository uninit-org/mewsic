package dev.uninit.mewsic.client.soundcloud.response.api

import dev.uninit.mewsic.client.common.platform.getPlatformHttpClient
import dev.uninit.mewsic.client.soundcloud.response.SoundCloudTrack
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.core.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class SoundCloudAPITrack(
    @SerialName("artwork_url")
    val artworkUrl: String? = null,
    @SerialName("comment_count")
    val commentCount: Int,
    val commentable: Boolean,
    @SerialName("created_at")
    val createdAt: String,
    val description: String? = null,
    @SerialName("download_count")
    val downloadCount: Int,
    @SerialName("download_url")
    val downloadUrl: String? = null,
    val downloadable: Boolean,
    override val duration: Long,
    // embeddable_by is deprecated
//    @SerialName("embeddable_by")
//    val embeddableBy: String,
    @SerialName("favoritings_count")
    val favoritingsCount: Int,
    val genre: String,
    override val id: Int,
    val isrc: String? = null,
    @SerialName("key_signature")
    val keySignature: String? = null,
    val kind: String,
    @SerialName("label_name")
    val labelName: String? = null,
    val license: String,
    @SerialName("permalink_url")
    val permalinkUrl: String,
    @SerialName("playback_count")
    val playbackCount: Int,
    @SerialName("purchase_title")
    val purchaseTitle: String? = null,
    @SerialName("purchase_url")
    val purchaseUrl: String? = null,
    val release: String? = null,
    @SerialName("release_day")
    val releaseDay: Int,
    @SerialName("release_month")
    val releaseMonth: Int,
    @SerialName("release_year")
    val releaseYear: Int,
    @SerialName("reposts_count")
    val repostsCount: Int,
    @SerialName("secret_uri")
    val secretUri: String? = null,
    @SerialName("sharing")
    val sharing: String,
    @SerialName("stream_url")
    val streamUrl: String,
    val streamable: Boolean,
    @SerialName("tag_list")
    val tagList: String,
    override val title: String,
    val uri: String,
    val user: SoundCloudAPIUser,
    @SerialName("user_favorite")
    val userFavorite: Boolean,
    @SerialName("user_playback_count")
    val userPlaybackCount: Int,
    @SerialName("waveform_url")
    val waveformUrl: String,
    val access: String,
) : SoundCloudTrack {
    override val artist = user.username
    override var thumbnailBase64: String? = null

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun initializeThumbnail() {
        if (artworkUrl != null) {
            thumbnailBase64 = Base64.encode(getPlatformHttpClient().get(artworkUrl).bodyAsChannel().readRemaining().readBytes())
        }
    }

    override val releaseDate = LocalDate(releaseYear, releaseMonth, releaseDay)
}
