package dev.uninit.mewsic.client.soundcloud.response.api

import dev.uninit.mewsic.client.soundcloud.response.SoundCloudUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SoundCloudAPIUser(
    @SerialName("avatar_url")
    val avatarUrl: String,
    val city: String,
    val country: String,
    @SerialName("created_at")
    val createdAt: String,
    val description: String,
    @SerialName("discogs_name")
    val discogsName: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("followers_count")
    val followersCount: Int,
    @SerialName("followings_count")
    val followingsCount: Int,
    @SerialName("full_name")
    val fullName: String,
    val id: Int,
    val kind: String,
    @SerialName("last_modified")
    val lastModified: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("likes_count")
    val likesCount: Int,
    val locale: String,
    val online: Boolean,
    val permalink: String,
    @SerialName("permalink_url")
    val permalinkUrl: String,
    val plan: String,
    @SerialName("playlist_count")
    val playlistCount: Int,
    @SerialName("primary_email_confirmed")
    val primaryEmailConfirmed: Boolean,
    @SerialName("private_playlists_count")
    val privatePlaylistsCount: Int,
    @SerialName("private_tracks_count")
    val privateTracksCount: Int,
    @SerialName("public_favorites_count")
    val publicFavoritesCount: Int,
    val quota: Quota,
    @SerialName("reposts_count")
    val repostsCount: Int,
    // TODO: Subscription type
//    val subscriptions: List<Subscription>,
    @SerialName("track_count")
    val trackCount: Int,
    @SerialName("upload_seconds_left")
    val uploadSecondsLeft: Int,
    val uri: String,
    val username: String,
    val website: String,
    @SerialName("website_title")
    val websiteTitle: String
) : SoundCloudUser {
    @Serializable
    data class Quota(
        @SerialName("unlimited_upload_quota")
        val unlimitedUploadQuota: Boolean,
        @SerialName("upload_seconds_used")
        val uploadSecondsUsed: Int,
        @SerialName("upload_seconds_left")
        val uploadSecondsLeft: Int
    )
}
