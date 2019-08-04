package dev.amr.travelmantics.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.storage.FirebaseStorage
import dev.amr.travelmantics.MainActivity
import dev.amr.travelmantics.R
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UploaderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val storage = FirebaseStorage.getInstance().reference
    private val notificationManager by lazy {
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override suspend fun doWork(): Result {
        val fileUri = inputData.getString(KEY_IMAGE_URI)?.toUri()
        fileUri?.let { return uploadImageFromURI(it) }
        throw IllegalStateException("Image URI doesn't exist")
    }

    private suspend fun uploadImageFromURI(fileUri: Uri): Result = suspendCoroutine { cont ->
        Timber.d("uploadImageFromURI:src:$fileUri")

        showProgressNotification(applicationContext.getString(R.string.progress_uploading), 0, 0)

        // Get a reference to store file at photos/<FILENAME>.jpg
        val photoRef = storage.child("deals")
            .child(fileUri.lastPathSegment!!)

        // Upload file to Firebase Storage
        Timber.d("uploadImageFromURI:dst:%s", photoRef.path)
        photoRef.putFile(fileUri).addOnProgressListener { taskSnapshot ->
            showProgressNotification(
                applicationContext.getString(R.string.progress_uploading),
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

    /**
     * Show notification with a progress bar.
     *
     * TODO: add "Cancel" Option
     */
    private fun showProgressNotification(caption: String, completedUnits: Long, totalUnits: Long) {
        var percentComplete = 0
        if (totalUnits > 0) {
            percentComplete = (100 * completedUnits / totalUnits).toInt()
        }

        createDefaultChannel()
        val builder = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID_DEFAULT
        )
            .setSmallIcon(R.drawable.ic_send_black_24dp)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(caption)
            .setProgress(100, percentComplete, false)
            .setOngoing(true)
            .setAutoCancel(false)

        notificationManager.notify(PROGRESS_NOTIFICATION_ID, builder.build())
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

        // TODO: pick icons
        val icon = R.drawable.ic_location_city_black_24dp

        createDefaultChannel()
        val builder = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID_DEFAULT
        )
            .setSmallIcon(icon)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(caption)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(FINISHED_NOTIFICATION_ID, builder.build())
    }

    /**
     * Show a notification for a finished upload.
     */
    private fun showUploadFinishedNotification(downloadUrl: Uri?) {
        // Hide the progress notification
        dismissProgressNotification()

        // Make Intent to MainActivity
        val intent = Intent(applicationContext, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val success = downloadUrl != null
        val caption =
            if (success) applicationContext.getString(R.string.upload_success) else applicationContext.getString(
                R.string.upload_failure
            )
        showFinishedNotification(caption, intent/*, success*/)
    }

    /**
     * Dismiss the progress notification.
     */
    private fun dismissProgressNotification() {
        notificationManager.cancel(PROGRESS_NOTIFICATION_ID)
    }

    companion object {

        const val KEY_IMAGE_URI: String = "key-image-uri"
        const val KEY_UPLOADED_URI: String = "key-uploaded-uri"
        private const val CHANNEL_ID_DEFAULT = "default"

        internal const val PROGRESS_NOTIFICATION_ID = 0
        internal const val FINISHED_NOTIFICATION_ID = 1
    }
}