package org.mewsic.jaad.mp4.api

import org.mewsic.commons.streams.DataInputStream

internal class ID3Frame(`in`: DataInputStream) {
    val size: Long

    //header data
    val iD: Int
    private val flags: Int
    var groupID = 0
    var encryptionMethod = 0

    //content data
    val data: ByteArray

    init {
        iD = `in`.readInt()
        size = ID3Tag.readSynch(`in`).toLong()
        flags = `in`.readShort().toInt()
        if (isInGroup) groupID = `in`.read().toInt()
        if (isEncrypted) encryptionMethod = `in`.read().toInt()
        //TODO: data length indicator, unsync
        data = ByteArray(size.toInt())
        `in`.read(data)
    }

    val isInGroup: Boolean
        get() = flags and 0x40 == 0x40
    val isCompressed: Boolean
        get() = flags and 8 == 8
    val isEncrypted: Boolean
        get() = flags and 4 == 4
    val text: String
        // FIXME: 12/29/22 | this (maybe incorrectly) assumes UTF8 encoding
        get() = data.decodeToString()

    //        get() = String(data, java.nio.charset.Charset.forName(TEXT_ENCODINGS[0]))
    val encodedText: String
        get() {
            //first byte indicates encoding
            val enc = data[0].toInt()

            //charsets 0,3 end with '0'; 1,2 end with '00'
            var t = -1
            var i = 1
            while (i < data.size && t < 0) {
                if (data[i].toInt() == 0 && (enc == 0 || enc == 3 || data[i + 1].toInt() == 0)) t = i
                i++
            }
            // FIXME: 12/29/22 | we need to change this to use the correct encoding
            return data.decodeToString(1, t - 1)

        }
    val number: Int
        get() = data.decodeToString().toInt()
    val numbers: IntArray
        get() {
            //multiple numbers separated by '/'
            val x = text
            val i = x.indexOf('/')
            val y: IntArray = if (i > 0) intArrayOf(
                x.substring(0, i).toInt(),
                x.substring(i + 1).toInt()
            ) else intArrayOf(x.toInt())
            return y
        }
    val date: String
        get() {
            return data.decodeToString()
            // FIXME: 12/29/22 | this is broken, we need kotlinx datetime
            //timestamp lengths: 4,7,10,13,16,19
//            val i: Int = java.lang.Math.floor((data.size / 3).toDouble()).toInt() - 1
//            val date: java.util.Date?
//            date = if (i >= 0 && i < VALID_TIMESTAMPS.size) {
//                val sdf: java.text.SimpleDateFormat =
//                    java.text.SimpleDateFormat(VALID_TIMESTAMPS[i])
//                sdf.parse(kotlin.String(data), java.text.ParsePosition(0))
//            } else null
//            return date
        }
    val locale: String
        get() {
            return data.decodeToString().lowercase()
        }

    companion object {
        const val ALBUM_TITLE = 1413565506 //TALB
        const val ALBUM_SORT_ORDER = 1414745921 //TSOA
        const val ARTIST = 1414546737 //TPE1
        const val ATTACHED_PICTURE = 1095780675 //APIC
        const val AUDIO_ENCRYPTION = 1095061059 //AENC
        const val AUDIO_SEEK_POINT_INDEX = 1095979081 //ASPI
        const val BAND = 1414546738 //TPE2
        const val BEATS_PER_MINUTE = 1413632077 //TBPM
        const val COMMENTS = 1129270605 //COMM
        const val COMMERCIAL_FRAME = 1129270610 //COMR
        const val COMMERCIAL_INFORMATION = 1464029005 //WCOM
        const val COMPOSER = 1413697357 //TCOM
        const val CONDUCTOR = 1414546739 //TPE3
        const val CONTENT_GROUP_DESCRIPTION = 1414091825 //TIT1
        const val CONTENT_TYPE = 1413697358 //TCON
        const val COPYRIGHT = 1464029008 //WCOP
        const val COPYRIGHT_MESSAGE = 1413697360 //TCOP
        const val ENCODED_BY = 1413828163 //TENC
        const val ENCODING_TIME = 1413760334 //TDEN
        const val ENCRYPTION_METHOD_REGISTRATION = 1162756946 //ENCR
        const val EQUALISATION = 1162958130 //EQU2
        const val EVENT_TIMING_CODES = 1163150159 //ETCO
        const val FILE_OWNER = 1414485838 //TOWN
        const val FILE_TYPE = 1413893204 //TFLT
        const val GENERAL_ENCAPSULATED_OBJECT = 1195724610 //GEOB
        const val GROUP_IDENTIFICATION_REGISTRATION = 1196575044 //GRID
        const val INITIAL_KEY = 1414219097 //TKEY
        const val INTERNET_RADIO_STATION_NAME = 1414681422 //TRSN
        const val INTERNET_RADIO_STATION_OWNER = 1414681423 //TRSO
        const val MODIFIED_BY = 1414546740 //TPE4
        const val INVOLVED_PEOPLE_LIST = 1414090828 //TIPL
        const val INTERNATIONAL_STANDARD_RECORDING_CODE = 1414746691 //TSRC
        const val LANGUAGES = 1414283598 //TLAN
        const val LENGTH = 1414284622 //TLEN
        const val LINKED_INFORMATION = 1279872587 //LINK
        const val LYRICIST = 1413830740 //TEXT
        const val MEDIA_TYPE = 1414350148 //TMED
        const val MOOD = 1414352719 //TMOO
        const val MPEG_LOCATION_LOOKUP_TABLE = 1296845908 //MLLT
        const val MUSICIAN_CREDITS_LIST = 1414349644 //TMCL
        const val MUSIC_CD_IDENTIFIER = 1296254025 //MCDI
        const val OFFICIAL_ARTIST_WEBPAGE = 1464811858 //WOAR
        const val OFFICIAL_AUDIO_FILE_WEBPAGE = 1464811846 //WOAF
        const val OFFICIAL_AUDIO_SOURCE_WEBPAGE = 1464811859 //WOAS
        const val OFFICIAL_INTERNET_RADIO_STATION_HOMEPAGE = 1464816211 //WORS
        const val ORIGINAL_ALBUM_TITLE = 1414480204 //TOAL
        const val ORIGINAL_ARTIST = 1414484037 //TOPE
        const val ORIGINAL_FILENAME = 1414481486 //TOFN
        const val ORIGINAL_LYRICIST = 1414483033 //TOLY
        const val ORIGINAL_RELEASE_TIME = 1413762898 //TDOR
        const val OWNERSHIP_FRAME = 1331121733 //OWNE
        const val PART_OF_A_SET = 1414549331 //TPOS
        const val PAYMENT = 1464877401 //WPAY
        const val PERFORMER_SORT_ORDER = 1414745936 //TSOP
        const val PLAYLIST_DELAY = 1413762137 //TDLY
        const val PLAY_COUNTER = 1346588244 //PCNT
        const val POPULARIMETER = 1347375181 //POPM
        const val POSITION_SYNCHRONISATION_FRAME = 1347375955 //POSS
        const val PRIVATE_FRAME = 1347570006 //PRIV
        const val PRODUCED_NOTICE = 1414550095 //TPRO
        const val PUBLISHER = 1414550850 //TPUB
        const val PUBLISHERS_OFFICIAL_WEBPAGE = 1464882498 //WPUB
        const val RECOMMENDED_BUFFER_SIZE = 1380078918 //RBUF
        const val RECORDING_TIME = 1413763651 //TDRC
        const val RELATIVE_VOLUME_ADJUSTMENT = 1381384498 //RVA2
        const val RELEASE_TIME = 1413763660 //TDRL
        const val REVERB = 1381388866 //RVRB
        const val SEEK_FRAME = 1397048651 //SEEK
        const val SET_SUBTITLE = 1414746964 //TSST
        const val SIGNATURE_FRAME = 1397311310 //SIGN
        const val ENCODING_TOOLS_AND_SETTINGS = 1414746949 //TSSE
        const val SUBTITLE = 1414091827 //TIT3
        const val SYNCHRONISED_LYRIC = 1398361172 //SYLT
        const val SYNCHRONISED_TEMPO_CODES = 1398363203 //SYTC
        const val TAGGING_TIME = 1413764167 //TDTG
        const val TERMS_OF_USE = 1431520594 //USER
        const val TITLE = 1414091826 //TIT2
        const val TITLE_SORT_ORDER = 1414745940 //TSOT
        const val TRACK_NUMBER = 1414677323 //TRCK
        const val UNIQUE_FILE_IDENTIFIER = 1430669636 //UFID
        const val UNSYNCHRONISED_LYRIC = 1431522388 //USLT
        const val USER_DEFINED_TEXT_INFORMATION_FRAME = 1415075928 //TXXX
        const val USER_DEFINED_URL_LINK_FRAME = 1465407576 //WXXX
        private val TEXT_ENCODINGS = arrayOf("ISO-8859-1", "UTF-16" /*BOM*/, "UTF-16", "UTF-8")
        private val VALID_TIMESTAMPS =
            arrayOf("yyyy, yyyy-MM", "yyyy-MM-dd", "yyyy-MM-ddTHH", "yyyy-MM-ddTHH:mm", "yyyy-MM-ddTHH:mm:ss")
        private const val UNKNOWN_LANGUAGE = "xxx"
    }
}
