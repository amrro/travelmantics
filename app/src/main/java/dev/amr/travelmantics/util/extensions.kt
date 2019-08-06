@file:Suppress("NOTHING_TO_INLINE")

package dev.amr.travelmantics.util

import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

inline fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(
        this.requireContext(),
        message,
        duration
    ).show()
}

/**
 * Display the Snackbar with the [Snackbar.LENGTH_SHORT] duration.
 *
 * @param message the message text.
 */
@JvmName("snackbar")
inline fun View.snackbar(message: CharSequence, duration: Int = Snackbar.LENGTH_LONG) =
    Snackbar
        .make(this, message, duration)
        .apply { show() }

/**
 * Display the Snackbar with the [Snackbar.LENGTH_SHORT] duration.
 *
 * @param message the message resource.
 */
@JvmName("snackbar2")
inline fun View.snackbar(@StringRes message: Int, duration: Int = Snackbar.LENGTH_LONG) =
    Snackbar
        .make(this, message, duration)
        .apply { show() }

/**
 * Display Snackbar with the [Snackbar.LENGTH_LONG] duration.
 *
 * @param message the message text.
 */
@JvmName("snackbar3")
inline fun View.snackbar(message: CharSequence, @StringRes actionText: Int, noinline action: (View) -> Unit) = Snackbar
    .make(this, message, Snackbar.LENGTH_LONG)
    .setAction(actionText, action)
    .setActionTextColor(Color.RED)
    .apply { show() }
