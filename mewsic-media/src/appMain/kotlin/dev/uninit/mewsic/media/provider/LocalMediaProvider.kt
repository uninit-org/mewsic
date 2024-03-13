package dev.uninit.mewsic.media.provider

import dev.uninit.mewsic.media.playlist.MediaPlaylist
import dev.uninit.mewsic.media.track.FileMediaTrack
import dev.uninit.mewsic.media.track.MediaTrack
import kotlinx.coroutines.flow.flow
import java.io.File

class LocalMediaProvider(
    private val libraryPaths: List<File>
) : MediaProvider {
    override suspend fun allTracks() = flow {
        // TODO: Figure out sorting order? (filename, recently listened, etc)
        libraryPaths.forEach { libraryPath ->
            libraryPath.walkTopDown().forEach { file ->
                if (file.isFile) {
                    val mediaItem = FileMediaTrack(this@LocalMediaProvider, file)
                    emit(mediaItem)
                }
            }
        }
    }

    override suspend fun allPlaylists() = flow<MediaPlaylist> {
        // TODO: Locally stored playlists from a database/storage of some kind?

    }

    override suspend fun getRelatedTracks(track: MediaTrack) = flow {
        if (track is FileMediaTrack) {
            track.file.parentFile.walkTopDown().forEach { file ->
                if (file.isFile && file != track.file) {
                    val mediaItem = FileMediaTrack(this@LocalMediaProvider, file)
                    emit(mediaItem)
                }
            }
        }
    }
}
