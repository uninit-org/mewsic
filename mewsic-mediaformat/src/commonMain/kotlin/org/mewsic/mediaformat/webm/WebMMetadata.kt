package org.mewsic.mediaformat.webm

import org.mewsic.mediaformat.api.MediaMetadata

class WebMMetadata(
    override var title: String?,
    override var artist: String?,
    override var album: String?
) : MediaMetadata
