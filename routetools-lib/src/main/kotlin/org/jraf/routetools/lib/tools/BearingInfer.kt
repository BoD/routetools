package org.jraf.routetools.lib.tools

import com.mapbox.services.api.utils.turf.TurfMeasurement
import com.mapbox.services.commons.geojson.Point
import com.mapbox.services.commons.models.Position

class BearingInfer {
    companion object {
        const val MAX_POSITIONS = 6
    }

    private val positions = mutableListOf<Position>()

    val bearing: Double
        get() {
            if (positions.size < 2) return 0.0
            var lat0 = 0.0
            var lon0 = 0.0
            var lat1 = 0.0
            var lon1 = 0.0
            val subList0 = positions.subList(0, positions.size / 2)
            for (position in subList0) {
                lat0 += position.latitude
                lon0 += position.longitude
            }
            lat0 /= subList0.size
            lon0 /= subList0.size

            val subList1 = positions.subList(positions.size / 2, positions.size)
            for (position in subList1) {
                lat1 += position.latitude
                lon1 += position.longitude
            }
            lat1 /= subList1.size
            lon1 /= subList1.size

            var res = TurfMeasurement.bearing(Point.fromCoordinates(doubleArrayOf(lon0, lat0)), Point.fromCoordinates(doubleArrayOf(lon1, lat1)))

            // Convert -180..180 to 0..360
            if (res < 0) res += 360
            return res
        }

    fun add(position: Position) {
        positions += position
        if (positions.size > MAX_POSITIONS) positions.removeAt(0)
    }
}