package net.sourceforge.jaad.mp4.api

import net.sourceforge.jaad.mp4.boxes.Box

/**
 * This class contains the metadata for a movie. It parses different metadata
 * types (iTunes tags, ID3).
 * The fields can be read via the `get(Field)` method using one of
 * the predefined `Field`s.
 *
 * @author in-somnia
 */
class MetaData internal constructor() {
    class Field<T> private constructor(val name: String) {

        companion object {
            val ARTIST = Field<String?>("Artist")
            val TITLE = Field<String?>("Title")
            val ALBUM_ARTIST = Field<String?>("Album Artist")
            val ALBUM = Field<String?>("Album")
            val TRACK_NUMBER = Field<Int?>("Track Number")
            val TOTAL_TRACKS = Field<Int>("Total Tracks")
            val DISK_NUMBER = Field<Int?>("Disk Number")
            val TOTAL_DISKS = Field<Int>("Total disks")
            val COMPOSER = Field<String?>("Composer")
            val COMMENTS = Field<String?>("Comments")
            val TEMPO = Field<Int?>("Tempo")
            val LENGTH_IN_MILLISECONDS = Field<Int>("Length in milliseconds")
            val RELEASE_DATE: Field<java.util.Date?> = Field<java.util.Date?>("Release Date")
            val GENRE = Field<String?>("Genre")
            val ENCODER_NAME = Field<String?>("Encoder Name")
            val ENCODER_TOOL = Field<String?>("Encoder Tool")
            val ENCODING_DATE: Field<java.util.Date?> = Field<java.util.Date?>("Encoding Date")
            val COPYRIGHT = Field<String?>("Copyright")
            val PUBLISHER = Field<String?>("Publisher")
            val COMPILATION = Field<Boolean?>("Part of compilation")
            val COVER_ARTWORKS: Field<MutableList<net.sourceforge.jaad.mp4.api.Artwork>> =
                Field<MutableList<net.sourceforge.jaad.mp4.api.Artwork>>("Cover Artworks")
            val GROUPING = Field<String?>("Grouping")
            val LOCATION = Field<String?>("Location")
            val LYRICS = Field<String?>("Lyrics")
            val RATING = Field<Int?>("Rating")
            val PODCAST = Field<Int?>("Podcast")
            val PODCAST_URL = Field<String?>("Podcast URL")
            val CATEGORY = Field<String?>("Category")
            val KEYWORDS = Field<String?>("Keywords")
            val EPISODE_GLOBAL_UNIQUE_ID = Field<Int>("Episode Global Unique ID")
            val DESCRIPTION = Field<String?>("Description")
            val TV_SHOW = Field<String?>("TV Show")
            val TV_NETWORK = Field<String?>("TV Network")
            val TV_EPISODE = Field<String?>("TV Episode")
            val TV_EPISODE_NUMBER = Field<Int?>("TV Episode Number")
            val TV_SEASON = Field<Int?>("TV Season")
            val INTERNET_RADIO_STATION = Field<String>("Internet Radio Station")
            val PURCHASE_DATE = Field<String?>("Purchase Date")
            val GAPLESS_PLAYBACK = Field<String?>("Gapless Playback")
            val HD_VIDEO = Field<Boolean?>("HD Video")
            val LANGUAGE: Field<java.util.Locale?> = Field<java.util.Locale?>("Language")

            //sorting
            val ARTIST_SORT_TEXT = Field<String?>("Artist Sort Text")
            val TITLE_SORT_TEXT = Field<String?>("Title Sort Text")
            val ALBUM_SORT_TEXT = Field<String?>("Album Sort Text")
        }
    }

    private val contents: MutableMap<Field<*>, Any>

    init {
        contents = java.util.HashMap<Field<*>, Any>()
    }

    /*moov.udta:
	 * -3gpp boxes
	 * -meta
	 * --ilst
	 * --tags
	 * --meta (no container!)
	 * --tseg
	 * ---tshd
	 */
    fun parse(udta: Box?, meta: Box) {
        //standard boxes
        if (meta.hasChild(BoxTypes.COPYRIGHT_BOX)) {
            val cprt: CopyrightBox = meta.getChild(BoxTypes.COPYRIGHT_BOX) as CopyrightBox
            put<java.util.Locale?>(Field.LANGUAGE, java.util.Locale(cprt.getLanguageCode()))
            put(Field.COPYRIGHT, cprt.getNotice())
        }
        //3gpp user data
        if (udta != null) parse3GPPData(udta)
        //id3, TODO: can be present in different languages
        if (meta.hasChild(BoxTypes.ID3_TAG_BOX)) parseID3(meta.getChild(BoxTypes.ID3_TAG_BOX) as ID3TagBox)
        //itunes
        if (meta.hasChild(BoxTypes.ITUNES_META_LIST_BOX)) parseITunesMetaData(meta.getChild(BoxTypes.ITUNES_META_LIST_BOX)!!)
        //nero tags
        if (meta.hasChild(BoxTypes.NERO_METADATA_TAGS_BOX)) parseNeroTags(meta.getChild(BoxTypes.NERO_METADATA_TAGS_BOX) as NeroMetadataTagsBox)
    }

    //parses specific children of 'udta': 3GPP
    //TODO: handle language codes
    private fun parse3GPPData(udta: Box) {
        if (udta.hasChild(BoxTypes.THREE_GPP_ALBUM_BOX)) {
            val albm: ThreeGPPAlbumBox = udta.getChild(BoxTypes.THREE_GPP_ALBUM_BOX) as ThreeGPPAlbumBox
            put(Field.ALBUM, albm.getData())
            put(Field.TRACK_NUMBER, albm.getTrackNumber())
        }
        //if(udta.hasChild(BoxTypes.THREE_GPP_AUTHOR_BOX));
        //if(udta.hasChild(BoxTypes.THREE_GPP_CLASSIFICATION_BOX));
        if (udta.hasChild(BoxTypes.THREE_GPP_DESCRIPTION_BOX)) put(
            Field.DESCRIPTION,
            (udta.getChild(BoxTypes.THREE_GPP_DESCRIPTION_BOX) as ThreeGPPMetadataBox).getData()
        )
        if (udta.hasChild(BoxTypes.THREE_GPP_KEYWORDS_BOX)) put(
            Field.KEYWORDS,
            (udta.getChild(BoxTypes.THREE_GPP_KEYWORDS_BOX) as ThreeGPPMetadataBox).getData()
        )
        if (udta.hasChild(BoxTypes.THREE_GPP_LOCATION_INFORMATION_BOX)) put(
            Field.LOCATION,
            (udta.getChild(BoxTypes.THREE_GPP_LOCATION_INFORMATION_BOX) as ThreeGPPLocationBox).getPlaceName()
        )
        if (udta.hasChild(BoxTypes.THREE_GPP_PERFORMER_BOX)) put(
            Field.ARTIST,
            (udta.getChild(BoxTypes.THREE_GPP_PERFORMER_BOX) as ThreeGPPMetadataBox).getData()
        )
        if (udta.hasChild(BoxTypes.THREE_GPP_RECORDING_YEAR_BOX)) {
            val value: String = (udta.getChild(BoxTypes.THREE_GPP_RECORDING_YEAR_BOX) as ThreeGPPMetadataBox).getData()
            try {
                put<java.util.Date?>(Field.RELEASE_DATE, java.util.Date(value.toInt()))
            } catch (e: java.lang.NumberFormatException) {
                java.util.logging.Logger.getLogger("MP4 API").log(
                    java.util.logging.Level.INFO,
                    "unable to parse 3GPP metadata: recording year value: {0}",
                    value
                )
            }
        }
        if (udta.hasChild(BoxTypes.THREE_GPP_TITLE_BOX)) put(
            Field.TITLE,
            (udta.getChild(BoxTypes.THREE_GPP_TITLE_BOX) as ThreeGPPMetadataBox).getData()
        )
    }

    //parses children of 'ilst': iTunes
    private fun parseITunesMetaData(ilst: Box) {
        val boxes: List<Box> = ilst.getChildren()
        var l: Long
        var data: ITunesMetadataBox
        for (box in boxes) {
            l = box.getType()
            data = box.getChild(BoxTypes.ITUNES_METADATA_BOX) as ITunesMetadataBox
            if (l == BoxTypes.ARTIST_NAME_BOX) put(
                Field.ARTIST,
                data.getText()
            ) else if (l == BoxTypes.TRACK_NAME_BOX) put(
                Field.TITLE, data.getText()
            ) else if (l == BoxTypes.ALBUM_ARTIST_NAME_BOX) put(
                Field.ALBUM_ARTIST,
                data.getText()
            ) else if (l == BoxTypes.ALBUM_NAME_BOX) put(
                Field.ALBUM, data.getText()
            ) else if (l == BoxTypes.TRACK_NUMBER_BOX) {
                val b: ByteArray = data.getData()
                put<Int?>(Field.TRACK_NUMBER, b[3])
                put(Field.TOTAL_TRACKS, b[5].toInt())
            } else if (l == BoxTypes.DISK_NUMBER_BOX) put(
                Field.DISK_NUMBER,
                data.getInteger()
            ) else if (l == BoxTypes.COMPOSER_NAME_BOX) put(
                Field.COMPOSER, data.getText()
            ) else if (l == BoxTypes.COMMENTS_BOX) put(
                Field.COMMENTS,
                data.getText()
            ) else if (l == BoxTypes.TEMPO_BOX) put(
                Field.TEMPO, data.getInteger()
            ) else if (l == BoxTypes.RELEASE_DATE_BOX) put(
                Field.RELEASE_DATE,
                data.getDate()
            ) else if (l == BoxTypes.GENRE_BOX || l == BoxTypes.CUSTOM_GENRE_BOX) {
                var s: String? = null
                if (data.getDataType() === ITunesMetadataBox.DataType.UTF8) s = data.getText() else {
                    val i: Int = data.getInteger()
                    if (i > 0 && i < STANDARD_GENRES.size) s = STANDARD_GENRES[data.getInteger()]
                }
                if (s != null) put<String?>(Field.GENRE, s)
            } else if (l == BoxTypes.ENCODER_NAME_BOX) put(
                Field.ENCODER_NAME,
                data.getText()
            ) else if (l == BoxTypes.ENCODER_TOOL_BOX) put(
                Field.ENCODER_TOOL, data.getText()
            ) else if (l == BoxTypes.COPYRIGHT_BOX) put(
                Field.COPYRIGHT,
                data.getText()
            ) else if (l == BoxTypes.COMPILATION_PART_BOX) put(
                Field.COMPILATION, data.getBoolean()
            ) else if (l == BoxTypes.COVER_BOX) {
                val aw: net.sourceforge.jaad.mp4.api.Artwork = net.sourceforge.jaad.mp4.api.Artwork(
                    net.sourceforge.jaad.mp4.api.Artwork.Type.Companion.forDataType(data.getDataType()), data.getData()
                )
                if (contents.containsKey(Field.COVER_ARTWORKS)) get<MutableList<net.sourceforge.jaad.mp4.api.Artwork>>(
                    Field.COVER_ARTWORKS
                )!!.add(aw) else {
                    val list: MutableList<net.sourceforge.jaad.mp4.api.Artwork> =
                        java.util.ArrayList<net.sourceforge.jaad.mp4.api.Artwork>()
                    list.add(aw)
                    put<MutableList<net.sourceforge.jaad.mp4.api.Artwork>>(Field.COVER_ARTWORKS, list)
                }
            } else if (l == BoxTypes.GROUPING_BOX) put(
                Field.GROUPING,
                data.getText()
            ) else if (l == BoxTypes.LYRICS_BOX) put(
                Field.LYRICS, data.getText()
            ) else if (l == BoxTypes.RATING_BOX) put(
                Field.RATING,
                data.getInteger()
            ) else if (l == BoxTypes.PODCAST_BOX) put(
                Field.PODCAST, data.getInteger()
            ) else if (l == BoxTypes.PODCAST_URL_BOX) put(
                Field.PODCAST_URL,
                data.getText()
            ) else if (l == BoxTypes.CATEGORY_BOX) put(
                Field.CATEGORY, data.getText()
            ) else if (l == BoxTypes.KEYWORD_BOX) put(
                Field.KEYWORDS,
                data.getText()
            ) else if (l == BoxTypes.DESCRIPTION_BOX) put(
                Field.DESCRIPTION, data.getText()
            ) else if (l == BoxTypes.LONG_DESCRIPTION_BOX) put(
                Field.DESCRIPTION,
                data.getText()
            ) else if (l == BoxTypes.TV_SHOW_BOX) put(
                Field.TV_SHOW, data.getText()
            ) else if (l == BoxTypes.TV_NETWORK_NAME_BOX) put(
                Field.TV_NETWORK,
                data.getText()
            ) else if (l == BoxTypes.TV_EPISODE_BOX) put(
                Field.TV_EPISODE, data.getText()
            ) else if (l == BoxTypes.TV_EPISODE_NUMBER_BOX) put(
                Field.TV_EPISODE_NUMBER,
                data.getInteger()
            ) else if (l == BoxTypes.TV_SEASON_BOX) put(
                Field.TV_SEASON, data.getInteger()
            ) else if (l == BoxTypes.PURCHASE_DATE_BOX) put(
                Field.PURCHASE_DATE,
                data.getText()
            ) else if (l == BoxTypes.GAPLESS_PLAYBACK_BOX) put(
                Field.GAPLESS_PLAYBACK, data.getText()
            ) else if (l == BoxTypes.HD_VIDEO_BOX) put(
                Field.HD_VIDEO,
                data.getBoolean()
            ) else if (l == BoxTypes.ARTIST_SORT_BOX) put(
                Field.ARTIST_SORT_TEXT, data.getText()
            ) else if (l == BoxTypes.TRACK_SORT_BOX) put(
                Field.TITLE_SORT_TEXT,
                data.getText()
            ) else if (l == BoxTypes.ALBUM_SORT_BOX) put(
                Field.ALBUM_SORT_TEXT, data.getText()
            )
        }
    }

    //parses children of ID3
    private fun parseID3(box: ID3TagBox) {
        try {
            val `in`: java.io.DataInputStream = java.io.DataInputStream(java.io.ByteArrayInputStream(box.getID3Data()))
            val tag: net.sourceforge.jaad.mp4.api.ID3Tag = net.sourceforge.jaad.mp4.api.ID3Tag(`in`)
            var num: IntArray
            for (frame in tag.getFrames()) {
                when (frame.getID()) {
                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.TITLE -> put<String?>(
                        Field.TITLE,
                        frame.getEncodedText()
                    )

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.ALBUM_TITLE -> put<String?>(
                        Field.ALBUM,
                        frame.getEncodedText()
                    )

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.TRACK_NUMBER -> {
                        num = frame.getNumbers()
                        put<Int?>(Field.TRACK_NUMBER, num[0])
                        if (num.size > 1) put(Field.TOTAL_TRACKS, num[1])
                    }

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.ARTIST -> put<String?>(
                        Field.ARTIST,
                        frame.getEncodedText()
                    )

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.COMPOSER -> put<String?>(
                        Field.COMPOSER,
                        frame.getEncodedText()
                    )

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.BEATS_PER_MINUTE -> put<Int?>(
                        Field.TEMPO,
                        frame.getNumber()
                    )

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.LENGTH -> put(
                        Field.LENGTH_IN_MILLISECONDS,
                        frame.getNumber()
                    )

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.LANGUAGES -> put<java.util.Locale?>(
                        Field.LANGUAGE,
                        frame.getLocale()
                    )

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.COPYRIGHT_MESSAGE -> put<String?>(
                        Field.COPYRIGHT,
                        frame.getEncodedText()
                    )

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.PUBLISHER -> put<String?>(
                        Field.PUBLISHER,
                        frame.getEncodedText()
                    )

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.INTERNET_RADIO_STATION_NAME -> put(
                        Field.INTERNET_RADIO_STATION,
                        frame.getEncodedText()
                    )

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.ENCODING_TIME -> put<java.util.Date?>(
                        Field.ENCODING_DATE,
                        frame.getDate()
                    )

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.RELEASE_TIME -> put<java.util.Date?>(
                        Field.RELEASE_DATE,
                        frame.getDate()
                    )

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.ENCODING_TOOLS_AND_SETTINGS -> put<String?>(
                        Field.ENCODER_TOOL,
                        frame.getEncodedText()
                    )

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.PERFORMER_SORT_ORDER -> put<String?>(
                        Field.ARTIST_SORT_TEXT,
                        frame.getEncodedText()
                    )

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.TITLE_SORT_ORDER -> put<String?>(
                        Field.TITLE_SORT_TEXT,
                        frame.getEncodedText()
                    )

                    net.sourceforge.jaad.mp4.api.ID3Frame.Companion.ALBUM_SORT_ORDER -> put<String?>(
                        Field.ALBUM_SORT_TEXT,
                        frame.getEncodedText()
                    )
                }
            }
        } catch (e: java.io.IOException) {
            java.util.logging.Logger.getLogger("MP4 API")
                .log(java.util.logging.Level.SEVERE, "Exception in MetaData.parseID3: {0}", e.toString())
        }
    }

    //parses children of 'tags': Nero
    private fun parseNeroTags(tags: NeroMetadataTagsBox) {
        val pairs: Map<String, String> = tags.getPairs()
        var `val`: String?
        for (key in pairs.keys) {
            `val` = pairs[key]
            try {
                if (key == NERO_TAGS[0]) put(Field.ARTIST, `val`)
                if (key == NERO_TAGS[1]) put(Field.TITLE, `val`)
                if (key == NERO_TAGS[2]) put(Field.ALBUM, `val`)
                if (key == NERO_TAGS[3]) put<Int?>(Field.TRACK_NUMBER, `val`!!.toInt())
                if (key == NERO_TAGS[4]) put(Field.TOTAL_TRACKS, `val`!!.toInt())
                if (key == NERO_TAGS[5]) {
                    val c: java.util.Calendar = java.util.Calendar.getInstance()
                    c.set(java.util.Calendar.YEAR, `val`!!.toInt())
                    put<java.util.Date?>(Field.RELEASE_DATE, c.getTime())
                }
                if (key == NERO_TAGS[6]) put(Field.GENRE, `val`)
                if (key == NERO_TAGS[7]) put<Int?>(Field.DISK_NUMBER, `val`!!.toInt())
                if (key == NERO_TAGS[8]) put(Field.TOTAL_DISKS, `val`!!.toInt())
                if (key == NERO_TAGS[9]);
                if (key == NERO_TAGS[10]) put(Field.COPYRIGHT, `val`)
                if (key == NERO_TAGS[11]) put(Field.COMMENTS, `val`)
                if (key == NERO_TAGS[12]) put(Field.LYRICS, `val`)
                if (key == NERO_TAGS[13]);
                if (key == NERO_TAGS[14]) put<Int?>(Field.RATING, `val`!!.toInt())
                if (key == NERO_TAGS[15]) put(Field.PUBLISHER, `val`)
                if (key == NERO_TAGS[16]) put(Field.COMPOSER, `val`)
                if (key == NERO_TAGS[17]);
                if (key == NERO_TAGS[18]);
                if (key == NERO_TAGS[19]) put<Int?>(Field.TEMPO, `val`!!.toInt())
            } catch (e: java.lang.NumberFormatException) {
                java.util.logging.Logger.getLogger("MP4 API")
                    .log(java.util.logging.Level.SEVERE, "Exception in MetaData.parseNeroTags: {0}", e.toString())
            }
        }
    }

    private fun <T> put(field: Field<T?>, value: T) {
        contents[field] = value
    }

    fun containsMetaData(): Boolean {
        return !contents.isEmpty()
    }

    operator fun <T> get(field: Field<T>): T? {
        return contents[field] as T?
    }

    val all: Map<Field<*>, Any>
        get() = java.util.Collections.unmodifiableMap<Field<*>, Any>(contents)

    companion object {
        private val STANDARD_GENRES = arrayOf(
            "undefined",  //IDv1 standard
            "blues",
            "classic rock",
            "country",
            "dance",
            "disco",
            "funk",
            "grunge",
            "hip hop",
            "jazz",
            "metal",
            "new age",
            "oldies",
            "other",
            "pop",
            "r and b",
            "rap",
            "reggae",
            "rock",
            "techno",
            "industrial",
            "alternative",
            "ska",
            "death metal",
            "pranks",
            "soundtrack",
            "euro techno",
            "ambient",
            "trip hop",
            "vocal",
            "jazz funk",
            "fusion",
            "trance",
            "classical",
            "instrumental",
            "acid",
            "house",
            "game",
            "sound clip",
            "gospel",
            "noise",
            "alternrock",
            "bass",
            "soul",
            "punk",
            "space",
            "meditative",
            "instrumental pop",
            "instrumental rock",
            "ethnic",
            "gothic",
            "darkwave",
            "techno industrial",
            "electronic",
            "pop folk",
            "eurodance",
            "dream",
            "southern rock",
            "comedy",
            "cult",
            "gangsta",
            "top ",
            "christian rap",
            "pop funk",
            "jungle",
            "native american",
            "cabaret",
            "new wave",
            "psychedelic",
            "rave",
            "showtunes",
            "trailer",
            "lo fi",
            "tribal",
            "acid punk",
            "acid jazz",
            "polka",
            "retro",
            "musical",
            "rock and roll",  //winamp extension
            "hard rock",
            "folk",
            "folk rock",
            "national folk",
            "swing",
            "fast fusion",
            "bebob",
            "latin",
            "revival",
            "celtic",
            "bluegrass",
            "avantgarde",
            "gothic rock",
            "progressive rock",
            "psychedelic rock",
            "symphonic rock",
            "slow rock",
            "big band",
            "chorus",
            "easy listening",
            "acoustic",
            "humour",
            "speech",
            "chanson",
            "opera",
            "chamber music",
            "sonata",
            "symphony",
            "booty bass",
            "primus",
            "porn groove",
            "satire",
            "slow jam",
            "club",
            "tango",
            "samba",
            "folklore",
            "ballad",
            "power ballad",
            "rhythmic soul",
            "freestyle",
            "duet",
            "punk rock",
            "drum solo",
            "a capella",
            "euro house",
            "dance hall"
        )
        private val NERO_TAGS = arrayOf(
            "artist", "title", "album", "track", "totaltracks", "year", "genre",
            "disc", "totaldiscs", "url", "copyright", "comment", "lyrics",
            "credits", "rating", "label", "composer", "isrc", "mood", "tempo"
        )
    }
}
