package org.jraf.routetools.lib.tools

import com.mapbox.services.api.utils.turf.TurfConstants
import com.mapbox.services.api.utils.turf.TurfMeasurement
import com.mapbox.services.commons.models.Position
import org.jraf.routetools.lib.model.Speed

object RouteUtil {
    private const val NOISE_RANGE = .0001

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

    fun addNoise(positions: List<Position>): List<Position> {
        val res = mutableListOf<Position>()
        for (position in positions) {
            res += Position.fromCoordinates(position.longitude + Math.random() * NOISE_RANGE - NOISE_RANGE / 2,
                    position.latitude + Math.random() * NOISE_RANGE - NOISE_RANGE / 2)
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