package dev.amr.travelmantics.data

import com.google.firebase.firestore.DocumentId

data class Deal(
    @DocumentId
    val id: String,
    val title: String,
    val price: Int = 0,
    val description: String,
    val imageUrl: String
) {
    @Suppress("unused")
    constructor() : this("", "", 0, "", "")
}