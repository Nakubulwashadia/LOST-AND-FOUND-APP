package com.kayzwilson.retrace.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.kayzwilson.retrace.model.FoundItemData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundItemCard(item: FoundItemData) {
    // categoryEmoji comes from Utils.kt — same package, no import needed
    val emoji = categoryEmoji(item.category)
    val formattedTime = remember(item.timestamp) {
        if (item.timestamp > 0) {
            val sdf = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
            sdf.format(java.util.Date(item.timestamp))
        } else "Unknown date"
    }

    var showContactSheet by remember { mutableStateOf(false) }
    var contactName      by remember { mutableStateOf("") }
    var contactEmail     by remember { mutableStateOf("") }
    var contactPhone     by remember { mutableStateOf("") }
    var contactStudentId by remember { mutableStateOf("") }
    var contactRole      by remember { mutableStateOf("") }
    var isLoadingContact by remember { mutableStateOf(false) }
    var contactError     by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showContactSheet) {
        ModalBottomSheet(
            onDismissRequest = { showContactSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF0A3D2B), Color(0xFF1A6B47))),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = if (contactName.isNotBlank()) {
                        contactName.split(" ").filter { it.isNotEmpty() }.take(2)
                            .joinToString("") { it.first().uppercase() }
                    } else "?"
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    contactName.ifEmpty { "Unknown User" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0A3D2B)
                )
                if (contactRole.isNotEmpty()) {
                    Text(contactRole, fontSize = 13.sp, color = Color(0xFF5B6E8C))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFFECF8F2), RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Found: ${item.itemName}",
                        fontSize = 12.sp,
                        color = Color(0xFF1A6B47),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                when {
                    isLoadingContact -> {
                        CircularProgressIndicator(color = Color(0xFF1A6B47), strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Fetching contact details...",
                            fontSize = 13.sp, color = Color(0xFF5B6E8C)
                        )
                    }
                    contactError.isNotEmpty() -> {
                        Text("⚠️", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            contactError, fontSize = 13.sp,
                            color = Color(0xFFE53935), textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        if (contactEmail.isNotEmpty()) {
                            ContactDetailRow(
                                icon = "📧", label = "Email Address",
                                value = contactEmail, actionLabel = "Send Email",
                                actionColor = Color(0xFF2C7DA0)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        if (contactPhone.isNotEmpty()) {
                            ContactDetailRow(
                                icon = "📞", label = "Phone Number",
                                value = contactPhone, actionLabel = "Call",
                                actionColor = Color(0xFF2E7D32)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        if (contactStudentId.isNotEmpty()) {
                            ContactDetailRow(
                                icon = "🎓", label = "Student Email",
                                value = contactStudentId, actionLabel = null,
                                actionColor = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        if (contactEmail.isEmpty() && contactPhone.isEmpty()) {
                            Text(
                                "⚠️ This user hasn't added contact details yet.",
                                fontSize = 13.sp, color = Color(0xFF5B6E8C),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                OutlinedButton(
                    onClick = { showContactSheet = false },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.5.dp, Color(0xFFDDE3F0)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF5B6E8C)
                    )
                ) { Text("Close", fontWeight = FontWeight.SemiBold) }
            }
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                if (item.imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(item.imageUrl),
                        contentDescription = item.itemName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFD4EDE1), Color(0xFFECF8F2))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(emoji, fontSize = 72.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                item.category.substringAfter(" ").ifEmpty { item.category },
                                fontSize = 12.sp,
                                color = Color(0xFF1A6B47),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(Color(0xFF1A6B47), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        "FOUND", color = Color.White, fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    item.itemName.ifEmpty { "Unnamed Item" },
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color(0xFF0A3D2B)
                )
                if (item.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        item.description, fontSize = 13.sp, color = Color(0xFF5B6E8C),
                        maxLines = 2, overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (item.location.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📍", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                item.location, fontSize = 12.sp, color = Color(0xFF5B6E8C),
                                maxLines = 1, overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 110.dp)
                            )
                        }
                    }
                    if (item.timeFound.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⏰", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(item.timeFound, fontSize = 12.sp, color = Color(0xFF5B6E8C))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📅", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(formattedTime, fontSize = 12.sp, color = Color(0xFF5B6E8C))
                }
                if (item.college.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏛️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            item.college, fontSize = 12.sp, color = Color(0xFF5B6E8C),
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        showContactSheet = true
                        if (contactName.isEmpty() && contactError.isEmpty()) {
                            isLoadingContact = true
                            if (item.reportedBy.isEmpty()) {
                                isLoadingContact = false
                                contactError = "No finder information linked to this item."
                            } else {
                                db.collection("users").document(item.reportedBy).get()
                                    .addOnSuccessListener { doc ->
                                        isLoadingContact = false
                                        if (doc.exists()) {
                                            contactName      = doc.getString("name")      ?: ""
                                            contactEmail     = doc.getString("email")     ?: ""
                                            contactPhone     = doc.getString("phone")     ?: ""
                                            contactStudentId = doc.getString("studentId") ?: ""
                                            contactRole      = doc.getString("role")      ?: ""
                                        } else {
                                            contactError = "Finder profile not found."
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        isLoadingContact = false
                                        contactError = "Failed to load contact: ${e.message}"
                                    }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0A3D2B),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text("Contact Finder", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}