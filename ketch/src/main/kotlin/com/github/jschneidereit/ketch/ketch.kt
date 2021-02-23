package com.github.jschneidereit.ketch

import cli
import com.github.ajalt.mordant.TermColors
import io.kotest.core.Tags
import io.kotest.core.config.configuration
import io.kotest.core.extensions.DiscoveryExtension
import io.kotest.core.spec.Spec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.config.DetectedProjectConfig
import io.kotest.engine.config.apply
import io.kotest.engine.extensions.EnabledConditionSpecDiscoveryExtension
import io.kotest.engine.extensions.IgnoredSpecDiscoveryExtension
import io.kotest.engine.extensions.TagsExcludedDiscoveryExtension
import io.kotest.engine.launcher.ReporterTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.reporter.*
import io.kotest.framework.discovery.Discovery
import io.kotest.framework.discovery.DiscoveryRequest
import io.kotest.framework.discovery.DiscoverySelector
import kotlin.reflect.KClass

inline class PackageName(val value: String)

inline class ProgramName(val value: String)

private enum class ExitCode(val code: Int) { SUCCESS(0), FAILURE(-1) }

enum class ReporterKind { TAYCAN, TEAMCITY; }

data class KetchConfig(val reporter: Reporter, val tags: Tags, val configuration: DetectedProjectConfig? = null)

fun ketch(
    programName: ProgramName,
    packageName: PackageName,
    args: Array<String>,
    listeners: List<TestEngineListener> = listOf()
): Int {
    val config = cli(programName, args)
    val reporter = ketch(packageName, listeners, config)

    return (if (reporter.hasErrors()) ExitCode.FAILURE else ExitCode.SUCCESS).code
}

fun ketch(
    packageName: PackageName,
    listeners: List<TestEngineListener> = listOf(),
    ketchConfig: KetchConfig = KetchConfig(ReporterKind.TAYCAN.toReporter(TermColors.Level.NONE), Tags.Empty)
): Reporter {
    val specs = scan(packageName)

    return launch(ketchConfig, specs, listeners)
}

private fun scan(packageName: PackageName): List<KClass<out Spec>> {
    val selector = DiscoverySelector.PackageDiscoverySelector(packageName.value)
    val request = DiscoveryRequest(selectors = listOf(selector))
    val extensions = listOf(
        IgnoredSpecDiscoveryExtension,
        EnabledConditionSpecDiscoveryExtension,
        TagsExcludedDiscoveryExtension,
    ) + configuration.extensions().filterIsInstance<DiscoveryExtension>()

    return Discovery(extensions).discover(request).specs
}

private fun launch(config: KetchConfig, specs: List<KClass<out Spec>>, listeners: List<TestEngineListener>): Reporter {
    config.configuration?.apply(configuration)

    val launcher = listeners
        .fold(KotestEngineLauncher(), { acc, listener -> acc.withListener(listener) })
        .withListener(ReporterTestEngineListener(config.reporter))
        .withSpecs(specs)
        .withTags(config.tags)
        .withDumpConfig(true)

    try {
        launcher.launch()
    } catch (e: Throwable) {
        println(e)
        e.printStackTrace()
        config.reporter.engineFinished(listOf(e))
    }

    return config.reporter
}


