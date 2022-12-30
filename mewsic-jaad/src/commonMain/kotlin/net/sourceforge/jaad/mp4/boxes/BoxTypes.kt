package net.sourceforge.jaad.mp4.boxes
import net.sourceforge.jaad.mp4.boxes.BoxTypes
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
interface BoxTypes {
    companion object {
        const val EXTENDED_TYPE: Long = 1970628964 //uuid

        //standard boxes (ISO BMFF)
        const val ADDITIONAL_METADATA_CONTAINER_BOX = 1835361135L //meco
        const val APPLE_LOSSLESS_BOX = 1634492771L //alac
        const val BINARY_XML_BOX = 1652059500L //bxml
        const val BIT_RATE_BOX = 1651798644L //btrt
        const val CHAPTER_BOX = 1667788908L //chpl
        const val CHUNK_OFFSET_BOX = 1937007471L //stco
        const val CHUNK_LARGE_OFFSET_BOX = 1668232756L //co64
        const val CLEAN_APERTURE_BOX = 1668047216L //clap
        const val COMPACT_SAMPLE_SIZE_BOX = 1937013298L //stz2
        const val COMPOSITION_TIME_TO_SAMPLE_BOX = 1668576371L //ctts
        const val COPYRIGHT_BOX = 1668313716L //cprt
        const val DATA_ENTRY_URN_BOX = 1970433568L //urn 
        const val DATA_ENTRY_URL_BOX = 1970433056L //url 
        const val DATA_INFORMATION_BOX = 1684631142L //dinf
        const val DATA_REFERENCE_BOX = 1685218662L //dref
        const val DECODING_TIME_TO_SAMPLE_BOX = 1937011827L //stts
        const val DEGRADATION_PRIORITY_BOX = 1937007728L //stdp
        const val EDIT_BOX = 1701082227L //edts
        const val EDIT_LIST_BOX = 1701606260L //elst
        const val FD_ITEM_INFORMATION_BOX = 1718184302L //fiin
        const val FD_SESSION_GROUP_BOX = 1936025458L //segr
        const val FEC_RESERVOIR_BOX = 1717920626L //fecr
        const val FILE_PARTITION_BOX = 1718641010L //fpar
        const val FILE_TYPE_BOX = 1718909296L //ftyp
        const val FREE_SPACE_BOX = 1718773093L //free
        const val GROUP_ID_TO_NAME_BOX = 1734964334L //gitn
        const val HANDLER_BOX = 1751411826L //hdlr
        const val HINT_MEDIA_HEADER_BOX = 1752000612L //hmhd
        const val IPMP_CONTROL_BOX = 1768975715L //ipmc
        const val IPMP_INFO_BOX = 1768778086L //imif
        const val ITEM_INFORMATION_BOX = 1768517222L //iinf
        const val ITEM_INFORMATION_ENTRY = 1768842853L //infe
        const val ITEM_LOCATION_BOX = 1768714083L //iloc
        const val ITEM_PROTECTION_BOX = 1768977007L //ipro
        const val MEDIA_BOX = 1835297121L //mdia
        const val MEDIA_DATA_BOX = 1835295092L //mdat
        const val MEDIA_HEADER_BOX = 1835296868L //mdhd
        const val MEDIA_INFORMATION_BOX = 1835626086L //minf
        const val META_BOX = 1835365473L //meta
        const val META_BOX_RELATION_BOX = 1835364965L //mere
        const val MOVIE_BOX = 1836019574L //moov
        const val MOVIE_EXTENDS_BOX = 1836475768L //mvex
        const val MOVIE_EXTENDS_HEADER_BOX = 1835362404L //mehd
        const val MOVIE_FRAGMENT_BOX = 1836019558L //moof
        const val MOVIE_FRAGMENT_HEADER_BOX = 1835427940L //mfhd
        const val MOVIE_FRAGMENT_RANDOM_ACCESS_BOX = 1835430497L //mfra
        const val MOVIE_FRAGMENT_RANDOM_ACCESS_OFFSET_BOX = 1835430511L //mfro
        const val MOVIE_HEADER_BOX = 1836476516L //mvhd
        const val NERO_METADATA_TAGS_BOX = 1952540531L //tags
        const val NULL_MEDIA_HEADER_BOX = 1852663908L //nmhd
        const val ORIGINAL_FORMAT_BOX = 1718775137L //frma
        const val PADDING_BIT_BOX = 1885430882L //padb
        const val PARTITION_ENTRY = 1885431150L //paen
        const val PIXEL_ASPECT_RATIO_BOX = 1885434736L //pasp
        const val PRIMARY_ITEM_BOX = 1885959277L //pitm
        const val PROGRESSIVE_DOWNLOAD_INFORMATION_BOX = 1885628782L //pdin
        const val PROTECTION_SCHEME_INFORMATION_BOX = 1936289382L //sinf
        const val SAMPLE_DEPENDENCY_TYPE_BOX = 1935963248L //sdtp
        const val SAMPLE_DESCRIPTION_BOX = 1937011556L //stsd
        const val SAMPLE_GROUP_DESCRIPTION_BOX = 1936158820L //sgpd
        const val SAMPLE_SCALE_BOX = 1937011564L //stsl
        const val SAMPLE_SIZE_BOX = 1937011578L //stsz
        const val SAMPLE_TABLE_BOX = 1937007212L //stbl
        const val SAMPLE_TO_CHUNK_BOX = 1937011555L //stsc
        const val SAMPLE_TO_GROUP_BOX = 1935828848L //sbgp
        const val SCHEME_TYPE_BOX = 1935894637L //schm
        const val SCHEME_INFORMATION_BOX = 1935894633L //schi
        const val SHADOW_SYNC_SAMPLE_BOX = 1937011560L //stsh
        const val SKIP_BOX = 1936419184L //skip
        const val SOUND_MEDIA_HEADER_BOX = 1936549988L //smhd
        const val SUB_SAMPLE_INFORMATION_BOX = 1937072755L //subs
        const val SYNC_SAMPLE_BOX = 1937011571L //stss
        const val TRACK_BOX = 1953653099L //trak
        const val TRACK_EXTENDS_BOX = 1953654136L //trex
        const val TRACK_FRAGMENT_BOX = 1953653094L //traf
        const val TRACK_FRAGMENT_HEADER_BOX = 1952868452L //tfhd
        const val TRACK_FRAGMENT_RANDOM_ACCESS_BOX = 1952871009L //tfra
        const val TRACK_FRAGMENT_RUN_BOX = 1953658222L //trun
        const val TRACK_HEADER_BOX = 1953196132L //tkhd
        const val TRACK_REFERENCE_BOX = 1953654118L //tref
        const val TRACK_SELECTION_BOX = 1953719660L //tsel
        const val USER_DATA_BOX = 1969517665L //udta
        const val VIDEO_MEDIA_HEADER_BOX = 1986881636L //vmhd
        const val WIDE_BOX = 2003395685L //wide
        const val XML_BOX = 2020437024L //xml 

        //mp4 extension
        const val OBJECT_DESCRIPTOR_BOX = 1768907891L //iods
        const val SAMPLE_DEPENDENCY_BOX = 1935959408L //sdep

        //metadata: id3
        const val ID3_TAG_BOX = 1768174386L //id32

        //metadata: itunes
        const val ITUNES_META_LIST_BOX = 1768715124L //ilst
        const val CUSTOM_ITUNES_METADATA_BOX = 757935405L //----
        const val ITUNES_METADATA_BOX = 1684108385L //data
        const val ITUNES_METADATA_NAME_BOX = 1851878757L //name
        const val ITUNES_METADATA_MEAN_BOX = 1835360622L //mean
        const val ALBUM_ARTIST_NAME_BOX = 1631670868L //aART
        const val ALBUM_ARTIST_SORT_BOX = 1936679265L //soaa 
        const val ALBUM_NAME_BOX = 2841734242L //©alb
        const val ALBUM_SORT_BOX = 1936679276L //soal
        const val ARTIST_NAME_BOX = 2839630420L //©ART
        const val ARTIST_SORT_BOX = 1936679282L //soar
        const val CATEGORY_BOX = 1667331175L //catg
        const val COMMENTS_BOX = 2841865588L //©cmt
        const val COMPILATION_PART_BOX = 1668311404L //cpil 
        const val COMPOSER_NAME_BOX = 2843177588L //©wrt
        const val COMPOSER_SORT_BOX = 1936679791L //soco
        const val COVER_BOX = 1668249202L //covr
        const val CUSTOM_GENRE_BOX = 2842125678L //©gen
        const val DESCRIPTION_BOX = 1684370275L //desc
        const val DISK_NUMBER_BOX = 1684632427L //disk
        const val ENCODER_NAME_BOX = 2841996899L //©enc
        const val ENCODER_TOOL_BOX = 2842980207L //©too
        const val EPISODE_GLOBAL_UNIQUE_ID_BOX = 1701276004L //egid
        const val GAPLESS_PLAYBACK_BOX = 1885823344L //pgap
        const val GENRE_BOX = 1735291493L //gnre
        const val GROUPING_BOX = 2842129008L //©grp
        const val HD_VIDEO_BOX = 1751414372L //hdvd
        const val ITUNES_PURCHASE_ACCOUNT_BOX = 1634748740L //apID
        const val ITUNES_ACCOUNT_TYPE_BOX = 1634421060L //akID
        const val ITUNES_CATALOGUE_ID_BOX = 1668172100L //cnID
        const val ITUNES_COUNTRY_CODE_BOX = 1936083268L //sfID
        const val KEYWORD_BOX = 1801812343L //keyw
        const val LONG_DESCRIPTION_BOX = 1818518899L //ldes
        const val LYRICS_BOX = 2842458482L //©lyr
        const val META_TYPE_BOX = 1937009003L //stik
        const val PODCAST_BOX = 1885565812L //pcst
        const val PODCAST_URL_BOX = 1886745196L //purl
        const val PURCHASE_DATE_BOX = 1886745188L //purd
        const val RATING_BOX = 1920233063L //rtng
        const val RELEASE_DATE_BOX = 2841928057L //©day
        const val REQUIREMENT_BOX = 2842846577L //©req
        const val TEMPO_BOX = 1953329263L //tmpo
        const val TRACK_NAME_BOX = 2842583405L //©nam
        const val TRACK_NUMBER_BOX = 1953655662L //trkn
        const val TRACK_SORT_BOX = 1936682605L //sonm
        const val TV_EPISODE_BOX = 1953916275L //tves
        const val TV_EPISODE_NUMBER_BOX = 1953916270L //tven
        const val TV_NETWORK_NAME_BOX = 1953918574L //tvnn
        const val TV_SEASON_BOX = 1953919854L //tvsn
        const val TV_SHOW_BOX = 1953919848L //tvsh
        const val TV_SHOW_SORT_BOX = 1936683886L //sosn

        //metadata: 3gpp
        const val THREE_GPP_ALBUM_BOX = 1634493037L //albm
        const val THREE_GPP_AUTHOR_BOX = 1635087464L //auth
        const val THREE_GPP_CLASSIFICATION_BOX = 1668051814L //clsf
        const val THREE_GPP_DESCRIPTION_BOX = 1685283696L //dscp
        const val THREE_GPP_KEYWORDS_BOX = 1803122532L //kywd
        const val THREE_GPP_LOCATION_INFORMATION_BOX = 1819239273L //loci
        const val THREE_GPP_PERFORMER_BOX = 1885696614L //perf
        const val THREE_GPP_RECORDING_YEAR_BOX = 2037543523L //yrrc
        const val THREE_GPP_TITLE_BOX = 1953068140L //titl

        //metadata: google/youtube
        const val GOOGLE_HOST_HEADER_BOX = 1735616616L //gshh
        const val GOOGLE_PING_MESSAGE_BOX = 1735618669L //gspm
        const val GOOGLE_PING_URL_BOX = 1735618677L //gspu
        const val GOOGLE_SOURCE_DATA_BOX = 1735619428L //gssd
        const val GOOGLE_START_TIME_BOX = 1735619444L //gsst
        const val GOOGLE_TRACK_DURATION_BOX = 1735619684L //gstd

        //sample entries
        const val MP4V_SAMPLE_ENTRY = 1836070006L //mp4v
        const val H263_SAMPLE_ENTRY = 1932670515L //s263
        const val ENCRYPTED_VIDEO_SAMPLE_ENTRY = 1701733238L //encv
        const val AVC_SAMPLE_ENTRY = 1635148593L //avc1
        const val MP4A_SAMPLE_ENTRY = 1836069985L //mp4a
        const val AC3_SAMPLE_ENTRY = 1633889587L //ac-3
        const val EAC3_SAMPLE_ENTRY = 1700998451L //ec-3
        const val DRMS_SAMPLE_ENTRY = 1685220723L //drms
        const val AMR_SAMPLE_ENTRY = 1935764850L //samr
        const val AMR_WB_SAMPLE_ENTRY = 1935767394L //sawb
        const val EVRC_SAMPLE_ENTRY = 1936029283L //sevc
        const val QCELP_SAMPLE_ENTRY = 1936810864L //sqcp
        const val SMV_SAMPLE_ENTRY = 1936944502L //ssmv
        const val ENCRYPTED_AUDIO_SAMPLE_ENTRY = 1701733217L //enca
        const val MPEG_SAMPLE_ENTRY = 1836070003L //mp4s
        const val TEXT_METADATA_SAMPLE_ENTRY = 1835365492L //mett
        const val XML_METADATA_SAMPLE_ENTRY = 1835365496L //metx
        const val RTP_HINT_SAMPLE_ENTRY = 1920233504L //rtp 
        const val FD_HINT_SAMPLE_ENTRY = 1717858336L //fdp 

        //codec infos
        const val ESD_BOX = 1702061171L //esds

        //video codecs
        const val H263_SPECIFIC_BOX = 1681012275L //d263
        const val AVC_SPECIFIC_BOX = 1635148611L //avcC

        //audio codecs
        const val AC3_SPECIFIC_BOX = 1684103987L //dac3
        const val EAC3_SPECIFIC_BOX = 1684366131L //dec3
        const val AMR_SPECIFIC_BOX = 1684106610L //damr
        const val EVRC_SPECIFIC_BOX = 1684371043L //devc
        const val QCELP_SPECIFIC_BOX = 1685152624L //dqcp
        const val SMV_SPECIFIC_BOX = 1685286262L //dsmv

        //OMA DRM
        const val OMA_ACCESS_UNIT_FORMAT_BOX = 1868849510L //odaf
        const val OMA_COMMON_HEADERS_BOX = 1869112434L //ohdr
        const val OMA_CONTENT_ID_BOX = 1667459428L //ccid
        const val OMA_CONTENT_OBJECT_BOX = 1868850273L //odda
        const val OMA_COVER_URI_BOX = 1668706933L //cvru
        const val OMA_DISCRETE_MEDIA_HEADERS_BOX = 1868851301L //odhe
        const val OMA_DRM_CONTAINER_BOX = 1868853869L //odrm
        const val OMA_ICON_URI_BOX = 1768124021L //icnu
        const val OMA_INFO_URL_BOX = 1768842869L //infu
        const val OMA_LYRICS_URI_BOX = 1819435893L //lrcu
        const val OMA_MUTABLE_DRM_INFORMATION_BOX = 1835299433L //mdri
        const val OMA_KEY_MANAGEMENT_BOX = 1868852077L //odkm
        const val OMA_RIGHTS_OBJECT_BOX = 1868853858L //odrb
        const val OMA_TRANSACTION_TRACKING_BOX = 1868854388L //odtt

        //iTunes DRM (FairPlay)
        const val FAIRPLAY_USER_ID_BOX = 1970496882L //user
        const val FAIRPLAY_USER_NAME_BOX = 1851878757L //name
        const val FAIRPLAY_USER_KEY_BOX = 1801812256L //key 
        const val FAIRPLAY_IV_BOX = 1769367926L //iviv
        const val FAIRPLAY_PRIVATE_KEY_BOX = 1886546294L //priv
    }
}
