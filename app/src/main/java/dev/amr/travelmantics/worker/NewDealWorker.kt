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
        val title = checkNotNull(inputData.getString(KEY_TITLE))
        val price = checkNotNull(inputData.getInt(KEY_PRICE, 0))
        val description = checkNotNull(inputData.getString(KEY_DESC))
        val imageUri = checkNotNull(inputData.getString(ImageUploaderWorker.KEY_UPLOADED_URI))

        val newDeal = Deal("", title, price, description, imageUri)

        // TODO: add deep link, deal detail fragment, then take user to there from notification.
        val intent = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        when (repository.newDeal(newDeal)) {
            is dev.amr.travelmantics.data.Result.Success -> {
                showFinishedNotification(
                    title, context.getString(R.string.new_deal_success), intent
                )
                return Result.success()
            }
            is dev.amr.travelmantics.data.Result.Error -> {
                showFinishedNotification(
                    title, context.getString(R.string.new_deal_failure), intent
                )
                return Result.failure()
            }
            dev.amr.travelmantics.data.Result.Loading -> { /* do nothing club. */ }
        }

        return Result.failure()
    }

    /**
     * Show notification that the activity finished.
     */
    private fun showFinishedNotification(title: String, caption: String, intent: Intent) {

        Notifier.dismissNotification(context, title.hashCode())

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        Notifier.show(context) {
            contentTitle = title
            contentText = caption
            this.pendingIntent = pendingIntent

            // title is used to distinguish between and add as many notifications as new added deals.
            notificationId = title.hashCode()
        }
    }

    companion object {
        const val KEY_TITLE = "key-title-new-deal"
        const val KEY_PRICE = "key-price-new-deal"
        const val KEY_DESC = "key-desc-new-deal"
    }
}