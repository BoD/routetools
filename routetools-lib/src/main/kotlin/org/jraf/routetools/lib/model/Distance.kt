package org.jraf.routetools.lib.model

data class Distance(private val value: Double, private val unit: DistanceUnit) {
    fun toInch() = unit.toInch(value)
    fun toYard() = unit.toYard(value)
    fun toFeet() = unit.toFeet(value)
    fun toKilometers() = unit.toKilometers(value)
    fun toNauticalMiles() = unit.toNauticalMiles(value)
    fun toMillimeters() = unit.toMillimeters(value)
    fun toCentimeters() = unit.toCentimeters(value)
    fun toMiles() = unit.toMiles(value)
    fun toMeters() = unit.toMeters(value)
}