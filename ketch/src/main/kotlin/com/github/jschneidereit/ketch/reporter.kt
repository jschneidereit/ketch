package com.github.jschneidereit.ketch

import com.github.ajalt.mordant.TermColors
import io.kotest.engine.reporter.ConsoleReporter
import io.kotest.engine.reporter.IsolatedReporter
import io.kotest.engine.reporter.TaycanConsoleReporter
import io.kotest.engine.reporter.TeamCityConsoleReporter

internal fun ReporterKind?.toReporter(terminal: TermColors.Level?) = when (this) {
    null -> TaycanConsoleReporter()
    ReporterKind.TAYCAN -> TaycanConsoleReporter()
    ReporterKind.TEAMCITY -> TeamCityConsoleReporter()
}.also {
    val term = TermColors(terminal ?: TermColors.Level.ANSI256)
    if (it is ConsoleReporter) { it.setTerm(term) }
}.also {
    IsolatedReporter(it)
}