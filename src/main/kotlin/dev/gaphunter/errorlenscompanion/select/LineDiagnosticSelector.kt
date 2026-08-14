package dev.gaphunter.errorlenscompanion.select

import dev.gaphunter.errorlenscompanion.model.DiagnosticInfo

/**
 * A source line can carry several diagnostics at once (an unresolved
 * reference AND a style warning on the same line, for example). Real
 * Error Lens-style tools show exactly one inline hint per line -- more
 * than that turns into unreadable clutter fast. This picks the most
 * severe diagnostic per line, keeping first-seen order on ties.
 */
object LineDiagnosticSelector {

    fun selectOnePerLine(diagnostics: List<DiagnosticInfo>): List<DiagnosticInfo> {
        val byLine = LinkedHashMap<Int, DiagnosticInfo>()
        for (diagnostic in diagnostics) {
            val existing = byLine[diagnostic.lineNumber]
            if (existing == null || diagnostic.severity.weight > existing.severity.weight) {
                byLine[diagnostic.lineNumber] = diagnostic
            }
        }
        return byLine.values.toList()
    }
}
