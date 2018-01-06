/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2017-2018 Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jraf.routetools.lib.tools.formatter

import com.mapbox.services.commons.models.Position
import org.jraf.routetools.lib.model.Speed
import org.jraf.routetools.lib.tools.bearing.BearingInfer
import java.util.Locale


object AndroidEmulatorFormatter : Formatter {
    override fun format(positionList: List<Position>, speed: Speed, delayBetweenPositionsSecond: Int): String {
        val res = StringBuilder("""#!/usr/bin/expect

namespace path {::tcl::mathop}

set ip [lindex ${'$'}argv 0]
set port [lindex ${'$'}argv 1]
set date [clock format [clock seconds] -format %d%m%y]

set timeout 1
spawn telnet ${'$'}ip ${'$'}port
expect_after eof { exit 0 }

proc checksum {s} {
  set cs 0
  foreach char [split ${'$'}s {}] {
    scan ${'$'}char "%c" c
    set cs [^ ${'$'}cs ${'$'}c]
  }
  return [format "%02X" ${'$'}cs]
}


# Interact
expect OK

# Auth token
set fp [open "~/.emulator_console_auth_token" r]
if {[gets ${'$'}fp line] != -1} {
  send "auth ${'$'}line\r"
}

""")
        val positionAheadIndex = BearingInfer.DEFAULT_POSITION_COUNT / 2
        val bearingInfer = BearingInfer()
        for ((index, position) in positionList.withIndex()) {
            if (index + positionAheadIndex < positionList.lastIndex) {
                bearingInfer.add(positionList[index + positionAheadIndex])
            }

            val latitude = String.format(Locale.US, "%02d%.3f,%s", position.latitudeDegrees, position.latitudeMinutes, position.latitudeNorthSouth)
            val longitude = String.format(Locale.US, "%03d%.3f,%s", position.longitudeDegrees, position.longitudeMinutes, position.longitudeEastWest)
            val bearing = String.format(Locale.US, "%.1f", bearingInfer.bearing)
            val speedStr = String.format(Locale.US, "%.1f", speed.toMetersPerSecond())

            res.append("# $index  lat: ${position.latitude} ($latitude)  lon: ${position.longitude} ($longitude)  bear: $bearing  speed: $speedStr\n")
            res.append("set s \"GPRMC,[clock format [clock seconds] -format %H%M%S],A,$latitude,$longitude,$speedStr,$bearing,\$date,,\"\n")
            res.append("set s \"geo nmea \\\$\$s*[checksum \$s]\"\n")
            res.append("send \"\$s\\r\"\n")
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