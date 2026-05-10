package com.kayzwilson.retrace.ui.components

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import android.util.Log

object SendGridEmailService {

    private const val SENDGRID_API_KEY = "YOUR_SENDGRID_API_KEY_HERE"
    private const val SENDGRID_API_URL = "https://api.sendgrid.com/v3/mail/send"
    private const val TAG = "SendGridEmail"

    suspend fun sendFoundItemNotification(
        userEmail: String,
        itemName: String,
        itemCategory: String,
        itemLocation: String,
        itemDescription: String,
        finderName: String,
        finderContact: String
    ): Boolean = withContext(Dispatchers.Default) {
        Log.d(TAG, "Starting email send to: $userEmail")
        Log.d(TAG, "API Key: ${SENDGRID_API_KEY.take(10)}...")

        return@withContext try {
            val htmlContent = buildEmailContent(
                itemName = itemName,
                itemCategory = itemCategory,
                itemLocation = itemLocation,
                itemDescription = itemDescription,
                finderName = finderName,
                finderContact = finderContact
            )

            val jsonPayload = buildJsonPayload(
                toEmail = userEmail,
                subject = "Great News! Your Lost Item \"$itemName\" Has Been Found!",
                htmlContent = htmlContent
            )

            Log.d(TAG, "JSON Payload prepared: ${jsonPayload.take(200)}...")

            val url = URL(SENDGRID_API_URL)
            Log.d(TAG, "Connecting to SendGrid API...")

            val connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Authorization", "Bearer $SENDGRID_API_KEY")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            Log.d(TAG, "Sending request...")
            val outputStream: OutputStream = connection.outputStream
            outputStream.write(jsonPayload.toByteArray())
            outputStream.close()

            val statusCode = connection.responseCode
            Log.d(TAG, "Response status code: $statusCode")

            val responseBody = if (statusCode >= 200 && statusCode < 300) {
                connection.inputStream?.bufferedReader()?.readText() ?: ""
            } else {
                connection.errorStream?.bufferedReader()?.readText() ?: ""
            }
            Log.d(TAG, "Response body: $responseBody")

            connection.disconnect()

            val success = statusCode in 200..299
            Log.d(TAG, "Email send result: $success")
            success
        } catch (e: Exception) {
            Log.e(TAG, "Error sending email: ${e.message}", e)
            e.printStackTrace()
            false
        }
    }

    private fun buildJsonPayload(toEmail: String, subject: String, htmlContent: String): String {
        val escapedSubject = subject
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")

        val escapedHtml = escapeJson(htmlContent)

        return """
            {
              "personalizations": [
                {
                  "to": [{"email": "$toEmail"}],
                  "subject": "$escapedSubject"
                }
              ],
              "from": {"email": "emmanuelbrady77@gmail.com", "name": "Retrace Team"},
              "content": [
                {
                  "type": "text/html",
                  "value": "$escapedHtml"
                }
              ]
            }
        """.trimIndent()
    }

    private fun escapeJson(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    private fun buildEmailContent(
        itemName: String,
        itemCategory: String,
        itemLocation: String,
        itemDescription: String,
        finderName: String,
        finderContact: String
    ): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; background: #f9f9f9; border-radius: 8px; }
                    .header { background: #1A6B47; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background: white; padding: 20px; }
                    .item-details { background: #f0faf4; padding: 15px; border-left: 4px solid #1A6B47; margin: 15px 0; border-radius: 4px; }
                    .finder-info { background: #e8f5e9; padding: 15px; border-radius: 4px; margin: 15px 0; }
                    .footer { text-align: center; font-size: 12px; color: #999; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Your Lost Item Found!</h1>
                    </div>
                    <div class="content">
                        <p>Hi there!</p>
                        <p>Great news! Someone found an item matching your lost report on Retrace.</p>
                        
                        <div class="item-details">
                            <h3>Item Details</h3>
                            <p><strong>Item Name:</strong> $itemName</p>
                            <p><strong>Category:</strong> $itemCategory</p>
                            <p><strong>Location Found:</strong> $itemLocation</p>
                            <p><strong>Description:</strong> $itemDescription</p>
                        </div>
                        
                        <div class="finder-info">
                            <h3>Finder Information</h3>
                            <p><strong>Name:</strong> $finderName</p>
                            <p><strong>Contact:</strong> $finderContact</p>
                            <p style="color: #666; font-size: 14px; margin-top: 10px;">Please reach out to the finder to arrange a meetup and verify the item is yours.</p>
                        </div>
                        
                        <p style="color: #666;">If this isn't your item or you no longer need it, you can ignore this email.</p>
                        
                        <p>Best regards,<br><strong>Retrace Team</strong></p>
                    </div>
                    <div class="footer">
                        <p>Copyright 2024 Retrace. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}