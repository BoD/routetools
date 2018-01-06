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

package org.jraf.routetools.lib.tools.bearing

import com.mapbox.services.api.utils.turf.TurfMeasurement
import com.mapbox.services.commons.models.Position

object Bearing {
   fun bearing(position0: Position, position1: Position): Double {
       val res = TurfMeasurement.bearing(position0, position1)
       return positiveAngle(res)
   }

   // Convert -180..180 to 0..360
   fun positiveAngle(angle: Double) = if (angle < 0) angle + 360.0 else angle

   fun getMeanAngle(vararg anglesDeg: Double): Double {
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