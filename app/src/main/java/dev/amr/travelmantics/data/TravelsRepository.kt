package dev.amr.travelmantics.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TravelsRepository() : DataSource {
    private val db: FirebaseFirestore = Firebase.firestore

    override suspend fun getTravels(): Result<List<Travel>>  =
        suspendCoroutine { cont ->
            db.collection("travels")
                .get()
                .addOnSuccessListener {
                    try {
                        cont.resume(Result.Sucess(it.toObjects()))
                    } catch (e: Exception) {
                        cont.resume(Result.Error(e))
                    }
                }.addOnFailureListener {
                    cont.resume(Result.Error(it))
                }
        }

    override suspend fun newTravel(travel: Travel): Result<Boolean> =
        suspendCoroutine { cont ->
            db.collection("travels")
                .add(travel)
                .addOnSuccessListener {
                    cont.resume(Result.Sucess(true))
                }.addOnFailureListener {
                    cont.resume(Result.Error(it))
                }
        }
}

//suspend fun <T> Task<T>.asResult(): Result<T> =
//    suspendCoroutine { cont ->
//    this.addOnSuccessListener {
//        cont.resume(Result.Sucess(it))
//    }
//}