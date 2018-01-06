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

import com.mapbox.services.commons.models.Position

class BearingInfer(private val positionCount: Int = 4) {
    private val positions = mutableListOf<Position>()

    val bearing: Double
        get() {
            if (positions.size < 2) return 0.0
            val bearings = mutableListOf<Double>()
            for (i in 0 until positions.size - 1) {
                val position0 = positions[i]
                val position1 = positions[i + 1]
                bearings.add(Bearing.bearing(position0, position1))
            }
            return Bearing.positiveAngle(Bearing.getMeanAngle(*bearings.toDoubleArray()))
        }

    fun add(position: Position) {
        positions += position
        if (positions.size > positionCount) positions.removeAt(0)
    }
}