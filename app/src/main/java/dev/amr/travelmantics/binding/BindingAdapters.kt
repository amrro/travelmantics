package dev.amr.travelmantics.binding

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import dev.amr.travelmantics.data.Result

/**
 * Data Binding adapters specific to the app.
 */
object BindingAdapters {

    @JvmStatic
    @BindingAdapter("showViewIf")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
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
}
