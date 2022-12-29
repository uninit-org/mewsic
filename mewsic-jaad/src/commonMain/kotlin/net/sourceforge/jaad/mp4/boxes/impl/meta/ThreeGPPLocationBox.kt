package net.sourceforge.jaad.mp4.boxes.impl.meta

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * This box contains meta information about a location.
 *
 * If the location information refers to a time-variant location, the name
 * should express a high-level location, such as "Finland" for several places in
 * Finland or "Finland-Sweden" for several places in Finland and Sweden. Further
 * details on time-variant locations can be provided as additional notes.
 *
 * The values of longitude, latitude and altitude provide cursory Global
 * Positioning System (GPS) information of the media content.
 *
 * A value of longitude (latitude) that is less than –180 (-90) or greater than
 * 180 (90) indicates that the GPS coordinates (longitude, latitude, altitude)
 * are unspecified, i.e. none of the given values for longitude, latitude or
 * altitude are valid.
 *
 * @author in-somnia
 */
class ThreeGPPLocationBox :
    net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPMetadataBox("3GPP Location Information Box") {
    /**
     * The role of the place:<br></br>
     *
     *  1. "shooting location"
     *  1. "real location"
     *  1. "fictional location"
     * <br></br>
     * Other values are reserved.
     *
     * @return the role of the place
     */
    var role = 0
        private set

    /**
     * A floating point number indicating the longitude in degrees. Negative
     * values represent western longitude.
     *
     * @return the longitude
     */
    var longitude = 0.0
        private set

    /**
     * A floating point number indicating the latitude in degrees. Negative
     * values represent southern latitude.
     *
     * @return the latitude
     */
    var latitude = 0.0
        private set

    /**
     * A floating point number indicating the altitude in meters. The reference
     * altitude, indicated by zero, is set to the sea level.
     *
     * @return the altitude
     */
    var altitude = 0.0
        private set

    /**
     * A string indicating the name of the place.
     *
     * @return the place's name
     */
    var placeName: String? = null
        private set

    /**
     * A string indicating the astronomical body on which the location exists,
     * e.g. "earth".
     *
     * @return the astronomical body
     */
    var astronomicalBody: String? = null
        private set

    /**
     * A string containing any additional location-related information.
     *
     * @return the additional notes
     */
    var additionalNotes: String? = null
        private set

    @Throws(java.io.IOException::class)
    override fun decode(`in`: MP4InputStream) {
        decodeCommon(`in`)
        placeName = `in`.readUTFString(getLeft(`in`) as Int)
        role = `in`.read()
        longitude = `in`.readFixedPoint(16, 16)
        latitude = `in`.readFixedPoint(16, 16)
        altitude = `in`.readFixedPoint(16, 16)
        astronomicalBody = `in`.readUTFString(getLeft(`in`) as Int)
        additionalNotes = `in`.readUTFString(getLeft(`in`) as Int)
    }
}
