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

    /**
     * [canDisplay] tells whether the font the text will be drawn with has
     * a glyph for a given icon; when it doesn't, the severity's Latin-1
     * fallback is used instead of a box.
     */
    fun format(diagnostic: DiagnosticInfo, canDisplay: (String) -> Boolean = { true }): String {
        val singleLine = diagnostic.message.replace('\n', ' ').replace('\r', ' ').trim()
        val body = if (singleLine.length > MAX_MESSAGE_LENGTH) {
            singleLine.take(MAX_MESSAGE_LENGTH - ELLIPSIS.length) + ELLIPSIS
        } else {
            singleLine
        }
        val severity = diagnostic.severity
        val icon = if (canDisplay(severity.icon)) severity.icon else severity.fallbackIcon
        return "$icon $body"
    }
}
