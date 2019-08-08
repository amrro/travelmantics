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
package dev.amr.travelmantics.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TravelsRepository : DataSource {
    private val db: FirebaseFirestore = Firebase.firestore
    private val storage = FirebaseStorage.getInstance().reference

    override suspend fun getDeals(): Result<List<Deal>> =
        suspendCoroutine { cont ->
            db.collection("deals")
                .get()
                .addOnSuccessListener {
                    try {
                        cont.resume(Result.Success(it.toObjects()))
                    } catch (e: Exception) {
                        cont.resume(Result.Error(e))
                    }
                }.addOnFailureListener {
                    cont.resume(Result.Error(it))
                }
        }

    override suspend fun newDeal(deal: Deal): Result<Boolean> =
        suspendCoroutine { cont ->
            db.collection("deals")
                .add(deal)
                .addOnSuccessListener {
                    cont.resume(Result.Success(true))
                }.addOnFailureListener {
                    cont.resume(Result.Error(it))
                }
        }

    override fun uploadImageWithUri(
        uri: Uri,
        block: ((Result<Uri>, Int) -> Unit)?
    ) {
        // Get a reference to store file at photos/<FILENAME>.jpg
        val photoRef = storage.child("deals").child(uri.lastPathSegment!!)
        photoRef.putFile(uri)
            .addOnProgressListener { taskSnapshot ->
                val percentComplete = if (taskSnapshot.totalByteCount > 0) {
                    (100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                } else 0

                block?.invoke(Result.Loading, percentComplete)
            }.continueWithTask { task ->
                // Forward any exceptions
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                // Request the public download URL
                photoRef.downloadUrl
            }
            .addOnSuccessListener { block?.invoke(Result.Success(it), 100) }
            .addOnFailureListener { block?.invoke(Result.Error(it), 0) }
    }
}

data class UploadingProgress(
    val progress: Int = 0,
    val result: Result<Boolean>
)