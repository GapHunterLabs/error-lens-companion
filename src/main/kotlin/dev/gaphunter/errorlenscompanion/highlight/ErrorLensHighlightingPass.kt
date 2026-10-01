package dev.gaphunter.errorlenscompanion.highlight

import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import dev.gaphunter.errorlenscompanion.model.DiagnosticInfo

/**
 * Reads the highlight results the IDE's OWN inspection/annotator passes
 * already computed for this file -- this pass never re-analyzes anything
 * itself, it only reads [DaemonCodeAnalyzerEx.processHighlights] after
 * those passes finish (registered to run after `Pass.UPDATE_ALL`, see
 * [ErrorLensPassFactory]) -- and turns errors/warnings into end-of-line
 * inlays. This is the first paint; [ErrorLensDaemonListener] refreshes the
 * hints once the whole highlighting session has finished (the platform
 * drops obsolete highlights only then).
 *
 * [doCollectInformation] runs off the EDT (per the platform contract for
 * this class) and only reads immutable [com.intellij.codeInsight.daemon.impl.HighlightInfo]
 * data; [doApplyInformationToEditor] runs on the EDT and is the only
 * place that touches [Editor]/[com.intellij.openapi.editor.InlayModel].
 */
class ErrorLensHighlightingPass(
    project: Project,
    private val editor: Editor,
) : TextEditorHighlightingPass(project, editor.document, false) {

    private var lineDiagnostics: List<DiagnosticInfo> = emptyList()

    override fun doCollectInformation(progress: ProgressIndicator) {
        lineDiagnostics = ErrorLensDiagnostics.collect(editor, myProject)
    }

    override fun doApplyInformationToEditor() {
        ErrorLensDiagnostics.apply(editor, lineDiagnostics)
    }
}
