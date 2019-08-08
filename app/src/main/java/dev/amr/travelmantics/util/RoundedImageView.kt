/**
 *                           MIT License
 *
 *                 Copyright (c) 2019 Amr Elghobary
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
) : AppCompatImageView(context, attrs) {

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