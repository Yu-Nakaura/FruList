package app.nakaura.chloe.original

import com.google.firebase.firestore.DocumentId

data class ToDo(
    @DocumentId
    val userName: String = "",
    val group: String = "",
    val title: String = "",
    val point: Int = 0,
    val note: String = ""
)

