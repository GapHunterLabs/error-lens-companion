package dev.gaphunter.errorlenscompanion.highlight

import com.intellij.lang.LanguageAnnotators
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Regression test for a real bug found while recording the product demo
 * (2026-10-01): a hint stayed on its line after the problem was fixed,
 * because the highlighting pass read the highlights before the platform
 * dropped the obsolete ones. [ErrorLensDaemonListener] recomputes the
 * hints from the final highlights once highlighting has finished.
 */
class ErrorLensDaemonListenerTest : BasePlatformTestCase() {

    /** Reports an ERROR on every line containing "BAD" -- a stand-in for a real language's diagnostics. */
    private class BadLineAnnotator : Annotator {
        override fun annotate(element: PsiElement, holder: AnnotationHolder) {
            if (element !is PsiFile) return
            var start = 0
            for (line in element.text.split('\n')) {
                if (line.contains("BAD")) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "bad line").range(TextRange(start, start + line.length)).create()
                }
                start += line.length + 1
            }
        }
    }

    override fun setUp() {
        super.setUp()
        LanguageAnnotators.INSTANCE.addExplicitExtension(PlainTextLanguage.INSTANCE, BadLineAnnotator(), testRootDisposable)
    }

    private fun hints(): List<String> {
        val editor = myFixture.editor
        return editor.inlayModel.getAfterLineEndElementsInRange(0, editor.document.textLength).map { inlay ->
            (inlay.renderer as dev.gaphunter.errorlenscompanion.render.ErrorLensInlayRenderer).text
        }
    }

    private fun finishHighlighting() {
        myFixture.doHighlighting()
        val textEditor = FileEditorManager.getInstance(project).getSelectedEditor(myFixture.file.virtualFile) as? TextEditor
        val editors = listOfNotNull(textEditor)
        ErrorLensDaemonListener(project).daemonFinished(editors)
    }

    fun testHintsFollowTheFinalHighlightsWhenHighlightingFinishes() {
        myFixture.configureByText("notes.txt", "fine\nBAD line\nfine\n")
        finishHighlighting()
        assertEquals(1, hints().size)
        assertTrue(hints().single().contains("bad line"))

        WriteCommandAction.runWriteCommandAction(project) {
            myFixture.editor.document.setText("fine\ngood line\nfine\n")
        }
        finishHighlighting()
        assertEquals("The fixed line must not keep its hint", emptyList<String>(), hints())
    }
}
