/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2017-2018 Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jraf.routetools.cli

import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import org.jraf.routetools.lib.tools.formatter.Format
import java.io.File


class Arguments {
    @Parameter(
            names = ["-h", "--help"],
            description = "Show this help",
            help = true
    )
    var help: Boolean = false

    @Parameter(
            names = ["-ip", "--input-polyline"],
            description = "Input, as a polyline"
    )
    var inputPolyline: String? = null

    @Parameter(
            names = ["-pp", "--input-polyline-precision"],
            description = "The input polyline precision, either 5 or 6",
            validateWith = [(PolylinePrecisionValidator::class)]
    )
    var precision: Int = 5

    @Parameter(
            names = ["-ig", "--input-gpx"],
            description = "Input, as a gpx file",
            validateWith = [(FileExistsValidator::class)]
    )
    var inputGpxFile: File? = null

    @Parameter(
            names = ["-o", "--output"],
            description = "Output file.  If not specified, the output is stdout"
    )
    var outputFile: File? = null

    @Parameter(
            names = ["-f", "--format"],
            description = "Output format"
    )
    var format: Format = Format.GPX

    @Parameter(
            names = ["-n", "--noise"],
            description = "Add a random distance to each point, withing the given range (in meters - 0 for no noise)"
    )
    var noiseRangeMeters: Double = 0.0

    @Parameter(
            names = ["-d", "--delay-positions"],
            description = "Delay between each position, in seconds"
    )
    var delayBetweenPositionsSecond = 1

    @Parameter(
            names = ["-s", "--speed"],
            description = "Speed, in km/h"
    )
    var speed = 30

    @Parameter(
            names = ["-ec", "--exclude-close-positions"],
            description = "Exclude positions that are closer than the given value (in meters - 0 for no exclusion)"
    )
    var excludeClosePositionsMeters: Double = 2.0
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

class FileExistsValidator : IParameterValidator {
    @Throws(ParameterException::class)
    override fun validate(name: String, value: String) {
        if (!File(value).exists()) {
            throw ParameterException("Could not find file $value")
        }
    }
}
