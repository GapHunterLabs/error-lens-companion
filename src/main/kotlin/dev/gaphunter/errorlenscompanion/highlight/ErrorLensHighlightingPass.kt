package dev.gaphunter.errorlenscompanion.highlight

import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerEx
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import dev.gaphunter.errorlenscompanion.format.InlineTextFormatter
import dev.gaphunter.errorlenscompanion.model.DiagnosticInfo
import dev.gaphunter.errorlenscompanion.model.DiagnosticSeverity
import dev.gaphunter.errorlenscompanion.render.ErrorLensInlayManager
import dev.gaphunter.errorlenscompanion.select.LineDiagnosticSelector

/**
 * Reads the highlight results the IDE's OWN inspection/annotator passes
 * already computed for this file -- this pass never re-analyzes anything
 * itself, it only reads [DaemonCodeAnalyzerEx.processHighlights] after
 * those passes finish (registered to run after `Pass.UPDATE_ALL`, see
 * [ErrorLensPassFactory]) -- and turns errors/warnings into end-of-line
 * inlays.
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
        val document = editor.document
        val collected = mutableListOf<DiagnosticInfo>()

        DaemonCodeAnalyzerEx.processHighlights(
            document,
            myProject,
            HighlightSeverity.WEAK_WARNING,
            0,
            document.textLength,
        ) { info ->
            val severity = DiagnosticSeverity.fromPlatformSeverity(info.severity)
            val message = info.description
            if (severity != null && !message.isNullOrBlank()) {
                val lineNumber = document.getLineNumber(info.startOffset)
                collected += DiagnosticInfo(
                    severity = severity,
                    message = message,
                    lineNumber = lineNumber,
                    lineEndOffset = document.getLineEndOffset(lineNumber),
                )
            }
            true
        }

        lineDiagnostics = LineDiagnosticSelector.selectOnePerLine(collected)
    }

    override fun doApplyInformationToEditor() {
        val entries = lineDiagnostics.map { it.lineEndOffset to InlineTextFormatter.format(it) }
        ErrorLensInlayManager.replaceInlays(editor, entries)
    }
}
