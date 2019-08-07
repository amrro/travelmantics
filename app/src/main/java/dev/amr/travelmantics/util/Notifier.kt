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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import dev.amr.travelmantics.R

/**
 * Helper class for showing different types of notifications.
 *
 *
 * This class makes heavy use of the [NotificationCompat.Builder] helper
 * class to create notifications.
 */
object Notifier {

    /**
     * The unique identifier for this type of notification.
     * if no id passed for notification, this will be the default one.
     */
    private const val NOTIFICATION_TAG = "OneTime"

    /**
     * Currently, all constructed Notifications will be posted on this NotificationChannel.
     */
    private const val CHANNEL_ID_DEFAULT = "default"
    private var notificationManager: NotificationManager? = null

    /**
     * This data class reprensent the common data needed for any notification, e.g. title, text,
     * intent...etc.
     */
    data class NotificationData(
        var notificationId: Int? = null,
        var contentTitle: String = "",
        var contentText: String = "",
        var pendingIntent:
        PendingIntent? = null,
        var isAutoCancelable: Boolean = true,
        @DrawableRes var smallIcon: Int? = null
    )

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     */
    fun show(
        context: Context,
        init: NotificationData.() -> Unit
    ): NotificationCompat.Builder? {

        val data = NotificationData()
        data.init()

        val notificationId = data.notificationId ?: NOTIFICATION_TAG.hashCode()
        // Remove any notification with the same id.
        this.dismissNotification(context, notificationId)

        createDefaultChannel()
        val builder = notificationBuilder(context, CHANNEL_ID_DEFAULT, data)
            .setAutoCancel(data.isAutoCancelable)

        notify(notificationId, builder.build())

        return builder
    }

    fun progressable(
        context: Context,
        max: Int = 100,
        progress: Int,
        isIndeterminate: Boolean = false,
        init: NotificationData.() -> Unit
    ) {
        if (notificationManager == null) {
            notificationManager = notificationManager(context)
        }

        val data = NotificationData()
        data.init()

        val notificationId = data.notificationId ?: NOTIFICATION_TAG.hashCode()

        createDefaultChannel()
        val builder = notificationBuilder(context, CHANNEL_ID_DEFAULT, data)
            .setProgress(max, progress, isIndeterminate)
            .setOngoing(true)
            .setAutoCancel(false)

        notify(notificationId, builder.build())
    }

    /**
     * Cancels any notifications of this type previously shown.
     */
    fun dismissNotification(context: Context, id: Int) {
        if (notificationManager == null) {
        }
        notificationManager = notificationManager(context)

        notificationManager?.cancel(id)
    }

    private fun notificationManager(context: Context) = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun notify(id: Int, notification: Notification) {
        notificationManager?.notify(id, notification)
    }

    private fun notificationBuilder(
        context: Context,
        channelId: String? = null,
        data: NotificationData
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId ?: CHANNEL_ID_DEFAULT)

            // Set appropriate defaults for the notification light, sound,
            // and vibration.
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(data.smallIcon ?: R.drawable.notif_location_city_blue_24dp)
            .setContentTitle(data.contentTitle)
            .setContentText(data.contentText)
            // All fields below this line are optional.
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setTicker(data.contentTitle)
            .setContentIntent(data.pendingIntent)
    }

    private fun createDefaultChannel() {
        // Notification channel is added since android Oreo.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_DEFAULT,
                "Default",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
