package dev.gaphunter.errorlenscompanion.format

import dev.gaphunter.errorlenscompanion.model.DiagnosticInfo

/**
 * Turns a [DiagnosticInfo] into the exact text painted at the end of its
 * line. Kept pure and separate from [dev.gaphunter.errorlenscompanion.render.ErrorLensInlayRenderer]
 * so the truncation/whitespace rules are unit-testable without touching
 * Swing or the editor at all.
 */
object InlineTextFormatter {

    private const val MAX_MESSAGE_LENGTH = 120
    private const val ELLIPSIS = "…"

    fun format(diagnostic: DiagnosticInfo): String {
        val singleLine = diagnostic.message.replace('\n', ' ').replace('\r', ' ').trim()
        val body = if (singleLine.length > MAX_MESSAGE_LENGTH) {
            singleLine.take(MAX_MESSAGE_LENGTH - ELLIPSIS.length) + ELLIPSIS
        } else {
            singleLine
        }
        return "${diagnostic.severity.icon} $body"
    }
}
