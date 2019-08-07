package dev.amr.travelmantics.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.amr.travelmantics.MainActivity
import dev.amr.travelmantics.R
import dev.amr.travelmantics.data.Deal
import dev.amr.travelmantics.data.TravelsRepository
import dev.amr.travelmantics.util.Notifier

class NewDealWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val repository = TravelsRepository()
    private val context = applicationContext

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE)
        val price = inputData.getInt(KEY_PRICE, 0)
        val description = inputData.getString(KEY_DESC)
        val imageUri = inputData.getString(ImageUploaderWorker.KEY_UPLOADED_URI)

        require(!(title.isNullOrEmpty() || price <= 0 || description.isNullOrEmpty() || imageUri.isNullOrEmpty()))

        val newDeal = Deal("", title, price, description, imageUri)

        // TODO: add deep link, deal detail fragment, then take user to there from notification.
        // Make Intent to MainActivity
        val intent = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        when (repository.newDeal(newDeal)) {
            is dev.amr.travelmantics.data.Result.Success -> {
                showFinishedNotification(
                    context.getString(R.string.new_deal_success, title),
                    intent
                )
                return Result.success()
            }
            is dev.amr.travelmantics.data.Result.Error -> {
                showFinishedNotification(
                    context.getString(R.string.new_deal_failure, title),
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
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        Notifier.show(context) {
            contentTitle = context.getString(R.string.app_name)
            contentText = caption
            this.pendingIntent = pendingIntent
        }
    }

    companion object {
        const val KEY_TITLE = "key-title-new-deal"
        const val KEY_PRICE = "key-price-new-deal"
        const val KEY_DESC = "key-desc-new-deal"
    }
}