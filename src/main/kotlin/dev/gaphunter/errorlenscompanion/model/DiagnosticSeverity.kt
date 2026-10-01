package dev.gaphunter.errorlenscompanion.model

import com.intellij.lang.annotation.HighlightSeverity

/**
 * Error Lens Companion's own severity scale -- deliberately NOT the same
 * type as the platform's [HighlightSeverity]. [HighlightSeverity] carries
 * dozens of language-specific and internal levels (spell-check, server
 * problems, text attributes markers...); we only ever show three, and
 * defining our own small enum with an explicit [weight] keeps
 * [dev.gaphunter.errorlenscompanion.select.LineDiagnosticSelector] and
 * [dev.gaphunter.errorlenscompanion.format.InlineTextFormatter] testable
 * with plain JUnit, no IDE/project boot required.
 */
enum class DiagnosticSeverity(val icon: String, val weight: Int, val fallbackIcon: String) {
    // fallbackIcon: Latin-1, present in practically every editor font. The
    // inline text is drawn with the editor's own font, and Java2D draws a
    // glyph that font lacks as an empty box -- JetBrains Mono, the default
    // editor font, has no "✖" (seen 2026-10-01: every inline error started
    // with a box).
    ERROR("✖", 3, "×"),
    WARNING("⚠", 2, "!"),
    WEAK_WARNING("ℹ", 1, "i"),
    ;

    companion object {
        /**
         * Maps a real platform severity down to our three levels, or
         * `null` for anything we deliberately don't surface inline
         * (INFORMATION, spell-check, server-side, generic text
         * attributes...). Returning `null` here is how those get
         * filtered out before a single inlay is ever created.
         */
        fun fromPlatformSeverity(severity: HighlightSeverity): DiagnosticSeverity? = when (severity) {
            HighlightSeverity.ERROR -> ERROR
            HighlightSeverity.WARNING -> WARNING
            HighlightSeverity.WEAK_WARNING -> WEAK_WARNING
            else -> null
        }
    }
}
