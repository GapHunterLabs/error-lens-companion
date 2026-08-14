package dev.gaphunter.errorlenscompanion.highlight

import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Confirms the factory wiring compiles and functions against a real
 * project/editor -- doesn't race the daemon's own timing (see
 * README.md "Known limitations" for what that leaves unverified).
 *
 * Real, useful discovery from writing this test: the platform's own
 * `TextEditorHighlightingPass` base constructor asserts it is NOT
 * called on the EDT (`ThreadingAssertions.assertBackgroundThread()`).
 * In production the registrar always calls `createHighlightingPass` off
 * the EDT, so calling it directly from the (EDT) test thread throws --
 * not a bug in the pass, a mismatch in how the first version of this
 * test called it. Fixed by running the call on a pooled thread, the
 * same way the platform actually does.
 */
class ErrorLensPassFactoryTest : BasePlatformTestCase() {

    fun testCreateHighlightingPassReturnsARealPassForANormalFile() {
        myFixture.configureByText("Sample.txt", "hello world\n")
        val file = myFixture.file
        val editor = myFixture.editor

        val pass = ApplicationManager.getApplication().executeOnPooledThread<Any?> {
            ErrorLensPassFactory().createHighlightingPass(file, editor)
        }.get()

        assertNotNull(pass)
        assertTrue(pass is ErrorLensHighlightingPass)
    }
}
