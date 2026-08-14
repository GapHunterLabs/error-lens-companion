package dev.gaphunter.errorlenscompanion.render

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Exercises the real `InlayModel.addAfterLineEndElement` / `Disposer`
 * calls -- the part of Error Lens Companion most worth de-risking
 * without a screenshot, since it's the mechanism that would silently do
 * nothing (or silently pile up stale inlays) if wired wrong. What this
 * does NOT verify is the actual on-screen pixels -- see README.md
 * "Known limitations".
 */
class ErrorLensInlayManagerTest : BasePlatformTestCase() {

    fun testReplaceInlaysAddsOneInlayPerEntry() {
        myFixture.configureByText("Sample.txt", "line one\nline two\nline three\n")
        val editor = myFixture.editor
        val document = editor.document

        ErrorLensInlayManager.replaceInlays(
            editor,
            listOf(
                document.getLineEndOffset(0) to "✖ boom",
                document.getLineEndOffset(1) to "⚠ careful",
            ),
        )

        val inlays = editor.inlayModel.getAfterLineEndElementsInRange(0, document.textLength)
        assertEquals(2, inlays.size)
    }

    fun testReplaceInlaysDisposesPreviousInlaysInsteadOfAccumulating() {
        myFixture.configureByText("Sample.txt", "line one\nline two\n")
        val editor = myFixture.editor
        val document = editor.document

        ErrorLensInlayManager.replaceInlays(editor, listOf(document.getLineEndOffset(0) to "✖ first"))
        ErrorLensInlayManager.replaceInlays(editor, listOf(document.getLineEndOffset(0) to "✖ second"))

        val inlays = editor.inlayModel.getAfterLineEndElementsInRange(0, document.textLength)
        assertEquals(1, inlays.size)
    }

    fun testReplaceInlaysWithEmptyListClearsExistingOnes() {
        myFixture.configureByText("Sample.txt", "line one\n")
        val editor = myFixture.editor
        val document = editor.document

        ErrorLensInlayManager.replaceInlays(editor, listOf(document.getLineEndOffset(0) to "✖ gone soon"))
        ErrorLensInlayManager.replaceInlays(editor, emptyList())

        val inlays = editor.inlayModel.getAfterLineEndElementsInRange(0, document.textLength)
        assertEquals(0, inlays.size)
    }
}
