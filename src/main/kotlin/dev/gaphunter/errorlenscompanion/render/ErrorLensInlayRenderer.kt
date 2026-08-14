package dev.gaphunter.errorlenscompanion.render

import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Color
import java.awt.Graphics
import java.awt.Rectangle

/**
 * Renders one Error Lens Companion inlay: an icon + short message pinned
 * to the end of a line. Deliberately minimal painting -- flat, semi
 * transparent foreground color, editor's own italic font, no custom
 * backgrounds or theming beyond that -- to keep the one part of this
 * plugin that cannot be unit-tested (real on-screen `paint()`) as
 * low-risk as possible.
 *
 * This is the least-verified corner of the whole overnight batch: it
 * compiles and its width/paint calls run without throwing in headless
 * tests (see ErrorLensInlayManagerTest), but nobody has looked at the
 * actual pixels yet. See README.md "Known limitations".
 */
class ErrorLensInlayRenderer(private val text: String) : EditorCustomElementRenderer {

    companion object {
        private const val LEFT_PADDING_PX = 12
        private const val ALPHA = 140
    }

    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        val editor = inlay.editor
        val font = editor.colorsScheme.getFont(EditorFontType.ITALIC)
        val fontMetrics = editor.contentComponent.getFontMetrics(font)
        return LEFT_PADDING_PX + fontMetrics.stringWidth(text)
    }

    override fun paint(inlay: Inlay<*>, g: Graphics, targetRegion: Rectangle, textAttributes: TextAttributes) {
        val editor = inlay.editor
        val font = editor.colorsScheme.getFont(EditorFontType.ITALIC)
        val foreground = editor.colorsScheme.defaultForeground
        g.font = font
        g.color = Color(foreground.red, foreground.green, foreground.blue, ALPHA)
        val fontMetrics = g.getFontMetrics(font)
        val baseline = targetRegion.y + fontMetrics.ascent
        g.drawString(text, targetRegion.x + LEFT_PADDING_PX, baseline)
    }
}
