package com.soywiz.korge.ui

import com.soywiz.kds.*
import com.soywiz.korge.debug.*
import com.soywiz.korge.input.*
import com.soywiz.korge.render.*
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.font.*
import com.soywiz.korio.async.*
import com.soywiz.korma.geom.*
import com.soywiz.korui.*

inline fun Container.uiButton(
    width: Double = UI_DEFAULT_WIDTH,
    height: Double = UI_DEFAULT_HEIGHT,
    text: String = "",
    icon: BmpSlice? = null,
    block: @ViewDslMarker UIButton.() -> Unit = {}
): UIButton = UIButton(width, height, text, icon).addTo(this).apply(block)

@Deprecated("Use uiButton instead")
inline fun Container.iconButton(
    width: Double = UI_DEFAULT_WIDTH,
    height: Double = UI_DEFAULT_HEIGHT,
    icon: BmpSlice? = null,
    block: @ViewDslMarker UIButton.() -> Unit = {}
): UIButton = UIButton(width, height, icon = icon).addTo(this).apply(block)

@Deprecated("Use uiButton instead")
inline fun Container.uiTextButton(
    width: Double = UI_DEFAULT_WIDTH,
    height: Double = UI_DEFAULT_HEIGHT,
    text: String = "Button",
    textFont: Font? = null,
    textSize: Double? = null,
    block: @ViewDslMarker UIButton.() -> Unit = {}
): UIButton = UIButton(width, height, text).apply {
    if (textFont != null) this.textFont = textFont
    if (textSize != null) this.textSize = textSize
}.addTo(this).apply(block)

typealias UITextButton = UIButton
typealias IconButton = UIButton

open class UIButton(
	width: Double = 128.0,
	height: Double = 32.0,
    var text: String = "",
    var icon: BmpSlice? = null,
) : UIView(width, height) {
    @Deprecated("Use uiSkin instead")
    var skin: UISkin? get() = uiSkin ; set(value) { uiSkin = value }

	var forcePressed = false
    protected val rect: NinePatchEx = ninePatch(null, width, height)
    protected val textShadowView = text("", 16.0)
    protected val textView = text("", 16.0)
    protected val iconView = image(Bitmaps.transparent)
    private val textAndShadow = fastArrayListOf(textView, textShadowView)
	protected var bover = false
	protected var bpressing = false

    override fun renderInternal(ctx: RenderContext) {
        val skin = realUiSkin
        //println("UIButton: skin=$skin")
        rect.ninePatch = when {
            !enabled -> skin.buttonDisabled
            bpressing || forcePressed -> skin.buttonDown
            bover -> skin.buttonOver
            else -> skin.buttonNormal
        }
        rect.width = width
        rect.height = height

        fitIconInRect(iconView, icon ?: Bitmaps.transparent, rect.width, rect.height, Anchor.MIDDLE_CENTER)
        iconView.alpha = when {
            !enabled -> 0.5
            bover -> 1.0
            else -> 1.0
        }

        if (text.isNotEmpty()) {
            val alignment = skin.buttonTextAlignment
            val font = skin.textFont
            val text = text
            val textSize = skin.textSize

            textAndShadow.fastForEach { textView ->
                textView.setTextBounds(Rectangle(0.0, 0.0, width, height))
                textView.visible = true
                textView.text = text
                textView.alignment = alignment
                textView.font = font
                textView.textSize = textSize
            }

            textView.color = skin.textColor
            textShadowView.color = skin.shadowColor
            textShadowView.position(skin.shadowPosition)
        } else {
            textView.visible = false
            textShadowView.visible = false
        }
        super.renderInternal(ctx)
    }

    fun simulateOver() {
		bover = true
	}

	fun simulateOut() {
		bover = false
	}

	fun simulatePressing(value: Boolean) {
		bpressing = value
	}

	fun simulateDown() {
		bpressing = true
	}

	fun simulateUp() {
		bpressing = false
	}

    val onPress = Signal<TouchEvents.Info>()

	init {
        singleTouch {
            start {
                simulateDown()
            }
            endAnywhere {
                simulateUp()
            }
            tap {
                onPress(it)
            }
        }
		mouse {
			onOver {
				simulateOver()
			}
			onOut {
				simulateOut()
			}
		}
	}

    override fun buildDebugComponent(views: Views, container: UiContainer) {
        container.uiCollapsibleSection(UIButton::class.simpleName!!) {
            uiEditableValue(::text)
            uiEditableValue(::textSize, min = 1.0, max = 300.0)
        }
        super.buildDebugComponent(views, container)
    }
}
