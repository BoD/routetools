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

package org.jraf.routetools.cli

import com.beust.jcommander.JCommander
import com.mapbox.services.commons.geojson.LineString
import com.mapbox.services.commons.models.Position
import io.jenetics.jpx.GPX
import io.jenetics.jpx.Track
import io.jenetics.jpx.TrackSegment
import org.jraf.routetools.lib.model.Distance
import org.jraf.routetools.lib.model.DistanceUnit
import org.jraf.routetools.lib.model.Speed
import org.jraf.routetools.lib.model.Time
import org.jraf.routetools.lib.tools.RouteUtil
import java.util.concurrent.TimeUnit


class Main {
    companion object {
        @JvmStatic
        fun main(av: Array<String>) {
            val arguments = Arguments()
            val jCommander = JCommander.newBuilder()
                    .addObject(arguments)
                    .build()
            jCommander.parse(*av)

            if (arguments.help) {
                jCommander.usage()
                return
            }

            var positions: List<Position> = emptyList()

            if (arguments.inputPolyline == null && arguments.inputGpxFile == null) {
                System.err.println("Either a polyline or a gpx file must be provided as the input")
                System.exit(-1)
                return
            }

            // Input
            if (arguments.inputPolyline != null) {
                // Polyline
                val polylineStr = arguments.inputPolyline
                val lineString = try {
                    LineString.fromPolyline(polylineStr, arguments.precision)
                } catch (t: Throwable) {
                    throw IllegalArgumentException("Could not parse the inputPolyline", t)
                }
                positions = lineString.coordinates
            } else arguments.inputGpxFile?.let { inputGpxFile ->
                // Gpx file
                val mutablePositions = mutableListOf<Position>()
                GPX.read(inputGpxFile.inputStream()).tracks()
                        .limit(1)
                        .flatMap(Track::segments)
                        .flatMap(TrackSegment::points)
                        .forEach { mutablePositions.add(Position.fromCoordinates(it.longitude.toDegrees(), it.latitude.toDegrees())) }
                positions = mutablePositions
            }

            // Interpolate
            val speed = Speed(Distance(arguments.speed.toDouble(), DistanceUnit.KILOMETERS), Time(1, TimeUnit.HOURS))
            positions = RouteUtil.interpolate(positions, speed, arguments.delayBetweenPositionsSecond)

            // Noise
            if (arguments.noiseRangeMeters != 0.0) {
                positions = RouteUtil.addNoise(positions, arguments.noiseRangeMeters)
            }

            // Format
            val formatted = arguments.format.format(positions, speed, arguments.delayBetweenPositionsSecond)

            // Output
            if (arguments.outputFile != null) {
                arguments.outputFile?.writeText(formatted)
            } else {
                System.out.println(formatted)
            }
        }
    }
}