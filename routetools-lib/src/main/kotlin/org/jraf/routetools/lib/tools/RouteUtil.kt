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

package org.jraf.routetools.lib.tools

import com.mapbox.services.api.utils.turf.TurfConstants
import com.mapbox.services.api.utils.turf.TurfMeasurement
import com.mapbox.services.commons.models.Position
import org.jraf.routetools.lib.model.Speed

object RouteUtil {
    fun interpolate(positions: List<Position>, speed: Speed, delayBetweenPositionsSecond: Int): List<Position> {
        val res = mutableListOf<Position>()
        val maxDistanceWithoutPointsMeters = delayBetweenPositionsSecond * speed.toMetersPerSecond()
        positions.forEachIndexed { index, position ->
            res += position
            if (index != positions.lastIndex) {
                val nextPosition = positions[index + 1]
                val distanceMeters = TurfMeasurement.distance(position, nextPosition, TurfConstants.UNIT_METERS)
                val neededPoints = (distanceMeters / maxDistanceWithoutPointsMeters).toInt()
                if (neededPoints > 0) res += divide(position, nextPosition, neededPoints)
            }
        }
        return res
    }

    fun addNoise(positions: List<Position>, noiseRangeMeters: Double): List<Position> {
        val res = mutableListOf<Position>()
        for (position in positions) {
            res += TurfMeasurement.destination(position, Math.random() * noiseRangeMeters, Math.random() * 360.0 - 180.0, TurfConstants.UNIT_METERS)
        }
        return res
    }


    private fun divide(from: Position, to: Position, numberOfPositions: Int): List<Position> {
        val heading = TurfMeasurement.bearing(from, to)
        val res = mutableListOf<Position>()

        var point: Position = from
        for (i in numberOfPositions downTo 0) {
            val dist = TurfMeasurement.distance(point, to, TurfConstants.UNIT_METERS)
            point = TurfMeasurement.destination(point, dist / (i + 1), heading, TurfConstants.UNIT_METERS)
            res += point
        }
        return res
    }

}