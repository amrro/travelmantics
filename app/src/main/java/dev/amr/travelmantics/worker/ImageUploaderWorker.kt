package dev.amr.travelmantics.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.storage.FirebaseStorage
import dev.amr.travelmantics.MainActivity
import dev.amr.travelmantics.R
import dev.amr.travelmantics.util.Notifier
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ImageUploaderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val context = applicationContext
    private val storage = FirebaseStorage.getInstance().reference

    override suspend fun doWork(): Result {
        val fileUri = inputData.getString(KEY_IMAGE_URI)?.toUri()
        fileUri?.let { return uploadImageFromURI(it) }
        throw IllegalStateException("Image URI doesn't exist")
    }

    private suspend fun uploadImageFromURI(fileUri: Uri): Result = suspendCoroutine { cont ->
        Timber.d("uploadImageFromURI:src:$fileUri")

        showProgressNotification(context.getString(R.string.progress_uploading), 0, 0)

        // Get a reference to store file at photos/<FILENAME>.jpg
        val photoRef = storage.child("deals")
            .child(fileUri.lastPathSegment!!)

        // Upload file to Firebase Storage
        Timber.d("uploadImageFromURI:dst:%s", photoRef.path)
        photoRef.putFile(fileUri).addOnProgressListener { taskSnapshot ->
            showProgressNotification(
                context.getString(R.string.progress_uploading),
                taskSnapshot.bytesTransferred,
                taskSnapshot.totalByteCount
            )
        }.continueWithTask { task ->
            // Forward any exceptions
            if (!task.isSuccessful) {
                throw task.exception!!
            }

            Timber.d("uploadImageFromURI: upload success")

            // Request the public download URL
            photoRef.downloadUrl
        }.addOnSuccessListener { uploadedUri ->
            // Upload succeeded
            Timber.d("uploadImageFromURI: getDownloadUri success")

            showUploadFinishedNotification(uploadedUri)
            cont.resume(Result.success(workDataOf(KEY_UPLOADED_URI to uploadedUri.toString())))
        }.addOnFailureListener { exception ->
            // Upload failed
            Timber.e(exception, "uploadImageFromURI: upload failed")

            showUploadFinishedNotification(null)
        }
    }

    /**
     * Show notification with a progress bar.
     */
    private fun showProgressNotification(caption: String, completedUnits: Long, totalUnits: Long) {
        var percentComplete = 0
        if (totalUnits > 0) {
            percentComplete = (100 * completedUnits / totalUnits).toInt()
        }

        Notifier
            .progressable(
                context,
                100, percentComplete
            ) {
                notificationId = PROGRESS_NOTIFICATION_ID
                contentTitle = context.getString(R.string.app_name)
                contentText = caption
                smallIcon = R.drawable.notif_add_a_photo_blue_24dp
            }
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

        Notifier.show(context) {
            contentTitle = applicationContext.getString(R.string.app_name)
            contentText = caption
            this.pendingIntent = pendingIntent
        }
    }

    /**
     * Show a notification for a finished upload.
     */
    private fun showUploadFinishedNotification(downloadUrl: Uri?) {
        // Hide the progress notification
        Notifier
            .dismissNotification(context, PROGRESS_NOTIFICATION_ID)

        // Make Intent to MainActivity
        val intent = Intent(applicationContext, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val success = downloadUrl != null
        val caption =
            if (success) context.getString(R.string.upload_success) else context.getString(
                R.string.upload_failure
            )
        showFinishedNotification(caption, intent/*, success*/)
    }


    companion object {

        const val KEY_IMAGE_URI: String = "key-image-uri"
        const val KEY_UPLOADED_URI: String = "key-uploaded-uri"

        internal const val PROGRESS_NOTIFICATION_ID = 0
    }
}