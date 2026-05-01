package com.kayzwilson.retrace.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kayzwilson.retrace.model.ReportUiState
import com.kayzwilson.retrace.ui.components.saveLostItem
import com.kayzwilson.retrace.ui.components.uploadImageToCloudinary

class ReportViewModel : ViewModel() {

    var uiState by mutableStateOf(ReportUiState())
        private set

    fun updateItemName(value: String) {
        uiState = uiState.copy(itemName = value)
    }

    fun updateCategory(value: String) {
        uiState = uiState.copy(category = value)
    }

    fun updateCollege(value: String) {
        uiState = uiState.copy(college = value)
    }

    fun updateLocation(value: String) {
        uiState = uiState.copy(location = value)
    }

    fun updateTime(value: String) {
        uiState = uiState.copy(time = value)
    }

    fun updateDescription(value: String) {
        uiState = uiState.copy(description = value)
    }

    fun submitLostItem(
        context: Context,
        imageUri: Uri?,
        onSuccess: () -> Unit
    ) {
        uiState = uiState.copy(isSubmitting = true, submitError = "")

        val db  = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (imageUri != null) {
            uploadImageToCloudinary(
                context  = context,
                imageUri = imageUri,
                folder   = "lost_items",
                onSuccess = { imageUrl ->
                    saveLostItem(
                        db          = db,
                        uid         = uid,
                        itemName    = uiState.itemName,
                        category    = uiState.category,
                        college     = uiState.college,
                        location    = uiState.location,
                        time        = uiState.time,
                        description = uiState.description,
                        imageUrl    = imageUrl
                    ) {
                        uiState = uiState.copy(isSubmitting = false)
                        onSuccess()
                    }
                },
                onFailure = { error ->
                    uiState = uiState.copy(
                        isSubmitting = false,
                        submitError  = "❌ $error"
                    )
                }
            )
        } else {
            saveLostItem(
                db          = db,
                uid         = uid,
                itemName    = uiState.itemName,
                category    = uiState.category,
                college     = uiState.college,
                location    = uiState.location,
                time        = uiState.time,
                description = uiState.description,
                imageUrl    = ""
            ) {
                uiState = uiState.copy(isSubmitting = false)
                onSuccess()
            }
        }
    }
}