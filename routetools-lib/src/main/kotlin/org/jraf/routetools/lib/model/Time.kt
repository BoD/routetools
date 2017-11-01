package org.jraf.routetools.lib.model

import java.util.concurrent.TimeUnit

data class Time(private val value: Long, private val unit: TimeUnit) {
    fun toNanos() = unit.toNanos(value)
    fun toMicros() = unit.toMicros(value)
    fun toMillis() = unit.toMillis(value)
    fun toSeconds() = unit.toSeconds(value)
    fun toMinutes() = unit.toMinutes(value)
    fun toHours() = unit.toHours(value)
    fun toDays() = unit.toDays(value)
}