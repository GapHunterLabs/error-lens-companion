package dev.gaphunter.errorlenscompanion.highlight

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project

/**
 * Refreshes the inline hints once the IDE has finished highlighting a
 * file -- after every pass AND after the platform has dropped the
 * highlights that no longer apply.
 *
 * Fixes a real bug found while recording the product demo (2026-10-01):
 * [ErrorLensHighlightingPass] runs as soon as the general highlighting
 * pass completes, but the platform removes obsolete highlights only when
 * the whole highlighting session ends. Typing the missing ";" removed
 * the "';' expected" error from the editor while its inline hint stayed
 * on the line, with nothing left to repaint it.
 */
class ErrorLensDaemonListener(private val project: Project) : DaemonCodeAnalyzer.DaemonListener {

    override fun daemonFinished(fileEditors: Collection<FileEditor>) {
        if (project.isDisposed) return
        for (fileEditor in fileEditors) {
            val editor = (fileEditor as? TextEditor)?.editor ?: continue
            if (editor.isDisposed || editor.isOneLineMode) continue
            ErrorLensDiagnostics.apply(editor, ErrorLensDiagnostics.collect(editor, project))
        }
    }
}
