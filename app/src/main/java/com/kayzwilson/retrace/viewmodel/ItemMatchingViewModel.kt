package com.kayzwilson.retrace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.kayzwilson.retrace.ui.components.ItemMatchingService
import com.kayzwilson.retrace.ui.components.SendGridEmailService
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.util.Log

class ItemMatchingViewModel : ViewModel() {

    private val TAG = "ItemMatching"

    fun checkAndNotifyMatches(
        foundItemCategory: String,
        foundItemLocation: String,
        foundItemDescription: String,
        finderName: String,
        finderContact: String,
        onComplete: (successCount: Int, failureCount: Int) -> Unit
    ) {
        Log.d(TAG, "=== checkAndNotifyMatches STARTED ===")
        Log.d(TAG, "Found Item Category: $foundItemCategory")
        Log.d(TAG, "Found Item Location: $foundItemLocation")
        Log.d(TAG, "Found Item Description: $foundItemDescription")
        Log.d(TAG, "Finder Name: $finderName")
        Log.d(TAG, "Finder Contact: $finderContact")

        viewModelScope.launch {
            try {
                // Find matching lost items
                Log.d(TAG, "Searching for matching lost items...")
                val matchingItems = ItemMatchingService.findMatchingLostItems(
                    foundItemCategory = foundItemCategory,
                    foundItemLocation = foundItemLocation,
                    foundItemDescription = foundItemDescription
                )

                Log.d(TAG, "Found ${matchingItems.size} matching lost items")
                matchingItems.forEachIndexed { index, item ->
                    Log.d(TAG, "Match $index: ${item.itemName} (reported by ${item.reportedBy})")
                }

                var successCount = 0
                var failureCount = 0

                // Send email for each matching item
                for ((index, lostItem) in matchingItems.withIndex()) {
                    Log.d(TAG, "Processing match $index: ${lostItem.itemName}")

                    // Get the email of the person who reported the lost item
                    val userEmail = getUserEmail(lostItem.reportedBy)
                    Log.d(TAG, "Got email for ${lostItem.reportedBy}: $userEmail")

                    if (userEmail.isNotEmpty()) {
                        Log.d(TAG, "Sending email to $userEmail...")
                        val emailSent = SendGridEmailService.sendFoundItemNotification(
                            userEmail = userEmail,
                            itemName = lostItem.itemName,
                            itemCategory = lostItem.category,
                            itemLocation = foundItemLocation,
                            itemDescription = foundItemDescription,
                            finderName = finderName,
                            finderContact = finderContact
                        )

                        Log.d(TAG, "Email send result: $emailSent")
                        if (emailSent) {
                            successCount++
                        } else {
                            failureCount++
                        }
                    } else {
                        Log.d(TAG, "User email was empty for ${lostItem.reportedBy}")
                        failureCount++
                    }
                }

                Log.d(TAG, "=== COMPLETED: $successCount sent, $failureCount failed ===")
                onComplete(successCount, failureCount)
            } catch (e: Exception) {
                Log.e(TAG, "Exception in checkAndNotifyMatches: ${e.message}", e)
                e.printStackTrace()
                onComplete(0, 0)
            }
        }
    }

    private suspend fun getUserEmail(uid: String): String {
        Log.d(TAG, "Fetching email for uid: $uid")
        return try {
            val doc = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .await()
            val email = doc.getString("email") ?: ""
            Log.d(TAG, "Email fetch successful: $email")
            email
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching email: ${e.message}", e)
            ""
        }
    }
}