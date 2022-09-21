package app.nakaura.chloe.original

import com.google.firebase.firestore.DocumentId

data class ToDo(
    @DocumentId
    val title: String = "",
    val point: String = "",
    val note: String = ""
)

