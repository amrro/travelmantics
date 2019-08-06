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
