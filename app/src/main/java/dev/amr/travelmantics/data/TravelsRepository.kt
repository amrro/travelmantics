package dev.amr.travelmantics.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TravelsRepository : DataSource {
    private val db: FirebaseFirestore = Firebase.firestore

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
}