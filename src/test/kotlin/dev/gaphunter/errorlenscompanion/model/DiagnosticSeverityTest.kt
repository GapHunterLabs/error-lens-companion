package dev.gaphunter.errorlenscompanion.model

import com.intellij.lang.annotation.HighlightSeverity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DiagnosticSeverityTest {

    @Test
    fun testErrorMapsToOurErrorSeverity() {
        assertEquals(DiagnosticSeverity.ERROR, DiagnosticSeverity.fromPlatformSeverity(HighlightSeverity.ERROR))
    }

    @Test
    fun testWarningMapsToOurWarningSeverity() {
        assertEquals(DiagnosticSeverity.WARNING, DiagnosticSeverity.fromPlatformSeverity(HighlightSeverity.WARNING))
    }

    @Test
    fun testWeakWarningMapsToOurWeakWarningSeverity() {
        assertEquals(DiagnosticSeverity.WEAK_WARNING, DiagnosticSeverity.fromPlatformSeverity(HighlightSeverity.WEAK_WARNING))
    }

    @Test
    fun testInformationIsDeliberatelyFilteredOut() {
        assertNull(DiagnosticSeverity.fromPlatformSeverity(HighlightSeverity.INFORMATION))
    }

    @Test
    fun testErrorOutweighsWarningOutweighsWeakWarning() {
        assert(DiagnosticSeverity.ERROR.weight > DiagnosticSeverity.WARNING.weight)
        assert(DiagnosticSeverity.WARNING.weight > DiagnosticSeverity.WEAK_WARNING.weight)
    }
}
