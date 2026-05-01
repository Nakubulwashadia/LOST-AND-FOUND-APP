package com.kayzwilson.retrace.ui.components

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore

fun uploadImageToCloudinary(
    context: Context,
    imageUri: Uri,
    folder: String,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    val cloudName    = "dchc83kzl"
    val uploadPreset = "retrace_uploads"

    val inputStream = context.contentResolver.openInputStream(imageUri)
    val bytes = inputStream?.readBytes()
    inputStream?.close()

    if (bytes == null) {
        onFailure("Could not read image file")
        return
    }

    Thread {
        try {
            val boundary = "Boundary-${System.currentTimeMillis()}"
            val url = java.net.URL("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty(
                "Content-Type", "multipart/form-data; boundary=$boundary"
            )

            val outputStream = connection.outputStream
            val writer = java.io.PrintStream(outputStream)

            writer.print("--$boundary\r\n")
            writer.print("Content-Disposition: form-data; name=\"upload_preset\"\r\n\r\n")
            writer.print("$uploadPreset\r\n")

            writer.print("--$boundary\r\n")
            writer.print("Content-Disposition: form-data; name=\"folder\"\r\n\r\n")
            writer.print("$folder\r\n")

            writer.print("--$boundary\r\n")
            writer.print("Content-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"\r\n")
            writer.print("Content-Type: image/jpeg\r\n\r\n")
            writer.flush()
            outputStream.write(bytes)
            outputStream.flush()
            writer.print("\r\n--$boundary--\r\n")
            writer.flush()

            val responseCode = connection.responseCode
            val response = if (responseCode == 200) {
                connection.inputStream.bufferedReader().readText()
            } else {
                connection.errorStream?.bufferedReader()?.readText() ?: "Unknown error"
            }
            connection.disconnect()

            if (responseCode == 200) {
                val secureUrl = response
                    .substringAfter("\"secure_url\":\"")
                    .substringBefore("\"")
                onSuccess(secureUrl)
            } else {
                onFailure("Upload failed: $response")
            }
        } catch (e: Exception) {
            onFailure("Upload error: ${e.message}")
        }
    }.start()
}

fun saveLostItem(
    db: FirebaseFirestore,
    uid: String?,
    itemName: String,
    category: String,
    college: String,
    location: String,
    time: String,
    description: String,
    imageUrl: String,
    onDone: () -> Unit
) {
    val lostItem = hashMapOf(
        "itemName"    to itemName,
        "category"   to category,
        "college"    to college,
        "location"   to location,
        "timeLost"   to time,
        "description" to description,
        "imageUrl"   to imageUrl,
        "reportedBy" to uid,
        "timestamp"  to System.currentTimeMillis(),
        "status"     to "lost"
    )
    db.collection("lost_items").add(lostItem)
        .addOnSuccessListener { onDone() }
        .addOnFailureListener { onDone() }
}

fun saveFoundItem(
    db: FirebaseFirestore,
    uid: String?,
    itemName: String,
    category: String,
    college: String,
    location: String,
    time: String,
    description: String,
    imageUrl: String,
    onDone: () -> Unit
) {
    val foundItem = hashMapOf(
        "itemName"    to itemName,
        "category"   to category,
        "college"    to college,
        "location"   to location,
        "timeFound"  to time,
        "description" to description,
        "imageUrl"   to imageUrl,
        "reportedBy" to uid,
        "timestamp"  to System.currentTimeMillis(),
        "status"     to "found"
    )
    db.collection("found_items").add(foundItem)
        .addOnSuccessListener { onDone() }
        .addOnFailureListener { onDone() }
}