package com.kayzwilson.retrace

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemReportingScreen() {
    val midnightBlue = Color(0xFF003366)
    val lightMidnightBlue = Color(0x1A003366)
    val db = Firebase.firestore
    val context = LocalContext.current // Grab context here for the Toast

    var showDetails by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var contactName by remember { mutableStateOf("") }
    var regNumber by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val categories = listOf("Electronics", "Clothing", "Accessories", "Documents", "Keys", "Bags", "Jewelry", "Other")

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    val scrollState = rememberScrollState()

    if (showDetails) {
        AlertDialog(
            onDismissRequest = { showDetails = false },
            title = { Text("Review Your Report", color = midnightBlue, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Item: $itemName")
                    Text("Category: $category")
                    Text("Description: $description")
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text("Name: $contactName")
                    Text("Reg No: $regNumber")
                    Text("Phone: $phone")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val itemData = hashMapOf(
                        "itemName" to itemName,
                        "category" to category,
                        "description" to description,
                        "contactName" to contactName,
                        "regNumber" to regNumber,
                        "phone" to phone,
                        "timestamp" to Timestamp.now()
                    )

                    // Sending to Firestore
                    db.collection("lost_items")
                        .add(itemData)
                        .addOnSuccessListener {
                            showDetails = false
                            android.widget.Toast.makeText(context, "Reported successfully!", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                        }
                }) {
                    Text("Confirm & Send")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDetails = false }) {
                    Text("Edit")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 48.dp)
    ) {
        Box(
            modifier = Modifier.size(56.dp).clip(CircleShape).background(lightMidnightBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = midnightBlue)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Report Lost Item", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = midnightBlue)
        Text("Provide details to help others return your item.", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        Text("What did you lose?", fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = itemName,
            onValueChange = { itemName = it },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = midnightBlue)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(8.dp)
            )
            ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                categories.forEach { cat ->
                    DropdownMenuItem(text = { Text(cat) }, onClick = { category = cat; categoryExpanded = false })
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text("Description", fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth().height(120.dp).padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp),
            placeholder = { Text("Color, brand, unique marks...") }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("Contact Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = midnightBlue)

        OutlinedTextField(
            value = contactName,
            onValueChange = { contactName = it },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = regNumber,
            onValueChange = { regNumber = it },
            label = { Text("Enter your registration number") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            Text(if (selectedImageUri == null) "+ Add Photo" else "Photo Attached", color = midnightBlue)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { showDetails = true },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = midnightBlue),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Submit Report", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}