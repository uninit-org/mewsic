package dev.uninit.mewsic.client.soundcloud.response.exposed

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
data class SoundCloudPublicTrack(
    @SerialName("artwork_url")
    val artworkUrl: String? = null,
    val caption: String? = null,
    val commentable: Boolean,
    @SerialName("comment_count")
    val commentCount: Int,
    @SerialName("created_at")
    val createdAt: String,
    val description: String? = null,
    val downloadable: Boolean,
    @SerialName("download_count")
    val downloadCount: Int,
    override val duration: Long,
    @SerialName("full_duration")
    val fullDuration: Int,
    @SerialName("embeddable_by")
    val embeddableBy: String? = null,
    val genre: String? = null,
    @SerialName("has_downloads_left")
    val hasDownloadsLeft: Boolean,
    override val id: Int,
    val kind: String,
    @SerialName("label_name")
    val labelName: String? = null,  // TODO: Might not be nullable
    @SerialName("last_modified")
    val lastModified: String,
    val license: String,
    @SerialName("likes_count")
    val likesCount: Int,
    val permalink: String,
    @SerialName("permalink_url")
    val permalinkUrl: String,
    @SerialName("playback_count")
    val playbackCount: Int,
    val public: Boolean,
    // TODO: Figure out what format this is
    // @SerialName("publisher_metadata")
    // val publisherMetadata: String? = null,
    @SerialName("purchase_title")
    val purchaseTitle: String? = null,
    @SerialName("purchase_url")
    val purchaseUrl: String? = null,
    @SerialName("release_date")
    val releaseDateInternal: String? = null,
    @SerialName("reposts_count")
    val repostsCount: Int? = null,
    @SerialName("secret_token")
    val secretToken: String? = null,
    val sharing: String,
    val state: String,
    val streamable: Boolean,
    @SerialName("tag_list")
    val tagList: String,
    override val title: String,
    @SerialName("track_format")
    val trackFormat: String,
    val uri: String,
    val urn: String,
    @SerialName("user_id")
    val userId: Int,
    val visuals: String? = null,
    @SerialName("waveform_url")
    val waveformUrl: String,
    @SerialName("display_date")
    val displayDate: String,
    val media: Media,
    @SerialName("station_urn")
    val stationUrn: String,
    @SerialName("station_permalink")
    val stationPermalink: String,
    @SerialName("track_authorization")
    val trackAuthorization: String,
    @SerialName("monetization_model")
    val monetizationModel: String,
    val policy: String,
    val user: SoundCloudPublicUser,
) : SoundCloudTrack {
    override val artist = user.username
    override var thumbnailBase64: String? = null
    override val releaseDate = releaseDateInternal?.let {
        DATETIME_FORMAT.matchEntire(it)?.destructured?.let { (year, month, day, hour, minute, second) ->
            LocalDate(year.toInt(), month.toInt(), day.toInt())
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun initializeThumbnail() {
        if (artworkUrl != null) {
            thumbnailBase64 = Base64.encode(getPlatformHttpClient().get(artworkUrl).bodyAsChannel().readRemaining().readBytes())
        }
    }

    @Serializable
    data class Media(
        val transcodings: List<Transcoding>,
    ) {
        @Serializable
        data class Transcoding(
            val url: String,
            val preset: String,
            val duration: Int,
            val snipped: Boolean,
            val format: Format,
            val quality: String,
        ) {
            @Serializable
            data class Format(
                val protocol: String,
                @SerialName("mime_type")
                val mimeType: String,
            )
        }
    }

    companion object {
        val DATETIME_FORMAT = Regex("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})Z")
    }
}
