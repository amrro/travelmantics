package dev.amr.travelmantics.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.amr.travelmantics.MainActivity
import dev.amr.travelmantics.R
import dev.amr.travelmantics.data.Deal
import dev.amr.travelmantics.data.TravelsRepository

class NewDealWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val repository = TravelsRepository()
    private val notificationManager by lazy {
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE)
        val price = inputData.getInt(KEY_PRICE, 0)
        val description = inputData.getString(KEY_DESC)
        val imageUri = inputData.getString(UploaderWorker.KEY_UPLOADED_URI)

        require(!(title.isNullOrEmpty() || price <= 0 || description.isNullOrEmpty() || imageUri.isNullOrEmpty()))

        val newDeal = Deal("", title, price, description, imageUri)

        // TODO: add deep link, deal detail fragment, then take user to there from notification.
        // Make Intent to MainActivity
        val intent = Intent(applicationContext, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        when (repository.newDeal(newDeal)) {
            is dev.amr.travelmantics.data.Result.Success -> {
                showFinishedNotification(
                    applicationContext.getString(R.string.new_deal_success, title),
                    intent
                )
                return Result.success()
            }
            is dev.amr.travelmantics.data.Result.Error -> {
                showFinishedNotification(
                    applicationContext.getString(R.string.new_deal_failure, title),
                    intent
                )
                return Result.failure()
            }
            dev.amr.travelmantics.data.Result.Loading -> { /* do nothing club. */
            }
        }

        return Result.failure()
    }

    /**
     * Show notification that the activity finished.
     */
    private fun showFinishedNotification(caption: String, intent: Intent/*, success: Boolean*/) {
        // Make PendingIntent for notification
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0 /* requestCode */, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val icon = R.drawable.ic_location_city_black_24dp

        createDefaultChannel()
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID_DEFAULT)
            .setSmallIcon(icon)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(caption)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(UploaderWorker.FINISHED_NOTIFICATION_ID, builder.build())
    }

    private fun createDefaultChannel() {
        // Notification channel is added since android Oreo. TODO: Go buy some.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_DEFAULT,
                "Default",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
    // TODO: add/ send notification in case use leaved the fragment.

    companion object {
        const val KEY_TITLE = "key-title-new-deal"
        const val KEY_PRICE = "key-price-new-deal"
        const val KEY_DESC = "key-desc-new-deal"

        private const val CHANNEL_ID_DEFAULT = "default"
    }
}