use std::collections::HashMap;
use crate::{error, warn};
use crate::api::stream::InputStream;
use crate::container::mpeg4::decode::MpegBox::{*};
use crate::container::mpeg4::Mpeg4BoxSignatures;
use crate::stream::DataInputStream;

pub enum MpegBox<'mpeg_life> {
    AppleLosslessBox {
        parent: Option<&'mpeg_life MpegBox<'mpeg_life>>,
        size: u64,
        box_type: u64,
        offset: u64,
        name: Option<String>,
        children: Option<Vec<&'mpeg_life MpegBox<'mpeg_life>>>,
        version: u32,
        flags: u64,


        max_sample_per_frame: u64,
        max_coded_frame_size: u64,
        bit_rate: u64,
        sample_rate: u64,
        sample_size: u32,
        history_mult: u32,
        initial_history: u32,
        k_modifier: u32,
        channels: u32,
    },
    ///
    /// BinaryXmlBox:
    /// When the primary data is in XML format and it is desired that the XML be
    /// stored directly in the meta-box, either the XmlBox or BinaryXmlBox may be
    /// used. The BinaryCmlBox may only be used when there is a single well-defined
    /// binarization of the XML for that defined format as defined by the handler
    ///
    BinaryXmlBox {
        parent: Option<&'mpeg_life MpegBox<'mpeg_life>>,
        size: u64,
        box_type: u64,
        offset: u64,
        name: Option<String>,
        children: Option<Vec<&'mpeg_life MpegBox<'mpeg_life>>>,
        version: u32,
        flags: u64,

        data: Vec<u8>,
    },
    BitRateBox {
        parent: Option<&'mpeg_life MpegBox<'mpeg_life>>,
        size: u64,
        box_type: u64,
        offset: u64,
        name: Option<String>,
        children: Option<Vec<&'mpeg_life MpegBox<'mpeg_life>>>,
        /// Size of the decoding buffer for the elementary stream in bytes
        decoding_buffer_size: u64,
        /// Maximum rate in bits per second that can be used to decode the elementary stream
        /// over any window of one second
        max_bitrate: u64,
        /// Average rate in bits per second over the entire elementary stream
        avg_bitrate: u64,
    },
    /// Defined in "Adobe Video File Format Specification v10,"
    /// the ChapterBox allows for a video to specify a list of chapters.
    /// These are mapped <u64, String> pairs, where the u64 is the time in milliseconds
    /// and the String is the chapter name.
    ChapterBox {
        parent: Option<&'mpeg_life MpegBox<'mpeg_life>>,
        size: u64,
        box_type: u64,
        offset: u64,
        name: Option<String>,
        children: Option<Vec<&'mpeg_life MpegBox<'mpeg_life>>>,
        version: u32,
        flags: u64,

        chapters: HashMap<u64, String>,
    },
    ChunkOffsetBox {
        parent: Option<&'mpeg_life MpegBox<'mpeg_life>>,
        size: u64,
        box_type: u64,
        offset: u64,
        name: Option<String>,
        children: Option<Vec<&'mpeg_life MpegBox<'mpeg_life>>>,
        version: u32,
        flags: u64,

        chunk_count: u64,
        /// If small_chunk is true, then the chunks are 4 bytes each, otherwise they are 8 bytes each.
        small_chunk: bool,
        chunks: Vec<[u8; 8]> // hopefully this means 4 or 8 bytes

    },
    CleanAperatureBox {
        parent: Option<&'mpeg_life MpegBox<'mpeg_life>>,
        size: u64,
        box_type: u64,
        offset: u64,
        name: Option<String>,
        children: Option<Vec<&'mpeg_life MpegBox<'mpeg_life>>>,

        clean_aperature_width_n: u64,
        clean_aperature_width_d: u64,
        clean_aperature_height_n: u64,
        clean_aperature_height_d: u64,
        horiz_off_n: u64,
        horiz_off_d: u64,
        vert_off_n: u64,
        vert_off_d: u64,

    },
    DefaultBox {
        parent: Option<&'mpeg_life MpegBox<'mpeg_life>>,
        size: u64,
        box_type: u64,
        offset: u64,
        name: Option<String>,
        children: Option<Vec<&'mpeg_life MpegBox<'mpeg_life>>>,
    },
    DefaultFullBox {
        parent: Option<&'mpeg_life MpegBox<'mpeg_life>>,
        size: u64,
        box_type: u64,
        offset: u64,
        name: Option<String>,
        children: Option<Vec<&'mpeg_life MpegBox<'mpeg_life>>>,
        version: u32,
        flags: u64,
    },
    Unknown {
            parent: Option<&'mpeg_life MpegBox<'mpeg_life>>,
            size: u64,
            box_type: u64,
            offset: u64,
            name: Option<String>,
            children: Option<Vec<&'mpeg_life MpegBox<'mpeg_life>>>,
    },
    SampleSizeBox {
        parent: Option<&'mpeg_life MpegBox<'mpeg_life>>,
        size: u64,
        box_type: u64,
        offset: u64,
        name: Option<String>,
        children: Option<Vec<&'mpeg_life MpegBox<'mpeg_life>>>,
        version: u32,
        flags: u64,

        sample_count: u32,
        sample_sizes: Vec<u32>,
    },
}
impl MpegBox<'_> {
    pub fn read_maybe_child_from<'mpeg_life>(stream: &mut DataInputStream, parent: Option<&'mpeg_life MpegBox<'mpeg_life>>) -> Option<MpegBox<'mpeg_life>> {
        let offset = stream.get_position();
        let mut size: u64 = stream.read_u32()? as u64;
        let mut box_type: u64 = stream.read_u32()? as u64;
        if size == 1 {
            size = stream.read_u64()?;
        }

        return match box_type {
            Mpeg4BoxSignatures::ADDITIONAL_METADATA_CONTAINER_BOX => {
                warn!("Currently parsing {}, which we don't have a custom box for yet", Mpeg4BoxSignatures::ADDITIONAL_METADATA_CONTAINER_BOX);
                Some(DefaultBox {
                    parent,
                    size,
                    box_type,
                    offset,
                    name: Some("Additional Metadata Container Box".to_string()),
                    children: None,
                })

            }
            Mpeg4BoxSignatures::APPLE_LOSSLESS_BOX => {
                let version = stream.read_u8()? as u32;
                let flags = stream.read_bytes(3)?;
                let max_sample_per_frame = stream.read_u32()? as u64;
                if stream.skip(1) != 1 {
                    error!("MpegBox read failed: could not skip into stream");
                    return None;
                }
                let sample_size = stream.read_u8()? as u32;
                let history_mult = stream.read_u8()? as u32;
                let initial_history = stream.read_u8()? as u32;
                let k_modifier = stream.read_u8()? as u32;
                let channels = stream.read_u8()? as u32;
                if stream.skip(2) != 2 {
                    error!("MpegBox read failed: could not skip into stream");
                    return None;
                }
                let max_coded_frame_size = stream.read_u32()? as u64;
                let bit_rate = stream.read_u32()? as u64;
                let sample_rate = stream.read_u32()? as u64;

                Some(MpegBox::AppleLosslessBox {
                    parent,
                    size,
                    box_type,
                    offset,
                    name: Some(String::from("Apple Lossless Box")),
                    children: None,
                    version,
                    flags,

                    max_sample_per_frame,
                    max_coded_frame_size,
                    bit_rate,
                    sample_rate,
                    sample_size,
                    history_mult,
                    initial_history,
                    k_modifier,
                    channels,
                })
            }
            Mpeg4BoxSignatures::BINARY_XML_BOX => {
                let version = stream.read_u8()? as u32;
                let flags = stream.read_bytes(3)?;
                let data = stream.read_some_here((size - 8) as usize);

                Some(MpegBox::BinaryXmlBox {
                    parent,
                    size,
                    box_type,
                    offset,
                    name: Some(String::from("Binary XML Box")),
                    children: None,
                    version,
                    flags,

                    data,
                })

            }
            Mpeg4BoxSignatures::BIT_RATE_BOX => {
                Some(BitRateBox {
                    parent,
                    size,
                    box_type,
                    offset,
                    name: Some(String::from("Bit Rate Box")),
                    children: None,
                    decoding_buffer_size: stream.read_u32()? as u64,
                    max_bitrate: stream.read_u32()? as u64,
                    avg_bitrate: stream.read_u32()? as u64,
                })

            }
            Mpeg4BoxSignatures::CHAPTER_BOX => {
                let version = stream.read_u8()? as u32;
                let flags = stream.read_bytes(3)? as u64;
                let mut chapters = HashMap::new();

                stream.skip(4);
                let entry_count = stream.read_u8()? as u64;
                for _ in 0..entry_count {
                    let timestamp = stream.read_u64()?;
                    let title_len = stream.read_u8()? as usize;
                    let title = stream.read_string(title_len);
                    chapters.insert(timestamp, title);
                }
                Some(ChapterBox {
                    parent,
                    size,
                    box_type,
                    offset,
                    name: Some(String::from("Chapter Box")),
                    children: None,
                    version,
                    flags,

                    chapters,
                })
            }
            Mpeg4BoxSignatures::CHUNK_OFFSET_BOX |
            Mpeg4BoxSignatures::CHUNK_LARGE_OFFSET_BOX => {
                let chunk_size = match box_type {
                    Mpeg4BoxSignatures::CHUNK_OFFSET_BOX => 4,
                    Mpeg4BoxSignatures::CHUNK_LARGE_OFFSET_BOX => 8,
                    _ => 0,
                };
                let version = stream.read_u8()? as u32;
                let flags = stream.read_bytes(3)? as u64;
                let entry_count = stream.read_u32()? as u64;
                let mut chunks = Vec::new();
                for _ in 0..entry_count {
                    let mut chunk_buf: [u8; 8] = [0; 8];
                    if stream.read_some_there(&mut chunk_buf, 0, chunk_size) != chunk_size {
                        error!("MpegBox read failed: could not read chunk offset");
                        return None;
                    }
                    chunks.push(chunk_buf);
                }
                Some(ChunkOffsetBox {
                    parent,
                    size,
                    box_type,
                    offset,
                    name: Some(String::from("Chunk Offset Box")),
                    children: None,
                    version,
                    flags,

                    chunks,
                    small_chunk: chunk_size == 4,
                    chunk_count: entry_count,
                })
            }
            Mpeg4BoxSignatures::CLEAN_APERTURE_BOX => {
                Some(CleanAperatureBox {
                    parent,
                    size,
                    box_type,
                    offset,
                    name: Some(String::from("Clean Aperature Box")),
                    children: None,
                    clean_aperature_width_n: stream.read_u32()? as u64,
                    clean_aperature_width_d: stream.read_u32()? as u64,
                    clean_aperature_height_n: stream.read_u32()? as u64,
                    clean_aperature_height_d: stream.read_u32()? as u64,
                    horiz_off_n: stream.read_u32()? as u64,
                    horiz_off_d: stream.read_u32()? as u64,
                    vert_off_n: stream.read_u32()? as u64,
                    vert_off_d: stream.read_u32()? as u64,
                })

            }
            Mpeg4BoxSignatures::COMPACT_SAMPLE_SIZE_BOX |
            Mpeg4BoxSignatures::SAMPLE_SIZE_BOX => {
                let compact = box_type == Mpeg4BoxSignatures::COMPACT_SAMPLE_SIZE_BOX;
                let mut size: u32 = 0;
                if compact {
                    if stream.skip(3) != 3 {
                        error!("MpegBox read failed: could not skip into stream");
                        return None;
                    }
                    size = stream.read_u8()? as u32;
                } else {
                    size = stream.read_u32()?;
                }
                let count = stream.read_u32()?;
                let mut sizes = Vec::new();

                None
            }
            Mpeg4BoxSignatures::COMPOSITION_TIME_TO_SAMPLE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::COPYRIGHT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::DATA_ENTRY_URN_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::DATA_ENTRY_URL_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::DATA_INFORMATION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::DATA_REFERENCE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::DECODING_TIME_TO_SAMPLE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::DEGRADATION_PRIORITY_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::EDIT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::EDIT_LIST_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::FD_ITEM_INFORMATION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::FD_SESSION_GROUP_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::FEC_RESERVOIR_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::FILE_PARTITION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::FILE_TYPE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::FREE_SPACE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::GROUP_ID_TO_NAME_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::HANDLER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::HINT_MEDIA_HEADER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::IPMP_CONTROL_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::IPMP_INFO_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ITEM_INFORMATION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ITEM_INFORMATION_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::ITEM_LOCATION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ITEM_PROTECTION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::MEDIA_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::MEDIA_DATA_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::MEDIA_HEADER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::MEDIA_INFORMATION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::META_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::META_BOX_RELATION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::MOVIE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::MOVIE_EXTENDS_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::MOVIE_EXTENDS_HEADER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::MOVIE_FRAGMENT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::MOVIE_FRAGMENT_HEADER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::MOVIE_FRAGMENT_RANDOM_ACCESS_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::MOVIE_FRAGMENT_RANDOM_ACCESS_OFFSET_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::MOVIE_HEADER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::NERO_METADATA_TAGS_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::NULL_MEDIA_HEADER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ORIGINAL_FORMAT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::PADDING_BIT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::PARTITION_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::PIXEL_ASPECT_RATIO_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::PRIMARY_ITEM_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::PROGRESSIVE_DOWNLOAD_INFORMATION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::PROTECTION_SCHEME_INFORMATION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SAMPLE_DEPENDENCY_TYPE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SAMPLE_DESCRIPTION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SAMPLE_GROUP_DESCRIPTION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SAMPLE_SCALE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SAMPLE_SIZE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SAMPLE_TABLE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SAMPLE_TO_CHUNK_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SAMPLE_TO_GROUP_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SCHEME_TYPE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SCHEME_INFORMATION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SHADOW_SYNC_SAMPLE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SKIP_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SOUND_MEDIA_HEADER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SUB_SAMPLE_INFORMATION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SYNC_SAMPLE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TRACK_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TRACK_EXTENDS_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TRACK_FRAGMENT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TRACK_FRAGMENT_HEADER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TRACK_FRAGMENT_RANDOM_ACCESS_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TRACK_FRAGMENT_RUN_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TRACK_HEADER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TRACK_REFERENCE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TRACK_SELECTION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::USER_DATA_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::VIDEO_MEDIA_HEADER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::WIDE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::XML_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::OBJECT_DESCRIPTOR_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SAMPLE_DEPENDENCY_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ID3_TAG_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ITUNES_META_LIST_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::CUSTOM_ITUNES_METADATA_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ITUNES_METADATA_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ITUNES_METADATA_NAME_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ITUNES_METADATA_MEAN_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ALBUM_ARTIST_NAME_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ALBUM_ARTIST_SORT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ALBUM_NAME_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ALBUM_SORT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ARTIST_NAME_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ARTIST_SORT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::CATEGORY_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::COMMENTS_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::COMPILATION_PART_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::COMPOSER_NAME_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::COMPOSER_SORT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::COVER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::CUSTOM_GENRE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::DESCRIPTION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::DISK_NUMBER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ENCODER_NAME_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ENCODER_TOOL_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::EPISODE_GLOBAL_UNIQUE_ID_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::GAPLESS_PLAYBACK_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::GENRE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::GROUPING_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::HD_VIDEO_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ITUNES_PURCHASE_ACCOUNT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ITUNES_ACCOUNT_TYPE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ITUNES_CATALOGUE_ID_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::ITUNES_COUNTRY_CODE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::KEYWORD_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::LONG_DESCRIPTION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::LYRICS_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::META_TYPE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::PODCAST_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::PODCAST_URL_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::PURCHASE_DATE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::RATING_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::RELEASE_DATE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::REQUIREMENT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TEMPO_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TRACK_NAME_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TRACK_NUMBER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TRACK_SORT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TV_EPISODE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TV_EPISODE_NUMBER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TV_NETWORK_NAME_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TV_SEASON_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TV_SHOW_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::TV_SHOW_SORT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::THREE_GPP_ALBUM_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::THREE_GPP_AUTHOR_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::THREE_GPP_CLASSIFICATION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::THREE_GPP_DESCRIPTION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::THREE_GPP_KEYWORDS_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::THREE_GPP_LOCATION_INFORMATION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::THREE_GPP_PERFORMER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::THREE_GPP_RECORDING_YEAR_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::THREE_GPP_TITLE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::GOOGLE_HOST_HEADER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::GOOGLE_PING_MESSAGE_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::GOOGLE_PING_URL_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::GOOGLE_SOURCE_DATA_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::GOOGLE_START_TIME_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::GOOGLE_TRACK_DURATION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::MP4V_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::H263_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::ENCRYPTED_VIDEO_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::AVC_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::MP4A_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::AC3_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::EAC3_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::DRMS_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::AMR_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::AMR_WB_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::EVRC_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::QCELP_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::SMV_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::ENCRYPTED_AUDIO_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::MPEG_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::TEXT_METADATA_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::XML_METADATA_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::RTP_HINT_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::FD_HINT_SAMPLE_ENTRY => {
                todo!()

            }
            Mpeg4BoxSignatures::ESD_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::H263_SPECIFIC_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::AVC_SPECIFIC_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::AC3_SPECIFIC_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::EAC3_SPECIFIC_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::AMR_SPECIFIC_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::EVRC_SPECIFIC_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::QCELP_SPECIFIC_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::SMV_SPECIFIC_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::OMA_ACCESS_UNIT_FORMAT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::OMA_COMMON_HEADERS_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::OMA_CONTENT_ID_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::OMA_CONTENT_OBJECT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::OMA_COVER_URI_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::OMA_DISCRETE_MEDIA_HEADERS_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::OMA_DRM_CONTAINER_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::OMA_ICON_URI_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::OMA_INFO_URL_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::OMA_LYRICS_URI_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::OMA_MUTABLE_DRM_INFORMATION_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::OMA_KEY_MANAGEMENT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::OMA_RIGHTS_OBJECT_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::OMA_TRANSACTION_TRACKING_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::FAIRPLAY_USER_ID_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::FAIRPLAY_USER_NAME_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::FAIRPLAY_USER_KEY_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::FAIRPLAY_IV_BOX => {
                todo!()

            }
            Mpeg4BoxSignatures::FAIRPLAY_PRIVATE_KEY_BOX => {
                todo!()

            }
            _ => {
                warn!("Unknown box type: {}", box_type);
                let mut unknown_box = MpegBox::Unknown {
                    parent,
                    size,
                    box_type: box_type as u64,
                    offset,
                    name: Some(Mpeg4BoxSignatures::name_for(box_type)),
                    children: Vec::new(),
                };
                unknown_box.read_children(stream);
                Some(unknown_box)
            }
        }
    }
    pub fn read_from(stream: &mut DataInputStream) -> Option<MpegBox> {
        MpegBox::read_maybe_child_from(stream, None)
    }
    pub fn read_children(&mut self, from: &mut DataInputStream) {
        while let Some(child) = MpegBox::read_maybe_child_from(from, Some(self)) {
            match self {
                _ => {
                    panic!("we shouldn't be here, make sure to add each new box type when implemented!")
                }
            }
        }
    }
}


