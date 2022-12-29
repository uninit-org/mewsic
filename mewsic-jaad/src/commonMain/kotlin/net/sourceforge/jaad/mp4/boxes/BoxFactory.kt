package net.sourceforge.jaad.mp4.boxes

import net.sourceforge.jaad.mp4.MP4InputStream

object BoxFactory : net.sourceforge.jaad.mp4.boxes.BoxTypes {
    private val LOGGER: java.util.logging.Logger = java.util.logging.Logger.getLogger("MP4 Boxes")

    init {
        for (h in LOGGER.getHandlers()) {
            LOGGER.removeHandler(h)
        }
        LOGGER.setLevel(java.util.logging.Level.WARNING)
        val h: java.util.logging.ConsoleHandler = java.util.logging.ConsoleHandler()
        h.setLevel(java.util.logging.Level.ALL)
        LOGGER.addHandler(h)
    }

    private val BOX_CLASSES: Map<Long, java.lang.Class<out net.sourceforge.jaad.mp4.boxes.BoxImpl>> =
        java.util.HashMap<Long, java.lang.Class<out net.sourceforge.jaad.mp4.boxes.BoxImpl>>()
    private val BOX_MULTIPLE_CLASSES: Map<Long, Array<java.lang.Class<out net.sourceforge.jaad.mp4.boxes.BoxImpl>>> =
        java.util.HashMap<Long, Array<java.lang.Class<out net.sourceforge.jaad.mp4.boxes.BoxImpl>>>()
    private val PARAMETER: MutableMap<Long, Array<String>> = java.util.HashMap<Long, Array<String>>()

    init {
        //classes
        BOX_CLASSES.put(ADDITIONAL_METADATA_CONTAINER_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(APPLE_LOSSLESS_BOX, net.sourceforge.jaad.mp4.boxes.impl.AppleLosslessBox::class.java)
        BOX_CLASSES.put(BINARY_XML_BOX, net.sourceforge.jaad.mp4.boxes.impl.BinaryXMLBox::class.java)
        BOX_CLASSES.put(BIT_RATE_BOX, net.sourceforge.jaad.mp4.boxes.impl.BitRateBox::class.java)
        BOX_CLASSES.put(CHAPTER_BOX, net.sourceforge.jaad.mp4.boxes.impl.ChapterBox::class.java)
        BOX_CLASSES.put(CHUNK_OFFSET_BOX, net.sourceforge.jaad.mp4.boxes.impl.ChunkOffsetBox::class.java)
        BOX_CLASSES.put(CHUNK_LARGE_OFFSET_BOX, net.sourceforge.jaad.mp4.boxes.impl.ChunkOffsetBox::class.java)
        BOX_CLASSES.put(CLEAN_APERTURE_BOX, net.sourceforge.jaad.mp4.boxes.impl.CleanApertureBox::class.java)
        BOX_CLASSES.put(COMPACT_SAMPLE_SIZE_BOX, net.sourceforge.jaad.mp4.boxes.impl.SampleSizeBox::class.java)
        BOX_CLASSES.put(
            COMPOSITION_TIME_TO_SAMPLE_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.CompositionTimeToSampleBox::class.java
        )
        BOX_CLASSES.put(COPYRIGHT_BOX, net.sourceforge.jaad.mp4.boxes.impl.CopyrightBox::class.java)
        BOX_CLASSES.put(DATA_ENTRY_URN_BOX, net.sourceforge.jaad.mp4.boxes.impl.DataEntryUrnBox::class.java)
        BOX_CLASSES.put(DATA_ENTRY_URL_BOX, net.sourceforge.jaad.mp4.boxes.impl.DataEntryUrlBox::class.java)
        BOX_CLASSES.put(DATA_INFORMATION_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(DATA_REFERENCE_BOX, net.sourceforge.jaad.mp4.boxes.impl.DataReferenceBox::class.java)
        BOX_CLASSES.put(
            DECODING_TIME_TO_SAMPLE_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.DecodingTimeToSampleBox::class.java
        )
        BOX_CLASSES.put(
            DEGRADATION_PRIORITY_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.DegradationPriorityBox::class.java
        )
        BOX_CLASSES.put(EDIT_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(EDIT_LIST_BOX, net.sourceforge.jaad.mp4.boxes.impl.EditListBox::class.java)
        BOX_CLASSES.put(
            FD_ITEM_INFORMATION_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.fd.FDItemInformationBox::class.java
        )
        BOX_CLASSES.put(FD_SESSION_GROUP_BOX, net.sourceforge.jaad.mp4.boxes.impl.fd.FDSessionGroupBox::class.java)
        BOX_CLASSES.put(FEC_RESERVOIR_BOX, net.sourceforge.jaad.mp4.boxes.impl.fd.FECReservoirBox::class.java)
        BOX_CLASSES.put(FILE_PARTITION_BOX, net.sourceforge.jaad.mp4.boxes.impl.fd.FilePartitionBox::class.java)
        BOX_CLASSES.put(FILE_TYPE_BOX, net.sourceforge.jaad.mp4.boxes.impl.FileTypeBox::class.java)
        BOX_CLASSES.put(FREE_SPACE_BOX, net.sourceforge.jaad.mp4.boxes.impl.FreeSpaceBox::class.java)
        BOX_CLASSES.put(GROUP_ID_TO_NAME_BOX, net.sourceforge.jaad.mp4.boxes.impl.fd.GroupIDToNameBox::class.java)
        BOX_CLASSES.put(HANDLER_BOX, net.sourceforge.jaad.mp4.boxes.impl.HandlerBox::class.java)
        BOX_CLASSES.put(HINT_MEDIA_HEADER_BOX, net.sourceforge.jaad.mp4.boxes.impl.HintMediaHeaderBox::class.java)
        BOX_CLASSES.put(IPMP_CONTROL_BOX, net.sourceforge.jaad.mp4.boxes.impl.IPMPControlBox::class.java)
        BOX_CLASSES.put(IPMP_INFO_BOX, net.sourceforge.jaad.mp4.boxes.impl.IPMPInfoBox::class.java)
        BOX_CLASSES.put(ITEM_INFORMATION_BOX, net.sourceforge.jaad.mp4.boxes.impl.ItemInformationBox::class.java)
        BOX_CLASSES.put(ITEM_INFORMATION_ENTRY, net.sourceforge.jaad.mp4.boxes.impl.ItemInformationEntry::class.java)
        BOX_CLASSES.put(ITEM_LOCATION_BOX, net.sourceforge.jaad.mp4.boxes.impl.ItemLocationBox::class.java)
        BOX_CLASSES.put(ITEM_PROTECTION_BOX, net.sourceforge.jaad.mp4.boxes.impl.ItemProtectionBox::class.java)
        BOX_CLASSES.put(MEDIA_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(MEDIA_DATA_BOX, net.sourceforge.jaad.mp4.boxes.impl.MediaDataBox::class.java)
        BOX_CLASSES.put(MEDIA_HEADER_BOX, net.sourceforge.jaad.mp4.boxes.impl.MediaHeaderBox::class.java)
        BOX_CLASSES.put(MEDIA_INFORMATION_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(META_BOX, net.sourceforge.jaad.mp4.boxes.impl.MetaBox::class.java)
        BOX_CLASSES.put(META_BOX_RELATION_BOX, net.sourceforge.jaad.mp4.boxes.impl.MetaBoxRelationBox::class.java)
        BOX_CLASSES.put(MOVIE_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(MOVIE_EXTENDS_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(MOVIE_EXTENDS_HEADER_BOX, net.sourceforge.jaad.mp4.boxes.impl.MovieExtendsHeaderBox::class.java)
        BOX_CLASSES.put(MOVIE_FRAGMENT_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(
            MOVIE_FRAGMENT_HEADER_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.MovieFragmentHeaderBox::class.java
        )
        BOX_CLASSES.put(MOVIE_FRAGMENT_RANDOM_ACCESS_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(
            MOVIE_FRAGMENT_RANDOM_ACCESS_OFFSET_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.MovieFragmentRandomAccessOffsetBox::class.java
        )
        BOX_CLASSES.put(MOVIE_HEADER_BOX, net.sourceforge.jaad.mp4.boxes.impl.MovieHeaderBox::class.java)
        BOX_CLASSES.put(
            NERO_METADATA_TAGS_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.meta.NeroMetadataTagsBox::class.java
        )
        BOX_CLASSES.put(NULL_MEDIA_HEADER_BOX, net.sourceforge.jaad.mp4.boxes.FullBox::class.java)
        BOX_CLASSES.put(ORIGINAL_FORMAT_BOX, net.sourceforge.jaad.mp4.boxes.impl.OriginalFormatBox::class.java)
        BOX_CLASSES.put(PADDING_BIT_BOX, net.sourceforge.jaad.mp4.boxes.impl.PaddingBitBox::class.java)
        BOX_CLASSES.put(PARTITION_ENTRY, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(PIXEL_ASPECT_RATIO_BOX, net.sourceforge.jaad.mp4.boxes.impl.PixelAspectRatioBox::class.java)
        BOX_CLASSES.put(PRIMARY_ITEM_BOX, net.sourceforge.jaad.mp4.boxes.impl.PrimaryItemBox::class.java)
        BOX_CLASSES.put(
            PROGRESSIVE_DOWNLOAD_INFORMATION_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.ProgressiveDownloadInformationBox::class.java
        )
        BOX_CLASSES.put(PROTECTION_SCHEME_INFORMATION_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(
            SAMPLE_DEPENDENCY_TYPE_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.SampleDependencyTypeBox::class.java
        )
        BOX_CLASSES.put(SAMPLE_DESCRIPTION_BOX, net.sourceforge.jaad.mp4.boxes.impl.SampleDescriptionBox::class.java)
        BOX_CLASSES.put(
            SAMPLE_GROUP_DESCRIPTION_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.SampleGroupDescriptionBox::class.java
        )
        BOX_CLASSES.put(SAMPLE_SCALE_BOX, net.sourceforge.jaad.mp4.boxes.impl.SampleScaleBox::class.java)
        BOX_CLASSES.put(SAMPLE_SIZE_BOX, net.sourceforge.jaad.mp4.boxes.impl.SampleSizeBox::class.java)
        BOX_CLASSES.put(SAMPLE_TABLE_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(SAMPLE_TO_CHUNK_BOX, net.sourceforge.jaad.mp4.boxes.impl.SampleToChunkBox::class.java)
        BOX_CLASSES.put(SAMPLE_TO_GROUP_BOX, net.sourceforge.jaad.mp4.boxes.impl.SampleToGroupBox::class.java)
        BOX_CLASSES.put(SCHEME_TYPE_BOX, net.sourceforge.jaad.mp4.boxes.impl.SchemeTypeBox::class.java)
        BOX_CLASSES.put(SCHEME_INFORMATION_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(SHADOW_SYNC_SAMPLE_BOX, net.sourceforge.jaad.mp4.boxes.impl.ShadowSyncSampleBox::class.java)
        BOX_CLASSES.put(SKIP_BOX, net.sourceforge.jaad.mp4.boxes.impl.FreeSpaceBox::class.java)
        BOX_CLASSES.put(SOUND_MEDIA_HEADER_BOX, net.sourceforge.jaad.mp4.boxes.impl.SoundMediaHeaderBox::class.java)
        BOX_CLASSES.put(
            SUB_SAMPLE_INFORMATION_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.SubSampleInformationBox::class.java
        )
        BOX_CLASSES.put(SYNC_SAMPLE_BOX, net.sourceforge.jaad.mp4.boxes.impl.SyncSampleBox::class.java)
        BOX_CLASSES.put(TRACK_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(TRACK_EXTENDS_BOX, net.sourceforge.jaad.mp4.boxes.impl.TrackExtendsBox::class.java)
        BOX_CLASSES.put(TRACK_FRAGMENT_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(
            TRACK_FRAGMENT_HEADER_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.TrackFragmentHeaderBox::class.java
        )
        BOX_CLASSES.put(
            TRACK_FRAGMENT_RANDOM_ACCESS_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.TrackFragmentRandomAccessBox::class.java
        )
        BOX_CLASSES.put(TRACK_FRAGMENT_RUN_BOX, net.sourceforge.jaad.mp4.boxes.impl.TrackFragmentRunBox::class.java)
        BOX_CLASSES.put(TRACK_HEADER_BOX, net.sourceforge.jaad.mp4.boxes.impl.TrackHeaderBox::class.java)
        BOX_CLASSES.put(TRACK_REFERENCE_BOX, net.sourceforge.jaad.mp4.boxes.impl.TrackReferenceBox::class.java)
        BOX_CLASSES.put(TRACK_SELECTION_BOX, net.sourceforge.jaad.mp4.boxes.impl.TrackSelectionBox::class.java)
        BOX_CLASSES.put(USER_DATA_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(VIDEO_MEDIA_HEADER_BOX, net.sourceforge.jaad.mp4.boxes.impl.VideoMediaHeaderBox::class.java)
        BOX_CLASSES.put(WIDE_BOX, net.sourceforge.jaad.mp4.boxes.impl.FreeSpaceBox::class.java)
        BOX_CLASSES.put(XML_BOX, net.sourceforge.jaad.mp4.boxes.impl.XMLBox::class.java)
        BOX_CLASSES.put(OBJECT_DESCRIPTOR_BOX, net.sourceforge.jaad.mp4.boxes.impl.ObjectDescriptorBox::class.java)
        BOX_CLASSES.put(SAMPLE_DEPENDENCY_BOX, net.sourceforge.jaad.mp4.boxes.impl.SampleDependencyBox::class.java)
        BOX_CLASSES.put(ID3_TAG_BOX, net.sourceforge.jaad.mp4.boxes.impl.meta.ID3TagBox::class.java)
        BOX_CLASSES.put(ITUNES_META_LIST_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(CUSTOM_ITUNES_METADATA_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(ITUNES_METADATA_BOX, net.sourceforge.jaad.mp4.boxes.impl.meta.ITunesMetadataBox::class.java)
        BOX_CLASSES.put(
            ITUNES_METADATA_NAME_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.meta.ITunesMetadataNameBox::class.java
        )
        BOX_CLASSES.put(
            ITUNES_METADATA_MEAN_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.meta.ITunesMetadataMeanBox::class.java
        )
        BOX_CLASSES.put(ALBUM_ARTIST_NAME_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(ALBUM_ARTIST_SORT_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(ALBUM_NAME_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(ALBUM_SORT_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(ARTIST_NAME_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(ARTIST_SORT_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(CATEGORY_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(COMMENTS_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(COMPILATION_PART_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(COMPOSER_NAME_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(COMPOSER_SORT_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(COVER_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(CUSTOM_GENRE_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(DESCRIPTION_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(DISK_NUMBER_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(ENCODER_NAME_BOX, net.sourceforge.jaad.mp4.boxes.impl.meta.EncoderBox::class.java)
        BOX_CLASSES.put(ENCODER_TOOL_BOX, net.sourceforge.jaad.mp4.boxes.impl.meta.EncoderBox::class.java)
        BOX_CLASSES.put(EPISODE_GLOBAL_UNIQUE_ID_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(GAPLESS_PLAYBACK_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(GENRE_BOX, net.sourceforge.jaad.mp4.boxes.impl.meta.GenreBox::class.java)
        BOX_CLASSES.put(GROUPING_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(HD_VIDEO_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(ITUNES_PURCHASE_ACCOUNT_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(ITUNES_ACCOUNT_TYPE_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(ITUNES_CATALOGUE_ID_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(ITUNES_COUNTRY_CODE_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(KEYWORD_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(LONG_DESCRIPTION_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(LYRICS_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(META_TYPE_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(PODCAST_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(PODCAST_URL_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(PURCHASE_DATE_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(RATING_BOX, net.sourceforge.jaad.mp4.boxes.impl.meta.RatingBox::class.java)
        BOX_CLASSES.put(RELEASE_DATE_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(REQUIREMENT_BOX, net.sourceforge.jaad.mp4.boxes.impl.meta.RequirementBox::class.java)
        BOX_CLASSES.put(TEMPO_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(TRACK_NAME_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(TRACK_NUMBER_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(TRACK_SORT_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(TV_EPISODE_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(TV_EPISODE_NUMBER_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(TV_NETWORK_NAME_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(TV_SEASON_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(TV_SHOW_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(TV_SHOW_SORT_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(THREE_GPP_ALBUM_BOX, net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPAlbumBox::class.java)
        BOX_CLASSES.put(THREE_GPP_AUTHOR_BOX, net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPMetadataBox::class.java)
        BOX_CLASSES.put(
            THREE_GPP_CLASSIFICATION_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPMetadataBox::class.java
        )
        BOX_CLASSES.put(
            THREE_GPP_DESCRIPTION_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPMetadataBox::class.java
        )
        BOX_CLASSES.put(
            THREE_GPP_KEYWORDS_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPKeywordsBox::class.java
        )
        BOX_CLASSES.put(
            THREE_GPP_LOCATION_INFORMATION_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPLocationBox::class.java
        )
        BOX_CLASSES.put(
            THREE_GPP_PERFORMER_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPMetadataBox::class.java
        )
        BOX_CLASSES.put(
            THREE_GPP_RECORDING_YEAR_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPRecordingYearBox::class.java
        )
        BOX_CLASSES.put(THREE_GPP_TITLE_BOX, net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPMetadataBox::class.java)
        BOX_CLASSES.put(GOOGLE_HOST_HEADER_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(GOOGLE_PING_MESSAGE_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(GOOGLE_PING_URL_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(GOOGLE_SOURCE_DATA_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(GOOGLE_START_TIME_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(GOOGLE_TRACK_DURATION_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(
            MP4V_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.VideoSampleEntry::class.java
        )
        BOX_CLASSES.put(
            H263_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.VideoSampleEntry::class.java
        )
        BOX_CLASSES.put(
            ENCRYPTED_VIDEO_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.VideoSampleEntry::class.java
        )
        BOX_CLASSES.put(
            AVC_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.VideoSampleEntry::class.java
        )
        BOX_CLASSES.put(
            MP4A_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.AudioSampleEntry::class.java
        )
        BOX_CLASSES.put(
            AC3_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.AudioSampleEntry::class.java
        )
        BOX_CLASSES.put(
            EAC3_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.AudioSampleEntry::class.java
        )
        BOX_CLASSES.put(
            DRMS_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.AudioSampleEntry::class.java
        )
        BOX_CLASSES.put(
            AMR_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.AudioSampleEntry::class.java
        )
        BOX_CLASSES.put(
            AMR_WB_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.AudioSampleEntry::class.java
        )
        BOX_CLASSES.put(
            EVRC_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.AudioSampleEntry::class.java
        )
        BOX_CLASSES.put(
            QCELP_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.AudioSampleEntry::class.java
        )
        BOX_CLASSES.put(
            SMV_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.AudioSampleEntry::class.java
        )
        BOX_CLASSES.put(
            ENCRYPTED_AUDIO_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.AudioSampleEntry::class.java
        )
        BOX_CLASSES.put(
            MPEG_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.MPEGSampleEntry::class.java
        )
        BOX_CLASSES.put(
            TEXT_METADATA_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.TextMetadataSampleEntry::class.java
        )
        BOX_CLASSES.put(
            XML_METADATA_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.XMLMetadataSampleEntry::class.java
        )
        BOX_CLASSES.put(
            RTP_HINT_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.RTPHintSampleEntry::class.java
        )
        BOX_CLASSES.put(
            FD_HINT_SAMPLE_ENTRY,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.FDHintSampleEntry::class.java
        )
        BOX_CLASSES.put(ESD_BOX, net.sourceforge.jaad.mp4.boxes.impl.ESDBox::class.java)
        BOX_CLASSES.put(
            H263_SPECIFIC_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.H263SpecificBox::class.java
        )
        BOX_CLASSES.put(
            AVC_SPECIFIC_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.AVCSpecificBox::class.java
        )
        BOX_CLASSES.put(
            AC3_SPECIFIC_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.AC3SpecificBox::class.java
        )
        BOX_CLASSES.put(
            EAC3_SPECIFIC_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.EAC3SpecificBox::class.java
        )
        BOX_CLASSES.put(
            AMR_SPECIFIC_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.AMRSpecificBox::class.java
        )
        BOX_CLASSES.put(
            EVRC_SPECIFIC_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.EVRCSpecificBox::class.java
        )
        BOX_CLASSES.put(
            QCELP_SPECIFIC_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.QCELPSpecificBox::class.java
        )
        BOX_CLASSES.put(
            SMV_SPECIFIC_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.SMVSpecificBox::class.java
        )
        BOX_CLASSES.put(
            OMA_ACCESS_UNIT_FORMAT_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.oma.OMAAccessUnitFormatBox::class.java
        )
        BOX_CLASSES.put(OMA_COMMON_HEADERS_BOX, net.sourceforge.jaad.mp4.boxes.impl.oma.OMACommonHeadersBox::class.java)
        BOX_CLASSES.put(OMA_CONTENT_ID_BOX, net.sourceforge.jaad.mp4.boxes.impl.oma.OMAContentIDBox::class.java)
        BOX_CLASSES.put(OMA_CONTENT_OBJECT_BOX, net.sourceforge.jaad.mp4.boxes.impl.oma.OMAContentObjectBox::class.java)
        BOX_CLASSES.put(OMA_COVER_URI_BOX, net.sourceforge.jaad.mp4.boxes.impl.oma.OMAURLBox::class.java)
        BOX_CLASSES.put(
            OMA_DISCRETE_MEDIA_HEADERS_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.oma.OMADiscreteMediaHeadersBox::class.java
        )
        BOX_CLASSES.put(OMA_DRM_CONTAINER_BOX, net.sourceforge.jaad.mp4.boxes.FullBox::class.java)
        BOX_CLASSES.put(OMA_ICON_URI_BOX, net.sourceforge.jaad.mp4.boxes.impl.oma.OMAURLBox::class.java)
        BOX_CLASSES.put(OMA_INFO_URL_BOX, net.sourceforge.jaad.mp4.boxes.impl.oma.OMAURLBox::class.java)
        BOX_CLASSES.put(OMA_LYRICS_URI_BOX, net.sourceforge.jaad.mp4.boxes.impl.oma.OMAURLBox::class.java)
        BOX_CLASSES.put(OMA_MUTABLE_DRM_INFORMATION_BOX, net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java)
        BOX_CLASSES.put(OMA_KEY_MANAGEMENT_BOX, net.sourceforge.jaad.mp4.boxes.FullBox::class.java)
        BOX_CLASSES.put(OMA_RIGHTS_OBJECT_BOX, net.sourceforge.jaad.mp4.boxes.impl.oma.OMARightsObjectBox::class.java)
        BOX_CLASSES.put(
            OMA_TRANSACTION_TRACKING_BOX,
            net.sourceforge.jaad.mp4.boxes.impl.oma.OMATransactionTrackingBox::class.java
        )
        BOX_CLASSES.put(FAIRPLAY_USER_ID_BOX, FairPlayDataBox::class.java)
        BOX_CLASSES.put(FAIRPLAY_USER_NAME_BOX, FairPlayDataBox::class.java)
        BOX_CLASSES.put(FAIRPLAY_USER_KEY_BOX, FairPlayDataBox::class.java)
        BOX_CLASSES.put(FAIRPLAY_IV_BOX, FairPlayDataBox::class.java)
        BOX_CLASSES.put(FAIRPLAY_PRIVATE_KEY_BOX, FairPlayDataBox::class.java)
        //parameter
        PARAMETER[ADDITIONAL_METADATA_CONTAINER_BOX] =
            arrayOf("Additional Metadata Container Box")
        PARAMETER[DATA_INFORMATION_BOX] = arrayOf("Data Information Box")
        PARAMETER[EDIT_BOX] = arrayOf("Edit Box")
        PARAMETER[MEDIA_BOX] = arrayOf("Media Box")
        PARAMETER[MEDIA_INFORMATION_BOX] = arrayOf("Media Information Box")
        PARAMETER[MOVIE_BOX] = arrayOf("Movie Box")
        PARAMETER[MOVIE_EXTENDS_BOX] =
            arrayOf("Movie Extends Box")
        PARAMETER[MOVIE_FRAGMENT_BOX] = arrayOf("Movie Fragment Box")
        PARAMETER[MOVIE_FRAGMENT_RANDOM_ACCESS_BOX] = arrayOf("Movie Fragment Random Access Box")
        PARAMETER[NULL_MEDIA_HEADER_BOX] = arrayOf("Null Media Header Box")
        PARAMETER[PARTITION_ENTRY] = arrayOf("Partition Entry")
        PARAMETER[PROTECTION_SCHEME_INFORMATION_BOX] =
            arrayOf("Protection Scheme Information Box")
        PARAMETER[SAMPLE_TABLE_BOX] = arrayOf("Sample Table Box")
        PARAMETER[SCHEME_INFORMATION_BOX] = arrayOf("Scheme Information Box")
        PARAMETER[TRACK_BOX] = arrayOf("Track Box")
        PARAMETER[TRACK_FRAGMENT_BOX] =
            arrayOf("Track Fragment Box")
        PARAMETER[USER_DATA_BOX] = arrayOf("User Data Box")
        PARAMETER[ITUNES_META_LIST_BOX] = arrayOf("iTunes Meta List Box")
        PARAMETER[CUSTOM_ITUNES_METADATA_BOX] =
            arrayOf("Custom iTunes Metadata Box")
        PARAMETER[ALBUM_ARTIST_NAME_BOX] = arrayOf("Album Artist Name Box")
        PARAMETER[ALBUM_ARTIST_SORT_BOX] = arrayOf("Album Artist Sort Box")
        PARAMETER[ALBUM_NAME_BOX] =
            arrayOf("Album Name Box")
        PARAMETER[ALBUM_SORT_BOX] = arrayOf("Album Sort Box")
        PARAMETER[ARTIST_NAME_BOX] = arrayOf("Artist Name Box")
        PARAMETER[ARTIST_SORT_BOX] = arrayOf("Artist Sort Box")
        PARAMETER[CATEGORY_BOX] = arrayOf("Category Box")
        PARAMETER[COMMENTS_BOX] = arrayOf("Comments Box")
        PARAMETER[COMPILATION_PART_BOX] = arrayOf("Compilation Part Box")
        PARAMETER[COMPOSER_NAME_BOX] =
            arrayOf("Composer Name Box")
        PARAMETER[COMPOSER_SORT_BOX] = arrayOf("Composer Sort Box")
        PARAMETER[COVER_BOX] = arrayOf("Cover Box")
        PARAMETER[CUSTOM_GENRE_BOX] = arrayOf("Custom Genre Box")
        PARAMETER[DESCRIPTION_BOX] =
            arrayOf("Description Cover Box")
        PARAMETER[DISK_NUMBER_BOX] = arrayOf("Disk Number Box")
        PARAMETER[EPISODE_GLOBAL_UNIQUE_ID_BOX] =
            arrayOf("Episode Global Unique ID Box")
        PARAMETER[GAPLESS_PLAYBACK_BOX] = arrayOf("Gapless Playback Box")
        PARAMETER[GROUPING_BOX] = arrayOf("Grouping Box")
        PARAMETER[HD_VIDEO_BOX] = arrayOf("HD Video Box")
        PARAMETER[ITUNES_PURCHASE_ACCOUNT_BOX] =
            arrayOf("iTunes Purchase Account Box")
        PARAMETER[ITUNES_ACCOUNT_TYPE_BOX] = arrayOf("iTunes Account Type Box")
        PARAMETER[ITUNES_CATALOGUE_ID_BOX] = arrayOf("iTunes Catalogue ID Box")
        PARAMETER[ITUNES_COUNTRY_CODE_BOX] = arrayOf("iTunes Country Code Box")
        PARAMETER[KEYWORD_BOX] = arrayOf("Keyword Box")
        PARAMETER[LONG_DESCRIPTION_BOX] = arrayOf("Long Description Box")
        PARAMETER[LYRICS_BOX] = arrayOf("Lyrics Box")
        PARAMETER[META_TYPE_BOX] = arrayOf("Meta Type Box")
        PARAMETER[PODCAST_BOX] = arrayOf("Podcast Box")
        PARAMETER[PODCAST_URL_BOX] = arrayOf("Podcast URL Box")
        PARAMETER[PURCHASE_DATE_BOX] = arrayOf("Purchase Date Box")
        PARAMETER[RELEASE_DATE_BOX] =
            arrayOf("Release Date Box")
        PARAMETER[TEMPO_BOX] = arrayOf("Tempo Box")
        PARAMETER[TRACK_NAME_BOX] =
            arrayOf("Track Name Box")
        PARAMETER[TRACK_NUMBER_BOX] = arrayOf("Track Number Box")
        PARAMETER[TRACK_SORT_BOX] = arrayOf("Track Sort Box")
        PARAMETER[TV_EPISODE_BOX] = arrayOf("TV Episode Box")
        PARAMETER[TV_EPISODE_NUMBER_BOX] = arrayOf("TV Episode Number Box")
        PARAMETER[TV_NETWORK_NAME_BOX] = arrayOf("TV Network Name Box")
        PARAMETER[TV_SEASON_BOX] = arrayOf("TV Season Box")
        PARAMETER[TV_SHOW_BOX] = arrayOf("TV Show Box")
        PARAMETER[TV_SHOW_SORT_BOX] = arrayOf("TV Show Sort Box")
        PARAMETER[THREE_GPP_AUTHOR_BOX] = arrayOf("3GPP Author Box")
        PARAMETER[THREE_GPP_CLASSIFICATION_BOX] = arrayOf("3GPP Classification Box")
        PARAMETER[THREE_GPP_DESCRIPTION_BOX] =
            arrayOf("3GPP Description Box")
        PARAMETER[THREE_GPP_PERFORMER_BOX] = arrayOf("3GPP Performer Box")
        PARAMETER[THREE_GPP_TITLE_BOX] = arrayOf("3GPP Title Box")
        PARAMETER[GOOGLE_HOST_HEADER_BOX] =
            arrayOf("Google Host Header Box")
        PARAMETER[GOOGLE_PING_MESSAGE_BOX] =
            arrayOf("Google Ping Message Box")
        PARAMETER[GOOGLE_PING_URL_BOX] = arrayOf("Google Ping URL Box")
        PARAMETER[GOOGLE_SOURCE_DATA_BOX] = arrayOf("Google Source Data Box")
        PARAMETER[GOOGLE_START_TIME_BOX] = arrayOf("Google Start Time Box")
        PARAMETER[GOOGLE_TRACK_DURATION_BOX] = arrayOf("Google Track Duration Box")
        PARAMETER[MP4V_SAMPLE_ENTRY] =
            arrayOf("MPEG-4 Video Sample Entry")
        PARAMETER[H263_SAMPLE_ENTRY] =
            arrayOf("H263 Video Sample Entry")
        PARAMETER[ENCRYPTED_VIDEO_SAMPLE_ENTRY] = arrayOf("Encrypted Video Sample Entry")
        PARAMETER[AVC_SAMPLE_ENTRY] =
            arrayOf("AVC Video Sample Entry")
        PARAMETER[MP4A_SAMPLE_ENTRY] = arrayOf("MPEG- 4Audio Sample Entry")
        PARAMETER[AC3_SAMPLE_ENTRY] =
            arrayOf("AC-3 Audio Sample Entry")
        PARAMETER[EAC3_SAMPLE_ENTRY] =
            arrayOf("Extended AC-3 Audio Sample Entry")
        PARAMETER[DRMS_SAMPLE_ENTRY] = arrayOf("DRMS Audio Sample Entry")
        PARAMETER[AMR_SAMPLE_ENTRY] =
            arrayOf("AMR Audio Sample Entry")
        PARAMETER[AMR_WB_SAMPLE_ENTRY] =
            arrayOf("AMR-Wideband Audio Sample Entry")
        PARAMETER[EVRC_SAMPLE_ENTRY] = arrayOf("EVC Audio Sample Entry")
        PARAMETER[QCELP_SAMPLE_ENTRY] = arrayOf("QCELP Audio Sample Entry")
        PARAMETER[SMV_SAMPLE_ENTRY] = arrayOf("SMV Audio Sample Entry")
        PARAMETER[ENCRYPTED_AUDIO_SAMPLE_ENTRY] = arrayOf("Encrypted Audio Sample Entry")
        PARAMETER[OMA_COVER_URI_BOX] =
            arrayOf("OMA DRM Cover URI Box")
        PARAMETER[OMA_DRM_CONTAINER_BOX] = arrayOf("OMA DRM Container Box")
        PARAMETER[OMA_ICON_URI_BOX] =
            arrayOf("OMA DRM Icon URI Box")
        PARAMETER[OMA_INFO_URL_BOX] =
            arrayOf("OMA DRM Info URL Box")
        PARAMETER[OMA_LYRICS_URI_BOX] =
            arrayOf("OMA DRM Lyrics URI Box")
        PARAMETER[OMA_MUTABLE_DRM_INFORMATION_BOX] =
            arrayOf("OMA DRM Mutable DRM Information Box")
    }

    @Throws(java.io.IOException::class)
    fun parseBox(
        parent: net.sourceforge.jaad.mp4.boxes.Box?,
        `in`: MP4InputStream
    ): net.sourceforge.jaad.mp4.boxes.Box {
        val offset: Long = `in`.getOffset()
        var size: Long = `in`.readBytes(4)
        val type: Long = `in`.readBytes(4)
        if (size == 1L) size = `in`.readBytes(8)
        if (type == EXTENDED_TYPE) `in`.skipBytes(16)

        //error protection
        if (parent != null) {
            val parentLeft: Long = parent.getOffset() + parent.getSize() - offset
            if (size > parentLeft) throw java.io.IOException("error while decoding box '" + typeToString(type) + "' at offset " + offset + ": box too large for parent")
        }
        java.util.logging.Logger.getLogger("MP4 Boxes").finest(typeToString(type))
        val box: net.sourceforge.jaad.mp4.boxes.BoxImpl = forType(type, `in`.getOffset())
        box.setParams(parent, size, type, offset)
        box.decode(`in`)

        //if box doesn't contain data it only contains children
        val cl: java.lang.Class<*> = box.javaClass
        if (cl == net.sourceforge.jaad.mp4.boxes.BoxImpl::class.java || cl == net.sourceforge.jaad.mp4.boxes.FullBox::class.java) box.readChildren(
            `in`
        )

        //check bytes left
        val left: Long = box.getOffset() + box.getSize() - `in`.getOffset()
        if (left > 0 && box !is net.sourceforge.jaad.mp4.boxes.impl.MediaDataBox
            && box !is net.sourceforge.jaad.mp4.boxes.UnknownBox
            && box !is net.sourceforge.jaad.mp4.boxes.impl.FreeSpaceBox
        ) LOGGER.log(
            java.util.logging.Level.INFO, "bytes left after reading box {0}: left: {1}, offset: {2}", arrayOf<Any>(
                typeToString(type), left, `in`.getOffset()
            )
        ) else if (left < 0) LOGGER.log(
            java.util.logging.Level.SEVERE, "box {0} overread: {1} bytes, offset: {2}", arrayOf<Any>(
                typeToString(type), -left, `in`.getOffset()
            )
        )

        //if mdat found and no random access, don't skip
        if (box.getType() != MEDIA_DATA_BOX || `in`.hasRandomAccess()) `in`.skipBytes(left)
        return box
    }

    //TODO: remove usages
    @Throws(java.io.IOException::class)
    fun parseBox(
        `in`: MP4InputStream,
        boxClass: java.lang.Class<out net.sourceforge.jaad.mp4.boxes.BoxImpl?>
    ): net.sourceforge.jaad.mp4.boxes.Box? {
        val offset: Long = `in`.getOffset()
        var size: Long = `in`.readBytes(4)
        val type: Long = `in`.readBytes(4)
        if (size == 1L) size = `in`.readBytes(8)
        if (type == EXTENDED_TYPE) `in`.skipBytes(16)
        var box: net.sourceforge.jaad.mp4.boxes.BoxImpl? = null
        try {
            box = boxClass.newInstance()
        } catch (e: java.lang.InstantiationException) {
        } catch (e: java.lang.IllegalAccessException) {
        }
        if (box != null) {
            box.setParams(null, size, type, offset)
            box.decode(`in`)
            val left: Long = box.getOffset() + box.getSize() - `in`.getOffset()
            `in`.skipBytes(left)
        }
        return box
    }

    private fun forType(type: Long, offset: Long): net.sourceforge.jaad.mp4.boxes.BoxImpl {
        var box: net.sourceforge.jaad.mp4.boxes.BoxImpl? = null
        val l: Long = java.lang.Long.valueOf(type)
        if (BOX_CLASSES.containsKey(l)) {
            val cl: java.lang.Class<out net.sourceforge.jaad.mp4.boxes.BoxImpl>? = BOX_CLASSES[l]
            if (PARAMETER.containsKey(l)) {
                val s = PARAMETER[l]!!
                try {
                    val con: java.lang.reflect.Constructor<out net.sourceforge.jaad.mp4.boxes.BoxImpl> =
                        cl.getConstructor(
                            String::class.java
                        )
                    box = con.newInstance(s[0])
                } catch (e: java.lang.Exception) {
                    LOGGER.log(
                        java.util.logging.Level.SEVERE,
                        "BoxFactory: could not call constructor for " + typeToString(type),
                        e
                    )
                    box = net.sourceforge.jaad.mp4.boxes.UnknownBox()
                }
            } else {
                try {
                    box = cl.newInstance()
                } catch (e: java.lang.Exception) {
                    LOGGER.log(
                        java.util.logging.Level.SEVERE,
                        "BoxFactory: could not instantiate box " + typeToString(type),
                        e
                    )
                }
            }
        }
        if (box == null) {
            LOGGER.log(
                java.util.logging.Level.INFO, "BoxFactory: unknown box type: {0}; position: {1}", arrayOf<Any>(
                    typeToString(type), offset
                )
            )
            box = net.sourceforge.jaad.mp4.boxes.UnknownBox()
        }
        return box
    }

    fun typeToString(l: Long): String {
        val b = ByteArray(4)
        b[0] = (l shr 24 and 0xFFL).toByte()
        b[1] = (l shr 16 and 0xFFL).toByte()
        b[2] = (l shr 8 and 0xFFL).toByte()
        b[3] = (l and 0xFFL).toByte()
        return kotlin.String(b)
    }
}
