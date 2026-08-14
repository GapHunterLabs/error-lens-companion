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
}
