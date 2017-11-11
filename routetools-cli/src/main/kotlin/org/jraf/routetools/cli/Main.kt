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
            val jCommander = JCommander.newBuilder()
                    .addObject(arguments)
                    .build()
            jCommander.parse(*av)

            if (arguments.help) {
                jCommander.usage()
                return
            }

            // Decode polyline
            val polylineStr = arguments.polyline
            val lineString = try {
                LineString.fromPolyline(polylineStr, arguments.precision)
            } catch (t: Throwable) {
                throw IllegalArgumentException("Could not parse the polyline", t)
            }
            var positions = lineString.coordinates

            // Interpolate
            val speed = Speed(Distance(arguments.speed.toDouble(), DistanceUnit.KILOMETERS), Time(1, TimeUnit.HOURS))
            positions = RouteUtil.interpolate(positions, speed, arguments.delayBetweenPositionsSecond)

            // Noise
            if (arguments.addNoise) {
                positions = RouteUtil.addNoise(positions)
            }

            // Format
            val formatted = FormatUtil.format(positions, arguments.format)

            // Output
            if (arguments.outputFile != null) {
                arguments.outputFile?.writeText(formatted)
            } else {
                System.out.println(formatted)
            }
        }
    }
}