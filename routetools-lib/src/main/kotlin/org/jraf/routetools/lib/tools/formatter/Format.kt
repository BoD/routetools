package org.jraf.routetools.lib.tools.formatter

import com.mapbox.services.commons.models.Position
import org.jraf.routetools.lib.model.Speed

enum class Format(private val formatter: Formatter) : Formatter {
    GPX(GpxFormatter),
    KML(KmlFormatter),
    ANDROID_EMULATOR(AndroidEmulatorFormatter), ;

    override fun format(positionList: List<Position>, speed: Speed, delayBetweenPositionsSecond: Int) = formatter.format(positionList, speed, delayBetweenPositionsSecond)
}