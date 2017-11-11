package org.jraf.routetools.cli

import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import org.jraf.routetools.lib.tools.FormatUtil
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
    var format: FormatUtil.Format = FormatUtil.Format.GPX
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
