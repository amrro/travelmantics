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
package dev.amr.travelmantics.binding

import android.net.Uri
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import dev.amr.travelmantics.data.Result

/**
 * Data Binding adapters specific to the app.
 */
object BindingAdapters {

    /**
     * Makes the View [View.INVISIBLE] unless the condition is met.
     */
    @Suppress("unused")
    @BindingAdapter("invisibleUnless")
    @JvmStatic
    fun invisibleUnless(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    /**
     * Makes the View [View.GONE] unless the condition is met.
     */
    @Suppress("unused")
    @BindingAdapter("goneUnless")
    @JvmStatic
    fun goneUnless(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("showFABUnless")
    fun showFABUnless(fab: FloatingActionButton, show: Boolean) {
        if (show) fab.show()
        else fab.hide()
    }

    @BindingAdapter("loseFocusWhen")
    @JvmStatic
    fun loseFocusWhen(view: EditText, condition: Boolean) {
        if (condition) view.clearFocus()
    }

    @JvmStatic
    @BindingAdapter("error")
    fun errorResult(tv: TextView, result: Result<Any>?) {
        if (result != null && result is Result.Error) {
            tv.text = result.exception.localizedMessage
        }
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun imageUrl(imageView: ImageView, url: String) {
        if (url.isNotEmpty()) {
            Picasso.Builder(imageView.context).build()
                .load(url)
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("imageUri")
    fun imageUri(imageView: ImageView, uri: Uri?) {
        uri?.let {
            Picasso.Builder(imageView.context).build()
                .load(uri)
                .into(imageView)
        }
    }
}
