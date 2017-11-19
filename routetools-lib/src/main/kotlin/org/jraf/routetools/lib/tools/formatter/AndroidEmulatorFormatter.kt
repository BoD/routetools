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

set ip [lindex ${'$'}argv 0]
set port [lindex ${'$'}argv 1]
set date [clock format [clock seconds] -format %d%m%y]

set timeout 1
spawn telnet ${'$'}ip ${'$'}port
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
        val timeFormat = SimpleDateFormat("hhmmss")
        var time = System.currentTimeMillis()
        for ((index, position) in positionList.withIndex()) {
            bearingInfer.add(position)

            val timeStr = timeFormat.format(Date(time))
            val latitude = String.format(Locale.US, "%02d%.3f,%s", position.latitudeDegrees, position.latitudeMinutes, position.latitudeNorthSouth)
            val longitude = String.format(Locale.US, "%03d%.3f,%s", position.longitudeDegrees, position.longitudeMinutes, position.longitudeEastWest)
            val bearing = String.format(Locale.US, "%.1f", bearingInfer.bearing)
            val speedStr = String.format(Locale.US, "%.1f", speed.toMetersPerSecond())

            res.append("# $index  lat: ${position.latitude} ($latitude)  lon: ${position.longitude} ($longitude)  bear: $bearing  speed: $speedStr\n")
            val nmeaCommand = "\$GPRMC,$timeStr,A,$latitude,$longitude,$speedStr,$bearing,$dateStr,,*"
            val nmeaCommandWithChecksum = "$nmeaCommand${getChecksum(nmeaCommand)}"
            val emulatorCommand = "geo nmea $nmeaCommandWithChecksum"
            res.append("# $emulatorCommand\n")
            val escapedEmulatorCommand = emulatorCommand.replace("$", "\\$")
            res.append("send \"$escapedEmulatorCommand\\r\"\n")
            res.append("expect OK\n")
            res.append("sleep $delayBetweenPositionsSecond\n")
            res.append("\n")

            time += delayBetweenPositionsSecond * 1000
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

    private fun getChecksum(command: String): String {
        val from = command.indexOf("$")
        val to = command.indexOf("*")
        val checkString = command.substring(from + 1, to)
        var sum = 0
        for (i in 0 until checkString.length) {
            sum = sum xor checkString[i].toInt()
        }
        return String.format("%02X", sum)
    }
}