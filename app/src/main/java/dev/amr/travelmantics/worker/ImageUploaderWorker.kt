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
import android.net.Uri
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dev.amr.travelmantics.MainActivity
import dev.amr.travelmantics.R
import dev.amr.travelmantics.data.TravelsRepository
import dev.amr.travelmantics.util.Notifier
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ImageUploaderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val context = applicationContext
    private val repository = TravelsRepository()

    override suspend fun doWork(): Result {
        val fileUri = inputData.getString(KEY_IMAGE_URI)?.toUri()
        fileUri?.let { return uploadImageFromURI(it) }
        throw IllegalStateException("Image URI doesn't exist")
    }

    private suspend fun uploadImageFromURI(fileUri: Uri): Result = suspendCoroutine { cont ->
        repository.uploadImageWithUri(fileUri) { result, percentage ->
            when (result) {
                is dev.amr.travelmantics.data.Result.Success -> {
                    showUploadFinishedNotification(result.data)
                    cont.resume(Result.success(workDataOf(KEY_UPLOADED_URI to result.data.toString())))
                }
                is dev.amr.travelmantics.data.Result.Loading -> {
                    showProgressNotification(context.getString(R.string.progress_uploading), percentage)
                }
                is dev.amr.travelmantics.data.Result.Error -> {
                    showUploadFinishedNotification(null)
                    cont.resume(Result.failure())
                }
            }
        }
    }

    /**
     * Show notification with a progress bar.
     */
    private fun showProgressNotification(caption: String, percent: Int) {
        Notifier
            .progressable(
                context,
                100, percent
            ) {
                notificationId = PROGRESS_NOTIFICATION_ID
                contentTitle = context.getString(R.string.app_name)
                contentText = caption
                smallIcon = R.drawable.notif_add_a_photo_blue_24dp
            }
    }

    /**
     * This dismisses any shown progress notification, then
     */
    private fun showUploadFinishedNotification(downloadUrl: Uri?) {
        // Hide the progress notification
        Notifier
            .dismissNotification(context, PROGRESS_NOTIFICATION_ID)

        val caption =
            if (downloadUrl != null) context.getString(R.string.upload_success)
            else context.getString(
                R.string.upload_failure
            )

        // Make Intent to MainActivity
        val intent = Intent(applicationContext, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0 /* requestCode */, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        Notifier.show(context) {
            contentTitle = applicationContext.getString(R.string.app_name)
            contentText = caption
            this.pendingIntent = pendingIntent
        } }

    companion object {

        const val KEY_IMAGE_URI: String = "key-image-uri"
        const val KEY_UPLOADED_URI: String = "key-uploaded-uri"

        internal const val PROGRESS_NOTIFICATION_ID = 0
    }
}