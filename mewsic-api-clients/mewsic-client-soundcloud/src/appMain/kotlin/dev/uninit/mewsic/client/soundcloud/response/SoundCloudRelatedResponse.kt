package dev.uninit.mewsic.client.soundcloud.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SoundCloudRelatedResponse<T>(
    val collection: List<T>,
    @SerialName("query_urn")
    val queryUrn: String,
    val variant: String,
)
