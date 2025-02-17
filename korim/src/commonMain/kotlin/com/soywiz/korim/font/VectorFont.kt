package com.soywiz.korim.font

import com.soywiz.kds.iterators.*
import com.soywiz.korim.vector.*

interface VectorFont : Font {
    fun getGlyphPath(size: Double, codePoint: Int, path: GlyphPath = GlyphPath()): GlyphPath?

    override fun renderGlyph(
        ctx: Context2d,
        size: Double,
        codePoint: Int,
        x: Double,
        y: Double,
        fill: Boolean,
        metrics: GlyphMetrics
    ) {
        getGlyphMetrics(size, codePoint, metrics)
        val g = getGlyphPath(size, codePoint)
        if (g != null) {
            ctx.keepTransform {
                ctx.translate(x, y)
                g.draw(ctx)
            }
            if (fill) ctx.fill() else ctx.stroke()
        }
    }
}

fun VectorFont.withFallback(vararg other: VectorFont?): VectorFontList = when (this) {
    is VectorFontList -> VectorFontList((list + other).filterNotNull())
    else -> VectorFontList(listOfNotNull(this, *other))
}

class VectorFontList(val list: List<VectorFont>) : VectorFont {
    constructor(vararg fonts: VectorFont?) : this(fonts.filterNotNull())

    override val name: String = list.joinToString(", ") { it.name }

    private val temp = GlyphPath()

    override fun getGlyphPath(size: Double, codePoint: Int, path: GlyphPath): GlyphPath? =
        list.firstNotNullOfOrNull { it.getGlyphPath(size, codePoint, path) }

    override fun getFontMetrics(size: Double, metrics: FontMetrics): FontMetrics =
        list.first().getFontMetrics(size, metrics)

    override fun getGlyphMetrics(size: Double, codePoint: Int, metrics: GlyphMetrics): GlyphMetrics {
        list.fastForEach { font ->
            if (font.getGlyphPath(size, codePoint, temp) != null) {
                return font.getGlyphMetrics(size, codePoint, metrics)
            }
        }
        return list.first().getGlyphMetrics(size, codePoint, metrics)
    }

    override fun getKerning(size: Double, leftCodePoint: Int, rightCodePoint: Int): Double =
        list.first().getKerning(size, leftCodePoint, rightCodePoint)

}
