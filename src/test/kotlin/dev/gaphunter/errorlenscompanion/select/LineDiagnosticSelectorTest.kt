package dev.gaphunter.errorlenscompanion.select

import dev.gaphunter.errorlenscompanion.model.DiagnosticInfo
import dev.gaphunter.errorlenscompanion.model.DiagnosticSeverity
import org.junit.Assert.assertEquals
import org.junit.Test

class LineDiagnosticSelectorTest {

    private fun diagnostic(severity: DiagnosticSeverity, line: Int, message: String) =
        DiagnosticInfo(severity = severity, message = message, lineNumber = line, lineEndOffset = line * 100)

    @Test
    fun testOneDiagnosticPerLinePassesThroughUnchanged() {
        val input = listOf(
            diagnostic(DiagnosticSeverity.ERROR, 0, "boom"),
            diagnostic(DiagnosticSeverity.WARNING, 1, "careful"),
        )

        val result = LineDiagnosticSelector.selectOnePerLine(input)

        assertEquals(2, result.size)
        assertEquals("boom", result[0].message)
        assertEquals("careful", result[1].message)
    }

    @Test
    fun testTwoDiagnosticsOnSameLineKeepsTheMoreSevereOne() {
        val input = listOf(
            diagnostic(DiagnosticSeverity.WEAK_WARNING, 3, "minor style nit"),
            diagnostic(DiagnosticSeverity.ERROR, 3, "unresolved reference"),
        )

        val result = LineDiagnosticSelector.selectOnePerLine(input)

        assertEquals(1, result.size)
        assertEquals("unresolved reference", result[0].message)
    }

    @Test
    fun testTieOnSeverityKeepsTheFirstSeenOne() {
        val input = listOf(
            diagnostic(DiagnosticSeverity.WARNING, 5, "first warning"),
            diagnostic(DiagnosticSeverity.WARNING, 5, "second warning"),
        )

        val result = LineDiagnosticSelector.selectOnePerLine(input)

        assertEquals(1, result.size)
        assertEquals("first warning", result[0].message)
    }

    @Test
    fun testEmptyInputProducesEmptyOutput() {
        assertEquals(emptyList<DiagnosticInfo>(), LineDiagnosticSelector.selectOnePerLine(emptyList()))
    }

    @Test
    fun testPreservesLineOrderAcrossManyLines() {
        val input = listOf(
            diagnostic(DiagnosticSeverity.WARNING, 7, "seven"),
            diagnostic(DiagnosticSeverity.ERROR, 2, "two"),
            diagnostic(DiagnosticSeverity.WEAK_WARNING, 4, "four"),
        )

        val result = LineDiagnosticSelector.selectOnePerLine(input)

        assertEquals(listOf("seven", "two", "four"), result.map { it.message })
    }
}
