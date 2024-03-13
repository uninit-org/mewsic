package dev.uninit.mewsic.client.soundcloud.response.exposed

import dev.uninit.mewsic.client.soundcloud.response.SoundCloudUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SoundCloudPublicUser(
    @SerialName("avatar_url")
    val avatarUrl: String,
    val city: String? = null,
    @SerialName("comments_count")
    val commentsCount: Int? = null,
    @SerialName("country_code")
    val countryCode: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    // TODO: Subscriptions
//    @SerialName("creator_subscriptions")
//    val creatorSubscriptions: List<CreatorSubscription>,
//    @SerialName("creator_subscription")
//    val creatorSubscription: CreatorSubscription,
    val description: String? = null,
    @SerialName("followers_count")
    val followersCount: Int,
    @SerialName("followings_count")
    val followingsCount: Int? = null,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("groups_count")
    val groupsCount: Int? = null,
    val id: Int,
    val kind: String,
    @SerialName("last_modified")
    val lastModified: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("likes_count")
    val likesCount: Int? = null,
    @SerialName("playlist_likes_count")
    val playlistLikesCount: Int? = null,
    val permalink: String,
    @SerialName("permalink_url")
    val permalinkUrl: String,
    @SerialName("playlist_count")
    val playlistCount: Int? = null,
    @SerialName("reposts_count")
    val repostsCount: Int? = null,
    @SerialName("track_count")
    val trackCount: Int? = null,
    val uri: String,
    val urn: String,
    val username: String,
    val verified: Boolean,
//    val visuals: ...,
    val badges: Badges,
    @SerialName("station_urn")
    val stationUrn: String,
    @SerialName("station_permalink")
    val stationPermalink: String,
) : SoundCloudUser {
    @Serializable
    data class Badges(
        val pro: Boolean,
        @SerialName("pro_unlimited")
        val proUnlimited: Boolean,
        val verified: Boolean,
    )
}
