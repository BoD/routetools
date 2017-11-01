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