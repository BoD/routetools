package org.jraf.routetools.cli

import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import org.jraf.routetools.lib.tools.formatter.Format
import java.io.File


class Arguments {
    @Parameter(
            names = arrayOf("-h", "--help"),
            description = "Show this help",
            help = true)
    var help: Boolean = false

    @Parameter(
            names = arrayOf("-i", "--input"),
            description = "Input, as a polyline",
            required = true
    )
    var polyline: String? = null

    @Parameter(
            names = arrayOf("-p", "--precision"),
            description = "The polyline precision, either 5 or 6",
            validateWith = arrayOf(PolylinePrecisionValidator::class)
    )
    var precision: Int = 5

    @Parameter(
            names = arrayOf("-o", "--output"),
            description = "Output file.  If not specified, the output is stdout"
    )
    var outputFile: File? = null

    @Parameter(
            names = arrayOf("-f", "--format"),
            description = "Output format"
    )
    var format: Format = Format.GPX

    @Parameter(
            names = arrayOf("-n", "--noise"),
            description = "Add a random distance to each point, withing the given range (in meters - 0 for no noise)"
    )
    var noiseRangeMeters: Double = 0.0

    @Parameter(
            names = arrayOf("-d", "--delay-positions"),
            description = "Delay between each position, in seconds"
    )
    var delayBetweenPositionsSecond = 1

    @Parameter(
            names = arrayOf("-s", "--speed"),
            description = "Speed, in km/h"
    )
    var speed = 30

}

class PolylinePrecisionValidator : IParameterValidator {
    @Throws(ParameterException::class)
    override fun validate(name: String, value: String) {
        val precision = Integer.parseInt(value)
        if (precision !in intArrayOf(5, 6)) {
            throw ParameterException("Parameter $name should be either 5 or 6 (was: $value)")
        }
    }
}
