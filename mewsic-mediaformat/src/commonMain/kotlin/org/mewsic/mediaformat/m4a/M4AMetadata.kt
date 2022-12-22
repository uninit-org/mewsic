package org.mewsic.mediaformat.m4a

import org.mewsic.mediaformat.api.MediaMetadata

class M4AMetadata(
    override var title: String?,
    override var artist: String?,
    override var album: String?
) : MediaMetadata
