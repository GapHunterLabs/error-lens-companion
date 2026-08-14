package dev.gaphunter.errorlenscompanion.model

/**
 * One diagnostic already computed by the IDE's own highlighting passes,
 * reduced to exactly what Error Lens Companion needs to pick a line and
 * render text. Deliberately a plain, constructible data class -- NOT a
 * wrapper around the real platform `HighlightInfo` -- so the selection
 * and formatting logic around it stays testable with plain JUnit, no
 * IDE/project boot required.
 */
data class DiagnosticInfo(
    val severity: DiagnosticSeverity,
    val message: String,
    val lineNumber: Int,
    val lineEndOffset: Int,
)
