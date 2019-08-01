package dev.amr.travelmantics.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Travel(
    @DocumentId
    val id: String,
    val title: String,
    val points: Int = 0,
    val location: String,
    val imageURL: String
) {
    constructor() : this("", "", 0, "", "")
}