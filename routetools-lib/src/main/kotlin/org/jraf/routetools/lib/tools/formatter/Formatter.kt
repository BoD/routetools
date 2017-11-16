package org.jraf.routetools.lib.tools.formatter

import com.mapbox.services.commons.models.Position
import org.jraf.routetools.lib.model.Speed

interface Formatter {
    fun format(positionList: List<Position>, speed: Speed, delayBetweenPositionsSecond: Int): String
}