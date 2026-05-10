package com.kayzwilson.retrace.ui.components

import com.google.firebase.firestore.FirebaseFirestore
import com.kayzwilson.retrace.model.LostItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import android.util.Log

object ItemMatchingService {

    private val TAG = "ItemMatching"

    suspend fun findMatchingLostItems(
        foundItemCategory: String,
        foundItemLocation: String,
        foundItemDescription: String
    ): List<LostItemData> = withContext(Dispatchers.IO) {
        Log.d(TAG, "=== findMatchingLostItems STARTED ===")
        Log.d(TAG, "Category: $foundItemCategory")
        Log.d(TAG, "Location: $foundItemLocation")
        Log.d(TAG, "Description: $foundItemDescription")

        return@withContext try {
            val db = FirebaseFirestore.getInstance()

            // Query lost items with matching category and status
            Log.d(TAG, "Querying Firestore for lost items with category: $foundItemCategory")
            val snapshot = db.collection("lost_items")
                .whereEqualTo("status", "lost")
                .whereEqualTo("category", foundItemCategory)
                .get()
                .await()

            Log.d(TAG, "Query returned ${snapshot.documents.size} documents")

            val lostItems = snapshot.documents.mapNotNull { doc ->
                val item = doc.toObject(LostItemData::class.java)?.copy(id = doc.id)
                Log.d(TAG, "Processing doc ${doc.id}: ${item?.itemName}")
                item
            }

            Log.d(TAG, "Total lost items after mapping: ${lostItems.size}")

            // Filter by location proximity and description similarity
            val matches = lostItems.filter { lostItem ->
                val locationMatch = isLocationClose(lostItem.location, foundItemLocation)
                val descriptionMatch = isDescriptionSimilar(lostItem.description, foundItemDescription)
                Log.d(TAG, "Item ${lostItem.itemName}: location=$locationMatch, description=$descriptionMatch")

                locationMatch && descriptionMatch
            }

            Log.d(TAG, "Final matching items: ${matches.size}")
            matches
        } catch (e: Exception) {
            Log.e(TAG, "Exception in findMatchingLostItems: ${e.message}", e)
            e.printStackTrace()
            emptyList()
        }
    }

    private fun isLocationClose(lostLocation: String, foundLocation: String): Boolean {
        val lostWords = lostLocation.lowercase().split(" ", ",", "-")
        val foundWords = foundLocation.lowercase().split(" ", ",", "-")

        val result = lostWords.any { word ->
            word.isNotEmpty() && foundWords.any { it.contains(word) || word.contains(it) }
        }

        Log.d(TAG, "isLocationClose: '$lostLocation' vs '$foundLocation' = $result")
        return result
    }

    private fun isDescriptionSimilar(lostDescription: String, foundDescription: String): Boolean {
        if (lostDescription.isBlank() || foundDescription.isBlank()) return true

        val lostWords = lostDescription.lowercase().split(Regex("[\\s,.-]+"))
        val foundWords = foundDescription.lowercase().split(Regex("[\\s,.-]+"))

        val matchingWords = lostWords.count { lostWord ->
            lostWord.length > 1 && foundWords.any { foundWord ->
                foundWord.contains(lostWord, ignoreCase = true) || lostWord.contains(foundWord, ignoreCase = true)
            }
        }

        // Need at least 1 word match, OR if descriptions are very short, be more lenient
        val result = if (lostDescription.length < 15 || foundDescription.length < 15) {
            matchingWords > 0 || lostDescription.length + foundDescription.length < 30
        } else {
            matchingWords > 0
        }

        Log.d(TAG, "isDescriptionSimilar: '$lostDescription' vs '$foundDescription' = $result (matches: $matchingWords)")
        return result
    }
}