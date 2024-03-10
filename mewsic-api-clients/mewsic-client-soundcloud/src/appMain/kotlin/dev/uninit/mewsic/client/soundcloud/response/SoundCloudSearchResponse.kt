package dev.uninit.mewsic.client.soundcloud.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SoundCloudSearchResponse<T>(
    val collection: List<T>,
    @SerialName("total_results")
    val totalResults: Int,
    @SerialName("next_href")
    val nextHref: String? = null,
    @SerialName("query_urn")
    val queryUrn: String,
)
