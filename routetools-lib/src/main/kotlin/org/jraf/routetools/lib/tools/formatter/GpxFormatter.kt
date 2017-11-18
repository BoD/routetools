package org.jraf.routetools.lib.tools.formatter

import com.mapbox.services.commons.models.Position
import io.jenetics.jpx.GPX
import org.jraf.routetools.lib.model.Speed
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets


object GpxFormatter : Formatter {
    override fun format(positionList: List<Position>, speed: Speed, delayBetweenPositionsSecond: Int): String {
        var time = System.currentTimeMillis()
        val gpx = GPX.builder()
                .creator("routetools")
                .addTrack { track ->
                    track.addSegment { segment ->
                        for ((index, position) in positionList.withIndex()) {
                            segment.addPoint { p ->
                                p.lat(position.latitude)
                                        .lon(position.longitude)
                                        .desc("$index")
                                        .cmt("$index")
                                        .name("$index")
                                        .speed(speed.toMetersPerSecond())
                                        .time(time)
                            }

                            time += delayBetweenPositionsSecond * 1000
                        }
                    }
                }
                .build()

        val output = ByteArrayOutputStream()
        GPX.write(gpx, output, "\t")
        return output.toString(StandardCharsets.UTF_8.name())
    }
}