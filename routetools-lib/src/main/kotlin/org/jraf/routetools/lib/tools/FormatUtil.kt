package org.jraf.routetools.lib.tools

import com.mapbox.services.commons.models.Position
import org.jraf.routetools.lib.model.Speed

object FormatUtil {
    enum class Format {
        GPX,
        KML,
        ANDROID_EMULATOR,
    }

    fun format(format: Format, positionList: List<Position>, speed: Speed, delayBetweenPositionsSecond: Int): String {
        return when (format) {
            Format.GPX -> asGpx(positionList)
            Format.KML -> asKml(positionList)
            Format.ANDROID_EMULATOR -> asAndroidEmulatorScript(positionList, speed, delayBetweenPositionsSecond)
        }
    }

    fun asGpx(positionList: List<Position>): String {
        val res = StringBuilder("""<?xml version="1.0" encoding="UTF-8"?>
<gpx xsi:schemaLocation="http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd" xmlns="http://www.topografix.com/GPX/1/0" creator="RouteUtil" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0">
""")
        for (position in positionList) {
            res.append("<wpt lat=\"${position.latitude}\" lon=\"${position.longitude}\"/>\n")
        }
        res.append("</gpx>")
        return res.toString()
    }

    fun asKml(positionList: List<Position>): String {
        val res = StringBuilder("""<?xml version="1.0" encoding="utf-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2">
  <Document>
""")
        for (position in positionList) {
            res.append("<Placemark><Point><coordinates>${position.longitude},${position.latitude},0</coordinates></Point></Placemark>\n")
        }
        res.append("""  </Document>
</kml>""")
        return res.toString()
    }

    fun asAndroidEmulatorScript(positionList: List<Position>, speed: Speed, delayBetweenPositionsSecond: Int): String {
        val res = StringBuilder("""#!/usr/bin/expect

set port [lindex ${'$'}argv 0]
set date [clock format [clock seconds] -format %d%m%y]

set timeout 1
spawn telnet localhost ${'$'}port
expect_after eof { exit 0 }

## interact
expect OK

set fp [open "~/.emulator_console_auth_token" r]
if {[gets ${'$'}fp line] != -1} {
  send "auth ${'$'}line\r"
}

""")
        val bearingInfer = BearingInfer()
        for (position in positionList) {
            bearingInfer.add(position)

            val latitude = String.format("%d%.3f,%s", position.latitudeDegrees, position.latitudeMinutes, position.latitudeNorthSouth)
            val longitude = String.format("%d%.3f,%s", position.longitudeDegrees, position.longitudeMinutes, position.longitudeEastWest)
            val bearing = String.format("%.1f", bearingInfer.bearing)
            val speedStr = String.format("%.1f", speed.toMetersPerSecond())

            val command = "geo nmea \\\$GPRMC,[clock format [clock seconds] -format %H%M%S],A,$latitude,$longitude,$speedStr,$bearing,\$date,,*00"

            res.append("send \"$command\\r\"\n")
            res.append("expect OK\n")
            res.append("sleep $delayBetweenPositionsSecond\n")
        }

        res.append("\nsend \"exit\\r\"\n")
        return res.toString()
    }

    private val Position.latitudeDegrees get() = Math.abs(latitude).toInt()
    private val Position.latitudeMinutes get() = 60 * (Math.abs(latitude) - latitudeDegrees)
    private val Position.latitudeNorthSouth get() = if (latitude > 0) "N" else "S"

    private val Position.longitudeDegrees get() = Math.abs(longitude).toInt()
    private val Position.longitudeMinutes get() = 60 * (Math.abs(longitude) - longitudeDegrees)
    private val Position.longitudeEastWest get() = if (longitude > 0) "E" else "W"

}