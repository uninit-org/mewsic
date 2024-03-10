package dev.uninit.mewsic.client.soundcloud

import dev.uninit.mewsic.client.common.platform.getPlatformHttpClient
import dev.uninit.mewsic.client.soundcloud.response.SoundCloudSearchResponse
import dev.uninit.mewsic.client.soundcloud.response.SoundCloudTrack
import dev.uninit.mewsic.client.soundcloud.response.exposed.SoundCloudPublicTrack
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class PublicSoundCloudClient : SoundCloudClient() {
    lateinit var clientId: String

    override fun isAuthenticated() = false

    override suspend fun getTrack(id: Int): SoundCloudPublicTrack? {
        val track = httpClient.get("https://api-v2.soundcloud.com/search/tracks") {
            parameter("client_id", clientId)
            parameter("ids", id)
        }.body<SoundCloudSearchResponse<SoundCloudPublicTrack>>().collection.firstOrNull()
        track?.initializeThumbnail()
        return track
    }

    override suspend fun searchTracks(query: String) = flow {
        val chunkSize = 50
        var offset = 0

        while (true) {
            val results = httpClient.get("https://api-v2.soundcloud.com/search/tracks") {
                parameter("q", query)
                parameter("client_id", clientId)
                parameter("limit", chunkSize)
                parameter("offset", offset)
            }.body<SoundCloudSearchResponse<SoundCloudPublicTrack>>()

            emitAll(results.collection.asFlow().onEach { it.initializeThumbnail() })

            if (results.collection.size < chunkSize) {
                break
            }

            offset += chunkSize
        }
    }

    override suspend fun getDownloadUrl(track: SoundCloudTrack): String {
        val selectedTrack = if (track is SoundCloudPublicTrack) {
            track
        } else {
            getTrack(track.id) ?: throw IllegalStateException("Could not find track")
        }

        val bestStream = selectedTrack.media.transcodings.maxBy {
            // Mostly score by `quality` (highest quality first)
            // Then score by `protocol` (hls > progressive)
            // Finally score by `mime_type` (audio/mpeg > audio/ogg)
            val qualityScore = (
                QUALITY_ORDER.indexOf(it.quality) * 100 +
                PROTOCOL_ORDER.indexOf(it.format.protocol) * 10 +
                MIMETYPE_ORDER.indexOf(it.format.mimeType)
            )

            qualityScore
        }

        val streamData = httpClient.get(bestStream.url) {
            parameter("client_id", clientId)
        }.body<JsonObject>()
        return streamData["url"]?.jsonPrimitive?.content ?: throw IllegalStateException("Could not find stream url")
    }

    override suspend fun getRelatedTracks(track: SoundCloudTrack) = flow {
        val chunkSize = 50
        var offset = 0

        while (true) {
            val related = httpClient.get("https://api-v2.soundcloud.com/tracks/${track.id}/related") {
                parameter("client_id", clientId)
                parameter("limit", chunkSize)
                parameter("offset", offset)
            }.body<SoundCloudSearchResponse<SoundCloudPublicTrack>>()

            emitAll(related.collection.asFlow().onEach { it.initializeThumbnail() })

            if (related.collection.size < chunkSize) {
                break
            }
            offset += chunkSize
        }
    }

    override suspend fun initialSetup() {
        val tempClient = getPlatformHttpClient()
        val soundCloudHtml = tempClient.get("https://soundcloud.com").bodyAsText()
        val lastScript = SCRIPT_REGEX.find(soundCloudHtml, soundCloudHtml.lastIndexOf("<script"))?.value ?: throw IllegalStateException("Could not find script")
        val scriptSource = tempClient.get(lastScript).bodyAsText()
        clientId = CLIENT_ID_REGEX.find(scriptSource)?.groupValues?.get(1) ?: throw IllegalStateException("Could not locate client id")
    }

    companion object {
        private val SCRIPT_REGEX = Regex("https://a-v2.sndcdn.com/assets/\\d+-[a-f0-9]+\\.js")
        private val CLIENT_ID_REGEX = Regex("[,{]client_id:\"([a-zA-Z0-9]+)\"")

        private val QUALITY_ORDER = arrayOf(
            "sq",
            "hq",
        )
        private val PROTOCOL_ORDER = arrayOf(
            "progressive",
            "hls",
        )
        private val MIMETYPE_ORDER = arrayOf(
            "audio/ogg; codecs=\"opus\"",
            "audio/mpeg",
        )
    }
}
