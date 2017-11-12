package org.jraf.routetools.lib.tools

import com.mapbox.services.api.utils.turf.TurfMeasurement
import com.mapbox.services.commons.models.Position

class BearingInfer {
    private val positions = mutableListOf<Position>()

    val bearing: Double
        get() {
            if (positions.size < 2) return 0.0
            val bearings = mutableListOf<Double>()
            for (i in 0 until positions.size - 1) {
                val position0 = positions[i]
                val position1 = positions[i + 1]
                bearings.add(bearing(position0, position1))
            }
            return positiveAngle(getMeanAngle(*bearings.toDoubleArray()))
        }

    fun add(position: Position) {
        positions += position
        if (positions.size > POSITION_COUNT) positions.removeAt(0)
    }

    companion object {
        private const val POSITION_COUNT = 4

        private fun bearing(position0: Position, position1: Position): Double {
            val res = TurfMeasurement.bearing(position0, position1)
            return positiveAngle(res)
        }

        // Convert -180..180 to 0..360
        private fun positiveAngle(angle: Double) = if (angle < 0) angle + 360.0 else angle

        private fun getMeanAngle(vararg anglesDeg: Double): Double {
            var x = 0.0
            var y = 0.0

            for (angleD in anglesDeg) {
                val angleR = Math.toRadians(angleD)
                x += Math.cos(angleR)
                y += Math.sin(angleR)
            }
            val avgR = Math.atan2(y / anglesDeg.size, x / anglesDeg.size)
            return Math.toDegrees(avgR)
        }
    }
}