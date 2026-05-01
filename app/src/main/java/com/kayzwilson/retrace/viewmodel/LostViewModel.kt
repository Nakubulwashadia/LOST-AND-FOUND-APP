package com.kayzwilson.retrace.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.kayzwilson.retrace.model.LostItemData

data class LostUiState(
    val items: List<LostItemData> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String = ""
)

class LostViewModel : ViewModel() {

    var uiState by mutableStateOf(LostUiState())
        private set

    private var listener: ListenerRegistration? = null

    init {
        startListening()
    }

    private fun startListening() {
        val db = FirebaseFirestore.getInstance()
        listener = db.collection("lost_items")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    uiState = uiState.copy(
                        isLoading    = false,
                        errorMessage = "Failed to load items: ${error.message}"
                    )
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(LostItemData::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                uiState = uiState.copy(
                    items        = items,
                    isLoading    = false,
                    errorMessage = ""
                )
            }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}