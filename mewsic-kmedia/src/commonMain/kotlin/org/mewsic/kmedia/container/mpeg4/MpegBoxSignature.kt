package org.mewsic.kmedia.container.mpeg4

import org.mewsic.kmedia.tools.longFromBytes
import org.mewsic.kmedia.tools.sig

enum class MpegBoxSignature(val signature: Long) {
    EXTENDED_TYPE(sig("uuid")),

    /* ISO BMFF (standard) */

    ADDITIONAL_METADATA_CONTAINER(sig("meco")),
    APPLE_LOSSLESS_AUDIO_CODEC(sig("alac")),
    BINARY_XML(sig("bxml")),
    BITRATE_BOX(sig("btrt")),
    CHAPTER_BOX(sig("chpl")),
    CHUNK_OFFSET_BOX(sig("stco")),
    CHUNK_OFFSET_64_BOX(sig("co64")),
    CLEAN_APERTURE_BOX(sig("clap")),
    COMPACT_SAMPLE_SIZE_BOX(sig("stz2")),
    COMPOSITION_TIME_TO_SAMPLE_BOX(sig("ctts")),
    COPYRIGHT_BOX(sig("cprt")),
    DATA_ENTRY_URN_BOX(sig("urn ")),
    DATA_ENTRY_URL_BOX(sig("url ")),
    DATA_INFORMATION_BOX(sig("dinf")),
    DATA_REFERENCE_BOX(sig("dref")),
    DECODING_TIME_TO_SAMPLE_BOX(sig("stts")),
    DEGRADATION_PRIORITY_BOX(sig("stdp")),
    EDIT_BOX(sig("edts")),
    EDIT_LIST_BOX(sig("elst")),
    FD_ITEM_INFORMATION_BOX(sig("fiin")),
    FD_SESSION_GROUP_BOX(sig("segr")),
    FEC_RESERVOIR_BOX(sig("fecr")),
    FILE_PARTITION_BOX(sig("fpar")),
    FILE_TYPE_BOX(sig("ftyp")),
    FREE_SPACE_BOX(sig("free")),
    GROUP_ID_TO_NAME_BOX(sig("gitn")),
    HANDLER_BOX(sig("hdlr")),
    HINT_MEDIA_HEADER_BOX(sig("hmhd")),
    IPMP_CONTROL_BOX(sig("ipmc")),
    IPMP_INFO_BOX(sig("imif")),
    ITEM_INFORMATION_BOX(sig("iinf")),
    ITEM_INFORMATION_ENTRY(sig("infe")),
    ITEM_LOCATION_BOX(sig("iloc")),
    ITEM_PROTECTION_BOX(sig("ipro")),
    MEDIA_BOX(sig("mdia")),
    MEDIA_DATA_BOX(sig("mdat")),
    MEDIA_HEADER_BOX(sig("mdhd")),
    MEDIA_INFORMATION_BOX(sig("minf")),
    META_BOX(sig("meta")),
    META_BOX_RELATION_BOX(sig("mere")),
    MOVIE_BOX(sig("moov")),
    MOVIE_EXTENDS_BOX(sig("mvex")),
    MOVIE_EXTENDS_HEADER_BOX(sig("mehd")),
    MOVIE_FRAGMENT_BOX(sig("moof")),
    MOVIE_FRAGMENT_HEADER_BOX(sig("mfhd")),
    MOVIE_FRAGMENT_RANDOM_ACCESS_BOX(sig("mfra")),
    MOVIE_FRAGMENT_RANDOM_ACCESS_OFFSET_BOX(sig("mfro")),
    MOVIE_HEADER_BOX(sig("mvhd")),
    NERO_METADATA_BOX(sig("tags")),
    NULL_MEDIA_HEADER_BOX(sig("nmhd")),
    ORIGINAL_FORMAT_BOX(sig("frma")),
    PADDING_BITS_BOX(sig("padb")),
    PARTITION_ENTRY_BOX(sig("paen")),
    PIXEL_ASPECT_RATIO_BOX(sig("pasp")),
    PRIMARY_ITEM_BOX(sig("pitm")),
    PROGRESSIVE_DOWNLOAD_INFO_BOX(sig("pdin")),
    PROTECTION_SCHEME_INFO_BOX(sig("sinf")),
    SAMPLE_DEPENDENCY_TYPE_BOX(sig("sdtp")),
    SAMPLE_DESCRIPTION_BOX(sig("stsd")),
    SAMPLE_GROUP_DESCRIPTION_BOX(sig("sgpd")),
    SAMPLE_SCALE_BOX(sig("stsl")),
    SAMPLE_SIZE_BOX(sig("stsz")),
    SAMPLE_TABLE_BOX(sig("stbl")),
    SAMPLE_TO_CHUNK_BOX(sig("stsc")),
    SAMPLE_TO_GROUP_BOX(sig("sbgp")),
    SCHEME_TYPE_BOX(sig("schm")),
    SCHEME_INFORMATION_BOX(sig("schi")),
    SHADOW_SYNC_BOX(sig("stsh")),
    SKIP_BOX(sig("skip")),
    SOUND_MEDIA_HEADER_BOX(sig("smhd")),
    SUB_SAMPLE_INFORMATION_BOX(sig("subs")),
    SYNC_SAMPLE_BOX(sig("stss")),
    TRACK_BOX(sig("trak")),
    TRACK_EXTENDS_BOX(sig("trex")),
    TRACK_FRAGMENT_BOX(sig("traf")),
    TRACK_FRAGMENT_HEADER_BOX(sig("tfhd")),
    TRACK_FRAGMENT_RANDOM_ACCESS_BOX(sig("tfra")),
    TRACK_FRAGMENT_RUN_BOX(sig("trun")),
    TRACK_HEADER_BOX(sig("tkhd")),
    TRACK_REFERENCE_BOX(sig("tref")),
    TRACK_SELECTION_BOX(sig("tsel")),
    USER_DATA_BOX(sig("udta")),
    VIDEO_MEDIA_HEADER_BOX(sig("vmhd")),
    WIDE_BOX(sig("wide")),
    XML_BOX(sig("xml ")),

    /* MP4 Extensions */

    OBJECT_DESCRIPTOR_BOX(sig("iods")),
    SAMPLE_DEPENDENCY_BOX(sig("sdep")),

    /* Metadata: id3 */

    ID3_TAG_BOX(sig("id32")),

    /* Metadata: iTunes */

    ITUNES_META_LIST_BOX(sig("ilst")),
    ITUNES_METADATA_ITEM_BOX(sig("----")),
    ITUNES_METADATA_BOX(sig("data")),
    ITUNES_METADATA_NAME_BOX(sig("name")),
    ITUNES_METADATA_MEAN_BOX(sig("mean")),
    ALBUM_ARTIST_NAME_BOX(sig("aART")),
    ALBUM_ARTIST_SORT_BOX(sig("soaa")),
    ALBUM_NAME_BOX(sig("©alb")),
    ALBUM_SORT_BOX(sig("soal")),
    ARTIST_NAME_BOX(sig("©ART")),
    ARTIST_SORT_BOX(sig("soar")),
    CATEGORY_BOX(sig("catg")),
    COMMENT_BOX(sig("©cmt")),
    COMPILATION_PART_BOX(sig("cpil")),
    COMPOSER_NAME_BOX(sig("©wrt")),
    COMPOSER_SORT_BOX(sig("soco")),
    COVER_BOX(sig("covr")),
    DISC_NUMBER_BOX(sig("disk")),
    ENCODER_NAME_BOX(sig("©enc")),
    ENCODER_TOOL_BOX(sig("©too")),
    EPISODE_GLOBAL_UNIQUE_ID_BOX(sig("egid")),
    GAPLESS_PLAYBACK_BOX(sig("pgap")),
    GENRE_BOX(sig("gnre")),
    GROUPING_BOX(sig("©grp")),
    HD_VIDEO_BOX(sig("hdvd")),
    ITUNES_PURCHASE_ACCOUNT_BOX(sig("apID")),
    ITUNES_ACCOUNT_TYPE_BOX(sig("akID")),
    ITUNES_CATALOGUE_ID_BOX(sig("cnID")),
    ITUNES_COUNTRY_CODE_BOX(sig("sfID")),
    KEYWORD_BOX(sig("keyw")),
    LONG_DESCRIPTION_BOX(sig("ldes")),
    LYRICS_BOX(sig("©lyr")),
    META_TYPE_BOX(sig("stik")),
    PODCAST_BOX(sig("pcst")),
    PODCAST_URL_BOX(sig("purl")),
    PURCHASE_DATE_BOX(sig("purd")),
    RATING_BOX(sig("rtng")),
    RELEASE_DATE_BOX(sig("©day")),
    REQUIREMENTS_BOX(sig("©req")),
    TEMPO_BOX(sig("tmpo")),
    TRACK_NAME_BOX(sig("©nam")),
    TRACK_NUMBER_BOX(sig("trkn")),
    TRACK_SORT_BOX(sig("sonm")),
    TV_EPISODE_BOX(sig("tves")),
    TV_EPISODE_NUMBER_BOX(sig("tven")),
    TV_NETWORK_BOX(sig("tvnn")),
    TV_SEASON_BOX(sig("tvsn")),
    TV_SHOW_BOX(sig("tvsh")),
    TV_SHOW_SORT_BOX(sig("sosn")),

    /* Metadata: 3GPP */

    THREE_GPP_ALBUM_BOX(sig("albm")),
    THREE_GPP_AUTHOR_BOX(sig("auth")),
    THREE_GPP_CLASSIFICATION_BOX(sig("clsf")),
    THREE_GPP_DESCRIPTION_BOX(sig("dscp")),
    THREE_GPP_KEYWORD_BOX(sig("kywd")),
    THREE_GPP_LOCATION_BOX(sig("loci")),
    THREE_GPP_PERFORMER_BOX(sig("perf")),
    THREE_GPP_RECORDING_YEAR_BOX(sig("yrrc")),
    THREE_GPP_TITLE_BOX(sig("titl")),

    /* Metadata: GoogleVideo / YouTube */

    GOOGLE_HOST_HEADER_BOX(sig("gshh")),
    GOOGLE_PING_MESSAGE_BOX(sig("gspm")),
    GOOGLE_PING_URL_BOX(sig("gspu")),
    GOOGLE_SOURCE_DATA_BOX(sig("gssd")),
    GOOGLE_START_TIME_BOX(sig("gsst")),
    GOOGLE_TRACK_DURATION_BOX(sig("gstd")),

    /* Sample Encoding Entries */

    MP4V_SAMPLE_ENTRY(sig("mp4v")),
    H263_SAMPLE_ENTRY(sig("s263")),
    ENCRYPTED_VIDEO_SAMPLE_ENTRY(sig("encv")),
    AVC_SAMPLE_ENTRY(sig("avc1")),
    MP4A_SAMPLE_ENTRY(sig("mp4a")),
    AC3_SAMPLE_ENTRY(sig("ac-3")),
    EAC3_SAMPLE_ENTRY(sig("ec-3")),
    DRMS_SAMPLE_ENTRY(sig("drms")),
    AMR_SAMPLE_ENTRY(sig("samr")),
    AMR_WB_SAMPLE_ENTRY(sig("sawb")),
    EVRC_SAMPLE_ENTRY(sig("sevc")),
    QCELP_SAMPLE_ENTRY(sig("sqcp")),
    SMV_SAMPLE_ENTRY(sig("ssmv")),
    ENCRYPTED_AUDIO_SAMPLE_ENTRY(sig("enca")),
    MPEG_SAMPLE_ENTRY(sig("mp4s")),
    TEXT_METADATA_SAMPLE_ENTRY(sig("mett")),
    XML_METADATA_SAMPLE_ENTRY(sig("metx")),
    RTP_HINT_SAMPLE_ENTRY(sig("rtp ")),
    FD_HINT_SAMPLE_ENTRY(sig("fdp")),

    /* Codec Infos */

    ESD_BOX(sig("esds")),

    // Video Codecs

    H263_SPECIFIC_BOX(sig("d263")),
    AVC_SPECIFIC_BOX(sig("avcC")),

    // Audio Codecs

    AC3_SPECIFIC_BOX(sig("dac3")),
    EAC3_SPECIFIC_BOX(sig("dec3")),
    AMR_SPECIFIC_BOX(sig("damr")),
    EVRC_SPECIFIC_BOX(sig("devc")),
    QCELP_SPECIFIC_BOX(sig("dqcp")),
    SMV_SPECIFIC_BOX(sig("dsmv")),

    /* OMA Digital Rights Management */

    OMA_ACCESS_UNIT_FORMAT_BOX(sig("odaf")),
    OMA_COMMON_HEADERS_BOX(sig("ohdr")),
    OMA_CONTENT_ID_BOX(sig("ccid")),
    OMA_CONTENT_OBJECT_BOX(sig("odda")),
    OMA_COVER_URI_BOX(sig("cvru")),
    OMA_DISCRETE_MEDIA_HEADERS_BOX(sig("odhe")),
    OMA_DRM_CONTAINER_BOX(sig("odrm")),
    OMA_ICON_URI_BOX(sig("icnu")),
    OMA_INFO_URL_BOX(sig("infu")),
    OMA_LYRICS_URI_BOX(sig("lrcu")),
    OMA_MUTABLE_DRM_INFORMATION_BOX(sig("mdri")),
    OMA_KEY_MANAGEMENT_BOX(sig("odkm")),
    OMA_RIGHTS_OBJECT_BOX(sig("odrb")),
    OMA_TRANSACTION_TRACKING_BOX(sig("odtt")),

    /* iTunes DRM (FairPlay) */

    FAIRPLAY_USER_ID_BOX(sig("user")),
    FAIRPLAY_USER_NAME_BOX(sig("name")),
    FAIRPLAY_USER_KEY_BOX(sig("key ")),
    FAIRPLAY_IV_BOX(sig("iviv")),
    FAIRPLAY_PRIVATE_KEY_BOX(sig("priv")),

    UNKNOWN(0)
    ;
    companion object {
        fun fromSignature(signature: Long): MpegBoxSignature {
            return values().firstOrNull { it.signature == signature } ?: UNKNOWN
        }
        fun fromSignature(signature: String): MpegBoxSignature {
            return fromSignature(sig(signature))
        }
        fun fromSignature(signature: ByteArray): MpegBoxSignature {
            return fromSignature(longFromBytes(signature))
        }
    }

}

