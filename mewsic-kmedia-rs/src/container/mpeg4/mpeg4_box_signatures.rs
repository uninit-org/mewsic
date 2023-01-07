use std::string::ToString;
use crate::tools::sig;
type Long = u64;
pub struct Mpeg4BoxSignatures {

}
impl Mpeg4BoxSignatures {
    pub const EXTENDED_TYPE: u64 = sig("uuid".to_string());

    /* ISO BMFF (standard) */

    pub const ADDITIONAL_METADATA_CONTAINER_BOX: u64 = sig("meco".to_string());
    pub const APPLE_LOSSLESS_BOX: u64 = sig("alac".to_string());
    pub const BINARY_XML_BOX: u64 = sig("bxml".to_string());
    pub const BIT_RATE_BOX: u64 = sig("btrt".to_string());
    pub const CHAPTER_BOX: u64 = sig("chpl".to_string());
    pub const CHUNK_OFFSET_BOX: u64 = sig("stco".to_string());
    pub const CHUNK_LARGE_OFFSET_BOX: u64 = sig("co64".to_string());
    pub const CLEAN_APERTURE_BOX: u64 = sig("clap".to_string());
    pub const COMPACT_SAMPLE_SIZE_BOX: u64 = sig("stz2".to_string());
    pub const COMPOSITION_TIME_TO_SAMPLE_BOX: u64 = sig("ctts".to_string());
    pub const COPYRIGHT_BOX: u64 = sig("cprt".to_string());
    pub const DATA_ENTRY_URN_BOX: u64 = sig("urn ".to_string());
    pub const DATA_ENTRY_URL_BOX: u64 = sig("url ".to_string());
    pub const DATA_INFORMATION_BOX: u64 = sig("dinf".to_string());
    pub const DATA_REFERENCE_BOX: u64 = sig("dref".to_string());
    pub const DECODING_TIME_TO_SAMPLE_BOX: u64 = sig("stts".to_string());
    pub const DEGRADATION_PRIORITY_BOX: u64 = sig("stdp".to_string());
    pub const EDIT_BOX: u64 = sig("edts".to_string());
    pub const EDIT_LIST_BOX: u64 = sig("elst".to_string());
    pub const FD_ITEM_INFORMATION_BOX: u64 = sig("fiin".to_string());
    pub const FD_SESSION_GROUP_BOX: u64 = sig("segr".to_string());
    pub const FEC_RESERVOIR_BOX: u64 = sig("fecr".to_string());
    pub const FILE_PARTITION_BOX: u64 = sig("fpar".to_string());
    pub const FILE_TYPE_BOX: u64 = sig("ftyp".to_string());
    pub const FREE_SPACE_BOX: u64 = sig("free".to_string());
    pub const GROUP_ID_TO_NAME_BOX: u64 = sig("gitn".to_string());
    pub const HANDLER_BOX: u64 = sig("hdlr".to_string());
    pub const HINT_MEDIA_HEADER_BOX: u64 = sig("hmhd".to_string());
    pub const IPMP_CONTROL_BOX: u64 = sig("ipmc".to_string());
    pub const IPMP_INFO_BOX: u64 = sig("imif".to_string());
    pub const ITEM_INFORMATION_BOX: u64 = sig("iinf".to_string());
    pub const ITEM_INFORMATION_ENTRY: u64 = sig("infe".to_string());
    pub const ITEM_LOCATION_BOX: u64 = sig("iloc".to_string());
    pub const ITEM_PROTECTION_BOX: u64 = sig("ipro".to_string());
    pub const MEDIA_BOX: u64 = sig("mdia".to_string());
    pub const MEDIA_DATA_BOX: u64 = sig("mdat".to_string());
    pub const MEDIA_HEADER_BOX: u64 = sig("mdhd".to_string());
    pub const MEDIA_INFORMATION_BOX: u64 = sig("minf".to_string());
    pub const META_BOX: u64 = sig("meta".to_string());
    pub const META_BOX_RELATION_BOX: u64 = sig("mere".to_string());
    pub const MOVIE_BOX: u64 = sig("moov".to_string());
    pub const MOVIE_EXTENDS_BOX: u64 = sig("mvex".to_string());
    pub const MOVIE_EXTENDS_HEADER_BOX: u64 = sig("mehd".to_string());
    pub const MOVIE_FRAGMENT_BOX: u64 = sig("moof".to_string());
    pub const MOVIE_FRAGMENT_HEADER_BOX: u64 = sig("mfhd".to_string());
    pub const MOVIE_FRAGMENT_RANDOM_ACCESS_BOX: u64 = sig("mfra".to_string());
    pub const MOVIE_FRAGMENT_RANDOM_ACCESS_OFFSET_BOX: u64 = sig("mfro".to_string());
    pub const MOVIE_HEADER_BOX: u64 = sig("mvhd".to_string());
    pub const NERO_METADATA_TAGS_BOX: u64 = sig("tags".to_string());
    pub const NULL_MEDIA_HEADER_BOX: u64 = sig("nmhd".to_string());
    pub const ORIGINAL_FORMAT_BOX: u64 = sig("frma".to_string());
    pub const PADDING_BIT_BOX: u64 = sig("padb".to_string());
    pub const PARTITION_ENTRY: u64 = sig("paen".to_string());
    pub const PIXEL_ASPECT_RATIO_BOX: u64 = sig("pasp".to_string());
    pub const PRIMARY_ITEM_BOX: u64 = sig("pitm".to_string());
    pub const PROGRESSIVE_DOWNLOAD_INFORMATION_BOX: u64 = sig("pdin".to_string());
    pub const PROTECTION_SCHEME_INFORMATION_BOX: u64 = sig("sinf".to_string());
    pub const SAMPLE_DEPENDENCY_TYPE_BOX: u64 = sig("sdtp".to_string());
    pub const SAMPLE_DESCRIPTION_BOX: u64 = sig("stsd".to_string());
    pub const SAMPLE_GROUP_DESCRIPTION_BOX: u64 = sig("sgpd".to_string());
    pub const SAMPLE_SCALE_BOX: u64 = sig("stsl".to_string());
    pub const SAMPLE_SIZE_BOX: u64 = sig("stsz".to_string());
    pub const SAMPLE_TABLE_BOX: u64 = sig("stbl".to_string());
    pub const SAMPLE_TO_CHUNK_BOX: u64 = sig("stsc".to_string());
    pub const SAMPLE_TO_GROUP_BOX: u64 = sig("sbgp".to_string());
    pub const SCHEME_TYPE_BOX: u64 = sig("schm".to_string());
    pub const SCHEME_INFORMATION_BOX: u64 = sig("schi".to_string());
    pub const SHADOW_SYNC_SAMPLE_BOX: u64 = sig("stsh".to_string());
    pub const SKIP_BOX: u64 = sig("skip".to_string());
    pub const SOUND_MEDIA_HEADER_BOX: u64 = sig("smhd".to_string());
    pub const SUB_SAMPLE_INFORMATION_BOX: u64 = sig("subs".to_string());
    pub const SYNC_SAMPLE_BOX: u64 = sig("stss".to_string());
    pub const TRACK_BOX: u64 = sig("trak".to_string());
    pub const TRACK_EXTENDS_BOX: u64 = sig("trex".to_string());
    pub const TRACK_FRAGMENT_BOX: u64 = sig("traf".to_string());
    pub const TRACK_FRAGMENT_HEADER_BOX: u64 = sig("tfhd".to_string());
    pub const TRACK_FRAGMENT_RANDOM_ACCESS_BOX: u64 = sig("tfra".to_string());
    pub const TRACK_FRAGMENT_RUN_BOX: u64 = sig("trun".to_string());
    pub const TRACK_HEADER_BOX: u64 = sig("tkhd".to_string());
    pub const TRACK_REFERENCE_BOX: u64 = sig("tref".to_string());
    pub const TRACK_SELECTION_BOX: u64 = sig("tsel".to_string());
    pub const USER_DATA_BOX: u64 = sig("udta".to_string());
    pub const VIDEO_MEDIA_HEADER_BOX: u64 = sig("vmhd".to_string());
    pub const WIDE_BOX: u64 = sig("wide".to_string());
    pub const XML_BOX: u64 = sig("xml ".to_string());

    /* MP4 Extensions */

    pub const OBJECT_DESCRIPTOR_BOX: u64 = sig("iods".to_string());
    pub const SAMPLE_DEPENDENCY_BOX: u64 = sig("sdep".to_string());

    /* Metadata: id3 */

    pub const ID3_TAG_BOX: u64 = sig("id32".to_string());

    /* Metadata: iTunes */

    pub const ITUNES_META_LIST_BOX: u64 = sig("ilst".to_string());
    pub const CUSTOM_ITUNES_METADATA_BOX: u64 = sig("----".to_string());
    pub const ITUNES_METADATA_BOX: u64 = sig("data".to_string());
    pub const ITUNES_METADATA_NAME_BOX: u64 = sig("name".to_string());
    pub const ITUNES_METADATA_MEAN_BOX: u64 = sig("mean".to_string());
    pub const ALBUM_ARTIST_NAME_BOX: u64 = sig("aART".to_string());
    pub const ALBUM_ARTIST_SORT_BOX: u64 = sig("soaa ".to_string());
    pub const ALBUM_NAME_BOX: u64 = sig("©alb".to_string());
    pub const ALBUM_SORT_BOX: u64 = sig("soal".to_string());
    pub const ARTIST_NAME_BOX: u64 = sig("©ART".to_string());
    pub const ARTIST_SORT_BOX: u64 = sig("soar".to_string());
    pub const CATEGORY_BOX: u64 = sig("catg".to_string());
    pub const COMMENTS_BOX: u64 = sig("©cmt".to_string());
    pub const COMPILATION_PART_BOX: u64 = sig("cpil ".to_string());
    pub const COMPOSER_NAME_BOX: u64 = sig("©wrt".to_string());
    pub const COMPOSER_SORT_BOX: u64 = sig("soco".to_string());
    pub const COVER_BOX: u64 = sig("covr".to_string());
    pub const CUSTOM_GENRE_BOX: u64 = sig("©gen".to_string());
    pub const DESCRIPTION_BOX: u64 = sig("desc".to_string());
    pub const DISK_NUMBER_BOX: u64 = sig("disk".to_string());
    pub const ENCODER_NAME_BOX: u64 = sig("©enc".to_string());
    pub const ENCODER_TOOL_BOX: u64 = sig("©too".to_string());
    pub const EPISODE_GLOBAL_UNIQUE_ID_BOX: u64 = sig("egid".to_string());
    pub const GAPLESS_PLAYBACK_BOX: u64 = sig("pgap".to_string());
    pub const GENRE_BOX: u64 = sig("gnre".to_string());
    pub const GROUPING_BOX: u64 = sig("©grp".to_string());
    pub const HD_VIDEO_BOX: u64 = sig("hdvd".to_string());
    pub const ITUNES_PURCHASE_ACCOUNT_BOX: u64 = sig("apID".to_string());
    pub const ITUNES_ACCOUNT_TYPE_BOX: u64 = sig("akID".to_string());
    pub const ITUNES_CATALOGUE_ID_BOX: u64 = sig("cnID".to_string());
    pub const ITUNES_COUNTRY_CODE_BOX: u64 = sig("sfID".to_string());
    pub const KEYWORD_BOX: u64 = sig("keyw".to_string());
    pub const LONG_DESCRIPTION_BOX: u64 = sig("ldes".to_string());
    pub const LYRICS_BOX: u64 = sig("©lyr".to_string());
    pub const META_TYPE_BOX: u64 = sig("stik".to_string());
    pub const PODCAST_BOX: u64 = sig("pcst".to_string());
    pub const PODCAST_URL_BOX: u64 = sig("purl".to_string());
    pub const PURCHASE_DATE_BOX: u64 = sig("purd".to_string());
    pub const RATING_BOX: u64 = sig("rtng".to_string());
    pub const RELEASE_DATE_BOX: u64 = sig("©day".to_string());
    pub const REQUIREMENT_BOX: u64 = sig("©req".to_string());
    pub const TEMPO_BOX: u64 = sig("tmpo".to_string());
    pub const TRACK_NAME_BOX: u64 = sig("©nam".to_string());
    pub const TRACK_NUMBER_BOX: u64 = sig("trkn".to_string());
    pub const TRACK_SORT_BOX: u64 = sig("sonm".to_string());
    pub const TV_EPISODE_BOX: u64 = sig("tves".to_string());
    pub const TV_EPISODE_NUMBER_BOX: u64 = sig("tven".to_string());
    pub const TV_NETWORK_NAME_BOX: u64 = sig("tvnn".to_string());
    pub const TV_SEASON_BOX: u64 = sig("tvsn".to_string());
    pub const TV_SHOW_BOX: u64 = sig("tvsh".to_string());
    pub const TV_SHOW_SORT_BOX: u64 = sig("sosn".to_string());

    /* Metadata: 3GPP */

    pub const THREE_GPP_ALBUM_BOX: u64 = sig("albm".to_string());
    pub const THREE_GPP_AUTHOR_BOX: u64 = sig("auth".to_string());
    pub const THREE_GPP_CLASSIFICATION_BOX: u64 = sig("clsf".to_string());
    pub const THREE_GPP_DESCRIPTION_BOX: u64 = sig("dscp".to_string());
    pub const THREE_GPP_KEYWORDS_BOX: u64 = sig("kywd".to_string());
    pub const THREE_GPP_LOCATION_INFORMATION_BOX: u64 = sig("loci".to_string());
    pub const THREE_GPP_PERFORMER_BOX: u64 = sig("perf".to_string());
    pub const THREE_GPP_RECORDING_YEAR_BOX: u64 = sig("yrrc".to_string());
    pub const THREE_GPP_TITLE_BOX: u64 = sig("titl".to_string());

    /* Metadata: GoogleVideo / YouTube */

    pub const GOOGLE_HOST_HEADER_BOX: u64 = sig("gshh".to_string());
    pub const GOOGLE_PING_MESSAGE_BOX: u64 = sig("gspm".to_string());
    pub const GOOGLE_PING_URL_BOX: u64 = sig("gspu".to_string());
    pub const GOOGLE_SOURCE_DATA_BOX: u64 = sig("gssd".to_string());
    pub const GOOGLE_START_TIME_BOX: u64 = sig("gsst".to_string());
    pub const GOOGLE_TRACK_DURATION_BOX: u64 = sig("gstd".to_string());

    /* Sample Encoding Entries */

    pub const MP4V_SAMPLE_ENTRY: u64 = sig("mp4v".to_string());
    pub const H263_SAMPLE_ENTRY: u64 = sig("s263".to_string());
    pub const ENCRYPTED_VIDEO_SAMPLE_ENTRY: u64 = sig("encv".to_string());
    pub const AVC_SAMPLE_ENTRY: u64 = sig("avc1".to_string());
    pub const MP4A_SAMPLE_ENTRY: u64 = sig("mp4a".to_string());
    pub const AC3_SAMPLE_ENTRY: u64 = sig("ac-3".to_string());
    pub const EAC3_SAMPLE_ENTRY: u64 = sig("ec-3".to_string());
    pub const DRMS_SAMPLE_ENTRY: u64 = sig("drms".to_string());
    pub const AMR_SAMPLE_ENTRY: u64 = sig("samr".to_string());
    pub const AMR_WB_SAMPLE_ENTRY: u64 = sig("sawb".to_string());
    pub const EVRC_SAMPLE_ENTRY: u64 = sig("sevc".to_string());
    pub const QCELP_SAMPLE_ENTRY: u64 = sig("sqcp".to_string());
    pub const SMV_SAMPLE_ENTRY: u64 = sig("ssmv".to_string());
    pub const ENCRYPTED_AUDIO_SAMPLE_ENTRY: u64 = sig("enca".to_string());
    pub const MPEG_SAMPLE_ENTRY: u64 = sig("mp4s".to_string());
    pub const TEXT_METADATA_SAMPLE_ENTRY: u64 = sig("mett".to_string());
    pub const XML_METADATA_SAMPLE_ENTRY: u64 = sig("metx".to_string());
    pub const RTP_HINT_SAMPLE_ENTRY: u64 = sig("rtp ".to_string());
    pub const FD_HINT_SAMPLE_ENTRY: u64 = sig("fdp ".to_string());

    /* Codec Infos */

    pub const ESD_BOX: u64 = sig("esds".to_string());

    // Video Codecs

    pub const AC3_SPECIFIC_BOX: u64 = sig("dac3".to_string());
    pub const H263_SPECIFIC_BOX: u64 = sig("d263".to_string());

    // Audio Codecs

    pub const AVC_SPECIFIC_BOX: u64 = sig("avcC".to_string());
    pub const EAC3_SPECIFIC_BOX: u64 = sig("dec3".to_string());
    pub const AMR_SPECIFIC_BOX: u64 = sig("damr".to_string());
    pub const EVRC_SPECIFIC_BOX: u64 = sig("devc".to_string());
    pub const QCELP_SPECIFIC_BOX: u64 = sig("dqcp".to_string());
    pub const SMV_SPECIFIC_BOX: u64 = sig("dsmv".to_string());

    /* OMA Digital Rights Management */

    pub const OMA_ACCESS_UNIT_FORMAT_BOX: u64 = sig("odaf".to_string());
    pub const OMA_COMMON_HEADERS_BOX: u64 = sig("ohdr".to_string());
    pub const OMA_CONTENT_ID_BOX: u64 = sig("ccid".to_string());
    pub const OMA_CONTENT_OBJECT_BOX: u64 = sig("odda".to_string());
    pub const OMA_COVER_URI_BOX: u64 = sig("cvru".to_string());
    pub const OMA_DISCRETE_MEDIA_HEADERS_BOX: u64 = sig("odhe".to_string());
    pub const OMA_DRM_CONTAINER_BOX: u64 = sig("odrm".to_string());
    pub const OMA_ICON_URI_BOX: u64 = sig("icnu".to_string());
    pub const OMA_INFO_URL_BOX: u64 = sig("infu".to_string());
    pub const OMA_LYRICS_URI_BOX: u64 = sig("lrcu".to_string());
    pub const OMA_MUTABLE_DRM_INFORMATION_BOX: u64 = sig("mdri".to_string());
    pub const OMA_KEY_MANAGEMENT_BOX: u64 = sig("odkm".to_string());
    pub const OMA_RIGHTS_OBJECT_BOX: u64 = sig("odrb".to_string());
    pub const OMA_TRANSACTION_TRACKING_BOX: u64 = sig("odtt".to_string());

    /* iTunes DRM (FairPlay) */
    
    pub const FAIRPLAY_USER_ID_BOX: u64 = sig("user".to_string());
    pub const FAIRPLAY_USER_NAME_BOX: u64 = sig("name".to_string());
    pub const FAIRPLAY_USER_KEY_BOX: u64 = sig("key ".to_string());
    pub const FAIRPLAY_IV_BOX: u64 = sig("iviv".to_string());
    pub const FAIRPLAY_PRIVATE_KEY_BOX: u64 = sig("priv".to_string());

    pub const UNKNOWN_BOX: u64 = 0;
}

impl Mpeg4BoxSignatures {
    pub fn name_for(sig: u64) -> String {
        // reverse sig()
        let mut sig = sig;
        let mut name = String::new();
        for _ in 0..4 {
            name.push((sig & 0xff) as u8 as char);
            sig >>= 8;
        }
        name
    }
}