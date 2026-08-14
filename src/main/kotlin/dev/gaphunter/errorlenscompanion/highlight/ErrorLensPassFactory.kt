package dev.gaphunter.errorlenscompanion.highlight

import com.intellij.codeHighlighting.Pass
import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactoryRegistrar
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

/**
 * Registers [ErrorLensHighlightingPass] to run after the IDE's own
 * `Pass.UPDATE_ALL` (general highlighting) pass finishes, so it only
 * ever reads already-finished [com.intellij.codeInsight.daemon.impl.HighlightInfo]
 * results -- it never triggers or duplicates any analysis itself.
 */
class ErrorLensPassFactory : TextEditorHighlightingPassFactory, TextEditorHighlightingPassFactoryRegistrar {

    override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project) {
        registrar.registerTextEditorHighlightingPass(
            this,
            null,
            intArrayOf(Pass.UPDATE_ALL),
            false,
            -1,
        )
    }

    override fun createHighlightingPass(file: PsiFile, editor: Editor): TextEditorHighlightingPass? {
        if (editor.isOneLineMode) return null
        if (!file.isPhysical) return null
        return ErrorLensHighlightingPass(file.project, editor)
    }
}
