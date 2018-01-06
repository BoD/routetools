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
import io.jenetics.jpx.GPX
import org.jraf.routetools.lib.model.Speed
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets


object GpxFormatter : Formatter {
    override fun format(positionList: List<Position>, speed: Speed, delayBetweenPositionsSecond: Int): String {
        var time = System.currentTimeMillis()
        val gpx = GPX.builder()
                .creator("routetools")
                .addTrack { track ->
                    track.addSegment { segment ->
                        for ((index, position) in positionList.withIndex()) {
                            segment.addPoint { p ->
                                p.lat(position.latitude)
                                        .lon(position.longitude)
                                        .desc("$index")
                                        .cmt("$index")
                                        .name("$index")
                                        .speed(speed.toMetersPerSecond())
                                        .time(time)
                            }

                            time += delayBetweenPositionsSecond * 1000
                        }
                    }
                }
                .build()

        val output = ByteArrayOutputStream()
        GPX.write(gpx, output, "\t")
        return output.toString(StandardCharsets.UTF_8.name())
    }
}