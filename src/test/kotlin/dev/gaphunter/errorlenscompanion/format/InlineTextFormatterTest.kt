package dev.gaphunter.errorlenscompanion.format

import dev.gaphunter.errorlenscompanion.model.DiagnosticInfo
import dev.gaphunter.errorlenscompanion.model.DiagnosticSeverity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InlineTextFormatterTest {

    private fun diagnostic(severity: DiagnosticSeverity, message: String) =
        DiagnosticInfo(severity = severity, message = message, lineNumber = 0, lineEndOffset = 0)

    @Test
    fun testErrorIsPrefixedWithTheErrorIcon() {
        val text = InlineTextFormatter.format(diagnostic(DiagnosticSeverity.ERROR, "cannot resolve symbol 'x'"))
        assertEquals("✖ cannot resolve symbol 'x'", text)
    }

    @Test
    fun testWarningIsPrefixedWithTheWarningIcon() {
        val text = InlineTextFormatter.format(diagnostic(DiagnosticSeverity.WARNING, "unused import"))
        assertEquals("⚠ unused import", text)
    }

    @Test
    fun testWeakWarningIsPrefixedWithTheInfoIcon() {
        val text = InlineTextFormatter.format(diagnostic(DiagnosticSeverity.WEAK_WARNING, "redundant cast"))
        assertEquals("ℹ redundant cast", text)
    }

    @Test
    fun testMultilineMessageIsCollapsedToOneLine() {
        val text = InlineTextFormatter.format(diagnostic(DiagnosticSeverity.ERROR, "first line\nsecond line"))
        assertEquals("✖ first line second line", text)
    }

    @Test
    fun testLeadingAndTrailingWhitespaceIsTrimmed() {
        val text = InlineTextFormatter.format(diagnostic(DiagnosticSeverity.WARNING, "  spaced out  "))
        assertEquals("⚠ spaced out", text)
    }

    @Test
    fun testVeryLongMessageIsTruncatedWithAnEllipsis() {
        val longMessage = "x".repeat(200)
        val text = InlineTextFormatter.format(diagnostic(DiagnosticSeverity.ERROR, longMessage))
        assertTrue(text.endsWith("…"))
        assertTrue(text.length < longMessage.length)
    }

    @Test
    fun testShortMessageIsNeverTruncated() {
        val text = InlineTextFormatter.format(diagnostic(DiagnosticSeverity.ERROR, "short"))
        assertEquals("✖ short", text)
    }

    // Regression (2026-10-01): the editor font (JetBrains Mono by default)
    // has no "✖", and every inline error started with an empty box.
    @Test
    fun testIconTheFontCannotDisplayIsReplacedByItsFallback() {
        val noDingbats: (String) -> Boolean = { icon -> icon != "✖" }
        assertEquals("× cannot resolve symbol 'x'", InlineTextFormatter.format(diagnostic(DiagnosticSeverity.ERROR, "cannot resolve symbol 'x'"), noDingbats))
        assertEquals("⚠ unused import", InlineTextFormatter.format(diagnostic(DiagnosticSeverity.WARNING, "unused import"), noDingbats))
    }

    @Test
    fun testFallbackIconsAreLatin1SoAnyEditorFontHasThem() {
        for (severity in DiagnosticSeverity.entries) {
            assertTrue("${severity.name}: ${severity.fallbackIcon}", severity.fallbackIcon.all { it.code < 256 })
        }
    }

    @Test
    fun testFallbackIsCheckedAgainstARealFont() {
        val font = java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.ITALIC, 14)
        val text = InlineTextFormatter.format(diagnostic(DiagnosticSeverity.ERROR, "x")) { icon -> font.canDisplayUpTo(icon) == -1 }
        val icon = text.substringBefore(' ')
        assertEquals("The chosen icon must be drawable with the font: $icon", -1, font.canDisplayUpTo(icon))
    }
}
