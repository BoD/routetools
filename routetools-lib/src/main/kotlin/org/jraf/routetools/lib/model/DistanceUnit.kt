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

package org.jraf.routetools.lib.model

enum class DistanceUnit constructor(private val meters: Double) {
    INCH(0.0254),
    YARD(0.9144),
    FEET(0.3048),
    KILOMETERS(1000.0),
    NAUTICALMILES(1852.0),
    MILLIMETERS(0.001),
    CENTIMETERS(0.01),
    MILES(1609.344),
    METERS(1.0);

    fun toInch(distance: Double) = convert(distance, this, INCH)
    fun toYard(distance: Double) = convert(distance, this, YARD)
    fun toFeet(distance: Double) = convert(distance, this, FEET)
    fun toKilometers(distance: Double) = convert(distance, this, KILOMETERS)
    fun toNauticalMiles(distance: Double) = convert(distance, this, NAUTICALMILES)
    fun toMillimeters(distance: Double) = convert(distance, this, MILLIMETERS)
    fun toCentimeters(distance: Double) = convert(distance, this, CENTIMETERS)
    fun toMiles(distance: Double) = convert(distance, this, MILES)
    fun toMeters(distance: Double) = convert(distance, this, METERS)

    companion object {
        private fun convert(distance: Double, from: DistanceUnit, to: DistanceUnit) = if (from == to) {
            distance
        } else {
            distance * from.meters / to.meters
        }
    }
}