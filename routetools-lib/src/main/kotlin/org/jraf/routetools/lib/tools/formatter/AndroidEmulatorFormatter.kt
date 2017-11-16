package org.jraf.routetools.lib.tools.formatter

import com.mapbox.services.commons.models.Position
import org.jraf.routetools.lib.model.Speed
import org.jraf.routetools.lib.tools.BearingInfer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AndroidEmulatorFormatter : Formatter {
    override fun format(positionList: List<Position>, speed: Speed, delayBetweenPositionsSecond: Int): String {
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
        val dateStr = SimpleDateFormat("ddMMyy").format(Date())
        for ((index, position) in positionList.withIndex()) {
            bearingInfer.add(position)

            val latitude = String.format(Locale.US, "%d%.3f,%s", position.latitudeDegrees, position.latitudeMinutes, position.latitudeNorthSouth)
            val longitude = String.format(Locale.US, "%d%.3f,%s", position.longitudeDegrees, position.longitudeMinutes, position.longitudeEastWest)
            val bearing = String.format(Locale.US, "%.1f", bearingInfer.bearing)
            val speedStr = String.format(Locale.US, "%.1f", speed.toMetersPerSecond())

            res.append("# $index  lat: $latitude  lon: $longitude  bear: $bearing  speed: $speedStr\n")
            res.append("# geo nmea \$GPRMC,000000,A,$latitude,$longitude,$speedStr,$bearing,$dateStr,,*00\n")
            val command = "geo nmea \\\$GPRMC,[clock format [clock seconds] -format %H%M%S],A,$latitude,$longitude,$speedStr,$bearing,\$date,,*00"
            res.append("send \"$command\\r\"\n")
            res.append("expect OK\n")
            res.append("sleep $delayBetweenPositionsSecond\n")
            res.append("\n")
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