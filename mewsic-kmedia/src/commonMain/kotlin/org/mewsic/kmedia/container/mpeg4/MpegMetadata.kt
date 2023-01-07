package org.mewsic.kmedia.container.mpeg4

import org.mewsic.kmedia.api.ContainerMetadata

data class MpegMetadata(
    var bitrate: Int = -1
) : ContainerMetadata
