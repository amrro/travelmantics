package dev.amr.travelmantics.util

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import dev.amr.travelmantics.R

class RoundedImageView(
    context: Context,
    attrs: AttributeSet
): AppCompatImageView(context, attrs) {

    private val radius: Float

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView)
        try {
            radius = a.getDimension(R.styleable.RoundedImageView_radius, 16f)

        } finally {
            a.recycle()
        }

        outlineProvider = outlineProvider(radius)
        clipToOutline = true
    }

    private fun outlineProvider(radius: Float): ViewOutlineProvider {
        return object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(
                    view.paddingLeft,
                    view.paddingTop,
                    view.width - view.paddingRight,
                    view.height - view.paddingBottom,
                    radius
                )
            }
        }
    }
}