package org.jraf.routetools.cli

import com.mapbox.services.Constants
import com.mapbox.services.commons.geojson.LineString
import org.jraf.routetools.lib.model.Distance
import org.jraf.routetools.lib.model.DistanceUnit
import org.jraf.routetools.lib.model.Speed
import org.jraf.routetools.lib.model.Time
import org.jraf.routetools.lib.tools.FormatUtil
import org.jraf.routetools.lib.tools.RouteUtil
import java.util.concurrent.TimeUnit

class Main {
    companion object {
        @JvmStatic
        fun main(av: Array<String>) {
            val polylineStr = av[0]
            val lineString = LineString.fromPolyline(polylineStr, Constants.PRECISION_5)
            val positions = lineString.coordinates
            val speed = Speed(Distance(30.0, DistanceUnit.KILOMETERS), Time(1, TimeUnit.HOURS))
            val interpolatedPositions =  RouteUtil.interpolate(positions, speed, 1)
            val noisePositions = RouteUtil.addNoise(interpolatedPositions)
            System.out.println(FormatUtil.asGpx(noisePositions))
        }
    }
}