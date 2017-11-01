package org.jraf.routetools.lib.tools

import com.mapbox.services.commons.models.Position

object FormatUtil {
    fun asGpx(positionList: List<Position>): String {
        val res = StringBuilder("""<?xml version="1.0" encoding="UTF-8"?>
<gpx xsi:schemaLocation="http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd" xmlns="http://www.topografix.com/GPX/1/0" creator="RouteUtil" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0">""")
        for (position in positionList) {
            res.append("<wpt lat=\"${position.latitude}\" lon=\"${position.longitude}\"/>\n")
        }
        res.append("</gpx>")
        return res.toString()
    }
}