package org.jraf.routetools.lib.model

data class Speed(private val distance: Distance,
                 private val time: Time) {
    fun toMetersPerSecond() = distance.toMeters() / time.toSeconds()
    fun toKilometersPerHour() = distance.toKilometers() / time.toHours()
    fun toMilesPerHour() = distance.toMiles() / time.toHours()
}