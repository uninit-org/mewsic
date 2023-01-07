package org.mewsic.kmedia.container.mpeg4

import org.mewsic.kmedia.api.ContainerMedia
import org.mewsic.kmedia.api.ContainerMetadata

class MPEG4Container : ContainerMedia {
    override var metadata: ContainerMetadata = MpegMetadata()

}
