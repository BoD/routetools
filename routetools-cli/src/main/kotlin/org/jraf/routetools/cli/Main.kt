package org.jraf.routetools.cli

import com.beust.jcommander.JCommander
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
            val arguments = Arguments()
            JCommander.newBuilder()
                    .addObject(arguments)
                    .build()
                    .parse(*av)

            val polylineStr = arguments.polyline
            val lineString = try {
                LineString.fromPolyline(polylineStr, arguments.precision)
            } catch (t: Throwable) {
                throw IllegalArgumentException("Could not parse the polyline", t)
            }
            val positions = lineString.coordinates
            val speed = Speed(Distance(30.0, DistanceUnit.KILOMETERS), Time(1, TimeUnit.HOURS))
            val interpolatedPositions = RouteUtil.interpolate(positions, speed, 1)
            val noisePositions = RouteUtil.addNoise(interpolatedPositions)

            val formatted = FormatUtil.format(noisePositions, FormatUtil.Format.valueOf(arguments.format.toUpperCase()))
            if (arguments.outputFile != null) {
                arguments.outputFile?.writeText(formatted)
            } else {
                System.out.println(formatted)
            }
        }
    }
}