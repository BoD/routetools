package org.jraf.routetools.lib.tools.formatter

import com.mapbox.services.commons.models.Position
import org.jraf.routetools.lib.model.Speed

object KmlFormatter : Formatter {
    override fun format(positionList: List<Position>, speed: Speed, delayBetweenPositionsSecond: Int): String {
        val res = StringBuilder("""<?xml version="1.0" encoding="utf-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2">
  <Document>
""")
        for ((index, position) in positionList.withIndex()) {
            res.append("<!-- $index --><Placemark><Point><coordinates>${position.longitude},${position.latitude},0</coordinates></Point></Placemark>\n")
        }
        res.append("""  </Document>
</kml>""")
        return res.toString()
    }
}