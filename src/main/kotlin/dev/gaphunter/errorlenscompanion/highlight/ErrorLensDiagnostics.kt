package dev.gaphunter.errorlenscompanion.highlight

import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerEx
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.project.Project
import dev.gaphunter.errorlenscompanion.format.InlineTextFormatter
import dev.gaphunter.errorlenscompanion.model.DiagnosticInfo
import dev.gaphunter.errorlenscompanion.model.DiagnosticSeverity
import dev.gaphunter.errorlenscompanion.render.ErrorLensInlayManager
import dev.gaphunter.errorlenscompanion.select.LineDiagnosticSelector

/**
 * Reading the IDE's finished highlights and turning them into inlays,
 * shared by [ErrorLensHighlightingPass] (first paint) and
 * [ErrorLensDaemonListener] (the authoritative refresh once the whole
 * highlighting session has finished).
 */
object ErrorLensDiagnostics {

    /** One diagnostic per line (the most severe), read from highlights the IDE already computed. Needs read access. */
    fun collect(editor: Editor, project: Project): List<DiagnosticInfo> {
        val document = editor.document
        val collected = mutableListOf<DiagnosticInfo>()
        DaemonCodeAnalyzerEx.processHighlights(document, project, HighlightSeverity.WEAK_WARNING, 0, document.textLength) { info ->
            val severity = DiagnosticSeverity.fromPlatformSeverity(info.severity)
            val message = info.description
            if (severity != null && !message.isNullOrBlank() && info.startOffset <= document.textLength) {
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
        return LineDiagnosticSelector.selectOnePerLine(collected)
    }

    /** Replaces the editor's inlays with [diagnostics]. EDT only. */
    fun apply(editor: Editor, diagnostics: List<DiagnosticInfo>) {
        // same font the renderer draws with: an icon it has no glyph for would show as a box
        val font = editor.colorsScheme.getFont(EditorFontType.ITALIC)
        val entries = diagnostics.map { diagnostic ->
            diagnostic.lineEndOffset to InlineTextFormatter.format(diagnostic) { icon -> font.canDisplayUpTo(icon) == -1 }
        }
        ErrorLensInlayManager.replaceInlays(editor, entries)
    }
}
