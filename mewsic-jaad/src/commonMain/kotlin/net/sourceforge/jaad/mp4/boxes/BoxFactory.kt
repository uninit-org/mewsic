package net.sourceforge.jaad.mp4.boxes

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.impl.*
import net.sourceforge.jaad.mp4.boxes.impl.fd.*
import net.sourceforge.jaad.mp4.boxes.impl.meta.*
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.*
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.*
import net.sourceforge.jaad.mp4.boxes.impl.samplegroupentries.*
import net.sourceforge.jaad.mp4.boxes.impl.oma.*
import net.sourceforge.jaad.mp4.boxes.impl.drm.FairPlayDataBox
import org.mewsic.commons.lang.Log

object BoxFactory : BoxTypes() {

    @Throws(Exception::class)
    fun parseBox(
        parent: Box?,
        input: MP4InputStream
    ) : BoxImpl{
        val offset = input.getOffset()
        var size = input.readBytes(4)
        var type = input.readBytes(4)

        if (size == 1L) size = input.readBytes(8)
        if (type == EXTENDED_TYPE) input.skipBytes(16)
        if (parent != null) {
            val parentLeft: Long = parent.offset + parent.size - offset
            if (size > parentLeft) throw Exception("error while decoding box '" + typeToString(type) + "' at offset " + offset + ": box too large for parent")
        }
        var box = forType(type, input.getOffset())
        box.setParams(parent, size, type, offset)
        box.decode(input)
        val left: Long = box.offset + box.size - input.getOffset()
        if (left > 0 && box !is MediaDataBox
            && box !is UnknownBox
            && box !is FreeSpaceBox
        ) Log.info("bytes left after reading box ${typeToString(type)}: left: $left, offset: ${input.getOffset()}")
        else if (left < 0) Log.warn("Box ${typeToString(type)} overread: ${-left} bytes, offset: ${input.getOffset()}")
        if (box.type != MEDIA_DATA_BOX || input.hasRandomAccess()) input.skipBytes(left)
        return box
    }
    internal fun forType(type: Long, offset: Long): BoxImpl {
        Log.debug("Getting type for $type@$offset")
        val box: BoxImpl = when (type) {
            ADDITIONAL_METADATA_CONTAINER_BOX -> BoxImpl("Additional Metadata Container Box")
            APPLE_LOSSLESS_BOX -> AppleLosslessBox()
            BINARY_XML_BOX -> BinaryXMLBox()
            BIT_RATE_BOX -> BitRateBox()
            CHAPTER_BOX -> ChapterBox()
            CHUNK_OFFSET_BOX, CHUNK_LARGE_OFFSET_BOX -> ChunkOffsetBox()
            CLEAN_APERTURE_BOX -> CleanApertureBox()
            COMPACT_SAMPLE_SIZE_BOX -> SampleSizeBox()
            COMPOSITION_TIME_TO_SAMPLE_BOX -> CompositionTimeToSampleBox()
            COPYRIGHT_BOX -> CopyrightBox()
            DATA_ENTRY_URN_BOX -> DataEntryUrnBox()
            DATA_ENTRY_URL_BOX -> DataEntryUrlBox()
            DATA_INFORMATION_BOX -> BoxImpl("Data Information Box")
            DATA_REFERENCE_BOX -> DataReferenceBox()
            DECODING_TIME_TO_SAMPLE_BOX -> DecodingTimeToSampleBox()
            DEGRADATION_PRIORITY_BOX -> DegradationPriorityBox()
            EDIT_BOX -> BoxImpl("Edit Box")
            EDIT_LIST_BOX -> EditListBox()
            FD_ITEM_INFORMATION_BOX -> FDItemInformationBox()
            FD_SESSION_GROUP_BOX -> FDSessionGroupBox()
            FEC_RESERVOIR_BOX -> FECReservoirBox()
            FILE_PARTITION_BOX -> FilePartitionBox()
            FILE_TYPE_BOX -> FileTypeBox()
            FREE_SPACE_BOX -> FreeSpaceBox()
            GROUP_ID_TO_NAME_BOX -> GroupIDToNameBox()
            HANDLER_BOX -> HandlerBox()
            HINT_MEDIA_HEADER_BOX -> HintMediaHeaderBox()
            IPMP_CONTROL_BOX -> IPMPControlBox()
            IPMP_INFO_BOX -> IPMPInfoBox()
            ITEM_INFORMATION_BOX -> ItemInformationBox()
            ITEM_INFORMATION_ENTRY -> ItemInformationEntry()
            ITEM_LOCATION_BOX -> ItemLocationBox()
            ITEM_PROTECTION_BOX -> ItemProtectionBox()
            MEDIA_BOX -> BoxImpl("Media Box")
            MEDIA_DATA_BOX -> MediaDataBox()
            MEDIA_HEADER_BOX -> MediaHeaderBox()
            MEDIA_INFORMATION_BOX -> BoxImpl("Media Information Box")
            META_BOX -> MetaBox()
            META_BOX_RELATION_BOX -> MetaBoxRelationBox()
            MOVIE_BOX -> BoxImpl("Movie Box")
            MOVIE_EXTENDS_BOX -> BoxImpl("Movie Extends Box")
            MOVIE_EXTENDS_HEADER_BOX -> MovieExtendsHeaderBox()
            MOVIE_FRAGMENT_BOX -> BoxImpl("Movie Fragment Box")
            MOVIE_FRAGMENT_HEADER_BOX -> MovieFragmentHeaderBox()
            MOVIE_FRAGMENT_RANDOM_ACCESS_BOX -> BoxImpl("Movie Fragment Random Access Box")
            MOVIE_FRAGMENT_RANDOM_ACCESS_OFFSET_BOX -> MovieFragmentRandomAccessOffsetBox()
            MOVIE_HEADER_BOX -> MovieHeaderBox()
            NERO_METADATA_TAGS_BOX -> NeroMetadataTagsBox()
            NULL_MEDIA_HEADER_BOX -> BoxImpl("Null Media Header Box")
            ORIGINAL_FORMAT_BOX -> OriginalFormatBox()
            PADDING_BIT_BOX -> PaddingBitBox()
            PARTITION_ENTRY -> BoxImpl("Partition Entry")
            PIXEL_ASPECT_RATIO_BOX -> PixelAspectRatioBox()
            PRIMARY_ITEM_BOX -> PrimaryItemBox()
            PROGRESSIVE_DOWNLOAD_INFORMATION_BOX -> ProgressiveDownloadInformationBox()
            PROTECTION_SCHEME_INFORMATION_BOX -> BoxImpl("Protection Scheme Information Box")
            SAMPLE_DEPENDENCY_TYPE_BOX -> SampleDependencyTypeBox()
            SAMPLE_DESCRIPTION_BOX -> SampleDescriptionBox()
            SAMPLE_GROUP_DESCRIPTION_BOX -> SampleGroupDescriptionBox()
            SAMPLE_SCALE_BOX -> SampleScaleBox()
            SAMPLE_SIZE_BOX -> SampleSizeBox()
            SAMPLE_TABLE_BOX -> BoxImpl("Sample Table Box")
            SAMPLE_TO_CHUNK_BOX -> SampleToChunkBox()
            SAMPLE_TO_GROUP_BOX -> SampleToGroupBox()
            SCHEME_TYPE_BOX -> SchemeTypeBox()
            SCHEME_INFORMATION_BOX -> BoxImpl("Scheme Information Box")
            SHADOW_SYNC_SAMPLE_BOX -> ShadowSyncSampleBox()
            SKIP_BOX -> FreeSpaceBox()
            SOUND_MEDIA_HEADER_BOX -> SoundMediaHeaderBox()
            SUB_SAMPLE_INFORMATION_BOX -> SubSampleInformationBox()
            SYNC_SAMPLE_BOX -> SyncSampleBox()
            TRACK_BOX -> BoxImpl("Track Box")
            TRACK_EXTENDS_BOX -> TrackExtendsBox()
            TRACK_FRAGMENT_BOX -> BoxImpl("Track Fragment Box")
            TRACK_FRAGMENT_HEADER_BOX -> TrackFragmentHeaderBox()
            TRACK_FRAGMENT_RANDOM_ACCESS_BOX -> BoxImpl("Track Fragment Random Access Box")
            TRACK_HEADER_BOX -> TrackHeaderBox()
            TRACK_REFERENCE_BOX -> TrackReferenceBox()
            TRACK_SELECTION_BOX -> TrackSelectionBox()
            USER_DATA_BOX -> BoxImpl("User Data Box")
            VIDEO_MEDIA_HEADER_BOX -> VideoMediaHeaderBox()
            WIDE_BOX -> FreeSpaceBox()
            XML_BOX -> XMLBox()
            OBJECT_DESCRIPTOR_BOX -> ObjectDescriptorBox()
            SAMPLE_DEPENDENCY_BOX -> SampleDependencyBox()
            ID3_TAG_BOX -> ID3TagBox()
            ITUNES_METADATA_BOX -> ITunesMetadataBox()
            ITUNES_METADATA_NAME_BOX -> ITunesMetadataNameBox()
            ITUNES_METADATA_MEAN_BOX -> ITunesMetadataMeanBox()
            // TODO: Implement the following boxes
            // im pretty sure these are all the boxes that are not implemented
            ALBUM_ARTIST_NAME_BOX -> BoxImpl("Album Artist Name Box")
            ALBUM_ARTIST_SORT_BOX -> BoxImpl("Album Artist Sort Box")
            ALBUM_NAME_BOX -> BoxImpl("Album Name Box")
            ALBUM_SORT_BOX -> BoxImpl("Album Sort Box")
            ARTIST_NAME_BOX -> BoxImpl("Artist Name Box")
            ARTIST_SORT_BOX -> BoxImpl("Artist Sort Box")
            CATEGORY_BOX -> BoxImpl("Category Box")
            COMMENTS_BOX -> BoxImpl("Comment Box")
            COMPILATION_PART_BOX -> BoxImpl("Compilation Part Box")
            COMPOSER_NAME_BOX -> BoxImpl("Composer Name Box")
            COMPOSER_SORT_BOX -> BoxImpl("Composer Sort Box")
            COVER_BOX -> BoxImpl("Cover Box")
            CUSTOM_GENRE_BOX -> BoxImpl("Custom Genre Box")
            DESCRIPTION_BOX -> BoxImpl("Description Box")
            DISK_NUMBER_BOX -> BoxImpl("Disk Number Box")
            ENCODER_NAME_BOX -> BoxImpl("Encoder Name Box")
            ENCODER_TOOL_BOX -> BoxImpl("Encoder Tool Box")
            EPISODE_GLOBAL_UNIQUE_ID_BOX -> BoxImpl("Episode Global Unique ID Box")
            GAPLESS_PLAYBACK_BOX -> BoxImpl("Gapless Playback Box")
            GENRE_BOX -> BoxImpl("Genre Box")
            GROUPING_BOX -> BoxImpl("Grouping Box")
            HD_VIDEO_BOX -> BoxImpl("HD Video Box")
            ITUNES_PURCHASE_ACCOUNT_BOX -> BoxImpl("iTunes Purchase Account Box")
            ITUNES_ACCOUNT_TYPE_BOX -> BoxImpl("iTunes Account Type Box")
            ITUNES_CATALOGUE_ID_BOX -> BoxImpl("iTunes Catalogue ID Box")
            ITUNES_COUNTRY_CODE_BOX -> BoxImpl("iTunes Country Code Box")
            KEYWORD_BOX -> BoxImpl("Keyword Box")
            LONG_DESCRIPTION_BOX -> BoxImpl("Long Description Box")
            LYRICS_BOX -> BoxImpl("Lyrics Box")
            META_TYPE_BOX -> BoxImpl("Meta Type Box")
            PODCAST_BOX -> BoxImpl("Podcast Box")
            PODCAST_URL_BOX -> BoxImpl("Podcast URL Box")
            PURCHASE_DATE_BOX -> BoxImpl("Purchase Date Box")
            RATING_BOX -> BoxImpl("Rating Box")
            RELEASE_DATE_BOX -> BoxImpl("Release Date Box")
            REQUIREMENT_BOX -> BoxImpl("Requirement Box")
            TEMPO_BOX -> BoxImpl("Tempo Box")
            TRACK_NAME_BOX -> BoxImpl("Track Name Box")
            TRACK_NUMBER_BOX -> BoxImpl("Track Number Box")
            TRACK_SORT_BOX -> BoxImpl("Track Sort Box")
            TV_EPISODE_BOX -> BoxImpl("TV Episode Box")
            TV_EPISODE_NUMBER_BOX -> BoxImpl("TV Episode Number Box")
            TV_NETWORK_NAME_BOX -> BoxImpl("TV Network Name Box")
            TV_SEASON_BOX -> BoxImpl("TV Season Box")
            TV_SHOW_BOX -> BoxImpl("TV Show Box")
            TV_SHOW_SORT_BOX -> BoxImpl("TV Show Sort Box")
            THREE_GPP_ALBUM_BOX -> ThreeGPPAlbumBox()
            THREE_GPP_AUTHOR_BOX -> ThreeGPPMetadataBox("3GPP Author Box")
            THREE_GPP_CLASSIFICATION_BOX -> ThreeGPPMetadataBox("3GPP Classification Box")
            THREE_GPP_DESCRIPTION_BOX -> ThreeGPPMetadataBox("3GPP Description Box")
            THREE_GPP_KEYWORDS_BOX -> ThreeGPPKeywordsBox()
            THREE_GPP_LOCATION_INFORMATION_BOX -> ThreeGPPLocationBox()
            THREE_GPP_PERFORMER_BOX -> ThreeGPPMetadataBox("3GPP Performer Box")
            THREE_GPP_RECORDING_YEAR_BOX -> ThreeGPPRecordingYearBox()
            THREE_GPP_TITLE_BOX -> ThreeGPPMetadataBox("3GPP Title Box")
            GOOGLE_HOST_HEADER_BOX -> BoxImpl("Google Host Header Box")
            GOOGLE_PING_MESSAGE_BOX -> BoxImpl("Google Ping Message Box")
            GOOGLE_PING_URL_BOX -> BoxImpl("Google Ping URL Box")
            GOOGLE_SOURCE_DATA_BOX -> BoxImpl("Google Source Data Box")
            GOOGLE_START_TIME_BOX -> BoxImpl("Google Start Time Box")
            GOOGLE_TRACK_DURATION_BOX -> BoxImpl("Google Track Duration Box")
            MP4V_SAMPLE_ENTRY -> VideoSampleEntry("MPEG-4 Video Sample Entry")
            H263_SAMPLE_ENTRY -> VideoSampleEntry("H263 Video Sample Entry")
            ENCRYPTED_VIDEO_SAMPLE_ENTRY -> VideoSampleEntry("Encrypted Video Sample Entry")
            AVC_SAMPLE_ENTRY -> VideoSampleEntry("AVC Video Sample Entry")
            MP4A_SAMPLE_ENTRY -> AudioSampleEntry("MPEG- 4Audio Sample Entry")
            AC3_SAMPLE_ENTRY -> AudioSampleEntry("AC-3 Audio Sample Entry")
            EAC3_SAMPLE_ENTRY -> AudioSampleEntry("Extended AC-3 Audio Sample Entry")
            DRMS_SAMPLE_ENTRY -> AudioSampleEntry("DRMS Audio Sample Entry")
            AMR_SAMPLE_ENTRY -> AudioSampleEntry("AMR Audio Sample Entry")
            AMR_WB_SAMPLE_ENTRY -> AudioSampleEntry("AMR-Wideband Audio Sample Entry")
            EVRC_SAMPLE_ENTRY -> AudioSampleEntry("EVC Audio Sample Entry")
            QCELP_SAMPLE_ENTRY -> AudioSampleEntry("QCELP Audio Sample Entry")
            SMV_SAMPLE_ENTRY -> AudioSampleEntry("SMV Audio Sample Entry")
            ENCRYPTED_AUDIO_SAMPLE_ENTRY -> AudioSampleEntry("Encrypted Audio Sample Entry")
            MPEG_SAMPLE_ENTRY -> MPEGSampleEntry()
            TEXT_METADATA_SAMPLE_ENTRY -> TextMetadataSampleEntry()
            XML_METADATA_SAMPLE_ENTRY -> XMLMetadataSampleEntry()
            RTP_HINT_SAMPLE_ENTRY -> RTPHintSampleEntry()
            FD_HINT_SAMPLE_ENTRY -> FDHintSampleEntry()
            ESD_BOX -> ESDBox()
            H263_SPECIFIC_BOX -> H263SpecificBox()
            AVC_SPECIFIC_BOX -> AVCSpecificBox()
            AC3_SPECIFIC_BOX -> AC3SpecificBox()
            EAC3_SPECIFIC_BOX -> EAC3SpecificBox()
            AMR_SPECIFIC_BOX -> AMRSpecificBox()
            EVRC_SPECIFIC_BOX -> EVRCSpecificBox()
            QCELP_SPECIFIC_BOX -> QCELPSpecificBox()
            SMV_SPECIFIC_BOX -> SMVSpecificBox()
            OMA_ACCESS_UNIT_FORMAT_BOX -> OMAAccessUnitFormatBox()
            OMA_COMMON_HEADERS_BOX -> OMACommonHeadersBox()
            OMA_CONTENT_ID_BOX -> OMAContentIDBox()
            OMA_CONTENT_OBJECT_BOX -> OMAContentObjectBox()
            OMA_COVER_URI_BOX -> OMAURLBox("OMA DRM Cover URI Box")
            OMA_DISCRETE_MEDIA_HEADERS_BOX -> OMADiscreteMediaHeadersBox()
            OMA_DRM_CONTAINER_BOX -> FullBox("OMA DRM Container Box")
            OMA_ICON_URI_BOX -> OMAURLBox("OMA DRM Icon URI Box")
            OMA_INFO_URL_BOX -> OMAURLBox("OMA DRM Info URL Box")
            OMA_LYRICS_URI_BOX -> OMAURLBox("OMA DRM Lyrics URI Box")
            OMA_MUTABLE_DRM_INFORMATION_BOX -> BoxImpl("OMA DRM Mutable DRM Information Box")
            OMA_KEY_MANAGEMENT_BOX -> FullBox("OMA DRM Key Management Box")
            OMA_RIGHTS_OBJECT_BOX -> OMARightsObjectBox()
            OMA_TRANSACTION_TRACKING_BOX -> OMATransactionTrackingBox()
            FAIRPLAY_USER_ID_BOX -> FairPlayDataBox()
            FAIRPLAY_USER_NAME_BOX -> FairPlayDataBox()
            FAIRPLAY_USER_KEY_BOX -> FairPlayDataBox()
            FAIRPLAY_IV_BOX -> FairPlayDataBox()
            FAIRPLAY_PRIVATE_KEY_BOX -> FairPlayDataBox()

















            else -> UnknownBox()
        }
        Log.debug("Got type for $type@$offset: $box")
        return box
    }
    internal fun typeToString(l: Long): String {
        val b = ByteArray(4)
        b[0] = (l shr 24 and 0xFFL).toByte()
        b[1] = (l shr 16 and 0xFFL).toByte()
        b[2] = (l shr 8 and 0xFFL).toByte()
        b[3] = (l and 0xFFL).toByte()
        return b.decodeToString()
    }
}
