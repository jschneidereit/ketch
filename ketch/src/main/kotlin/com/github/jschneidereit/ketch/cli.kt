import com.github.ajalt.mordant.TermColors
import com.github.jschneidereit.ketch.KetchConfig
import com.github.jschneidereit.ketch.ProgramName
import com.github.jschneidereit.ketch.ReporterKind
import com.github.jschneidereit.ketch.toReporter
import io.kotest.core.Tags
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType

internal fun cli(programName: ProgramName, args: Array<String>): KetchConfig {
    val parser = ArgParser(programName.value)

    val reporter by parser.option(ArgType.Choice<ReporterKind>(), shortName = "r", description = "Reporter kind")
    val term by parser.option(ArgType.Choice<TermColors.Level>(), shortName = "c", description = "Terminal colors")
    val tags by parser.option(ArgType.String, shortName = "t", description = "Kotest tags expression")

    val split = args.map { it.split("\\s".toRegex()) }.flatten().toTypedArray()
    parser.parse(split)

    return KetchConfig(reporter.toReporter(term), Tags(tags))
}