package com.kayzwilson.retrace.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

import com.kayzwilson.retrace.ui.components.*


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFoundItemScreen(onSubmit: () -> Unit, onBack: () -> Unit) {
    var itemName     by remember { mutableStateOf("") }
    var category     by remember { mutableStateOf("") }
    var college      by remember { mutableStateOf("") }
    var location     by remember { mutableStateOf("") }
    var time         by remember { mutableStateOf("") }
    var description  by remember { mutableStateOf("") }
    var imageUri     by remember { mutableStateOf<android.net.Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var submitError  by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var showTimePicker   by remember { mutableStateOf(false) }
    val timePickerState  = rememberTimePickerState(is24Hour = false)

    val categories = listOf(
        "💻 Computer / Laptop", "📱 Phone", "🎧 Earphones / Headphones",
        "🪪 ID Card / Student Card", "🎒 Bag / Backpack", "📚 Books / Notes",
        "👓 Glasses / Spectacles", "🔑 Keys", "💳 Wallet / Cards",
        "🧥 Clothing", "⌚ Watch / Jewelry", "🖊️ Stationery", "Other"
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    // Image is mandatory here
    val canSubmit = itemName.isNotBlank() && category.isNotBlank() &&
            location.isNotBlank() && time.isNotBlank() && imageUri != null

    val context = androidx.compose.ui.platform.LocalContext.current

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute
                    val amPm = if (hour < 12) "AM" else "PM"
                    val h = if (hour % 12 == 0) 12 else hour % 12
                    time = "%d:%02d %s".format(h, minute, amPm)
                    showTimePicker = false
                }) { Text("OK", color = Color(0xFF1A6B47)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            title = { Text("Select Time Found", fontWeight = FontWeight.SemiBold) },
            text = { TimePicker(state = timePickerState) }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF0FAF4),
        topBar = {
            TopAppBar(
                title = { Text("Report Found Item", fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold, color = Color(0xFF0A3D2B)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back", tint = Color(0xFF1A6B47))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF0FAF4))
                .verticalScroll(rememberScrollState())
                .padding(paddingValues).padding(horizontal = 16.dp).padding(bottom = 32.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // ── Image Upload Card (Mandatory) ─────────────────────────────
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
                        Text("📷", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Item Photo", fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF0A3D2B))
                        Spacer(modifier = Modifier.width(6.dp))
                        // Mandatory badge
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFEBEE), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Required", fontSize = 11.sp,
                                color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (imageUri == null) Color(0xFFF0FAF4) else Color.Transparent)
                            .border(
                                2.dp,
                                if (imageUri == null) Color(0xFF1A6B47).copy(alpha = 0.4f) else Color(0xFF1A6B47),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            androidx.compose.foundation.Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Selected image",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp))
                            )
                            Box(
                                modifier = Modifier.fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Tap to change", color = Color.White,
                                    fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📸", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Tap to upload a photo", fontSize = 14.sp,
                                    color = Color(0xFF1A6B47), fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("A photo is required to help identify the item",
                                    fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Item Details Card ─────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)) {
                        Text("📋", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Item Details", fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF0A3D2B))
                    }
                    OutlinedTextField(
                        value = itemName, onValueChange = { itemName = it },
                        label = { Text("Item Name *") },
                        placeholder = { Text("e.g. Black Dell Laptop") },
                        leadingIcon = { Text("🏷️", modifier = Modifier.padding(start = 4.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A6B47), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = category, onValueChange = {}, readOnly = true,
                            label = { Text("Category *") },
                            placeholder = { Text("Select a category") },
                            leadingIcon = { Text("🗂️", modifier = Modifier.padding(start = 4.dp)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1A6B47), unfocusedBorderColor = Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }) {
                            categories.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = 14.sp) },
                                    onClick = { category = option; categoryExpanded = false }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = description, onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("Describe the item — color, brand, markings...") },
                        leadingIcon = { Text("📝", modifier = Modifier.padding(start = 4.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3, maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A6B47), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Location & Time Card ──────────────────────────────────────
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)) {
                        Text("📍", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Where & When", fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF0A3D2B))
                    }
                    OutlinedTextField(
                        value = college, onValueChange = { college = it },
                        label = { Text("College / Area") },
                        placeholder = { Text("e.g. College of Engineering") },
                        leadingIcon = { Text("🏛️", modifier = Modifier.padding(start = 4.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A6B47), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = location, onValueChange = { location = it },
                        label = { Text("Specific Location *") },
                        placeholder = { Text("e.g. Library, Room 204, Cafeteria") },
                        leadingIcon = { Text("📌", modifier = Modifier.padding(start = 4.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A6B47), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = time, onValueChange = {}, readOnly = true,
                        label = { Text("Time Found *") },
                        placeholder = { Text("Tap to select time") },
                        leadingIcon = { Text("⏰", modifier = Modifier.padding(start = 4.dp)) },
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Pick time",
                                    tint = Color(0xFF1A6B47), modifier = Modifier.size(18.dp))
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A6B47), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth().clickable { showTimePicker = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (submitError.isNotEmpty()) {
                Text(text = submitError, color = Color(0xFFE53935), fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp))
            }

            // ── Submit Button ─────────────────────────────────────────────
            Button(
                onClick = {
                    isSubmitting = true
                    submitError = ""
                    val db  = FirebaseFirestore.getInstance()
                    val uid = FirebaseAuth.getInstance().currentUser?.uid

                    // Image is mandatory on found form so imageUri is never null here
                    uploadImageToCloudinary(
                        context  = context,
                        imageUri = imageUri!!,
                        folder   = "found_items",
                        onSuccess = { imageUrl ->
                            saveFoundItem(db, uid, itemName, category, college,
                                location, time, description, imageUrl) {
                                isSubmitting = false
                                onSubmit()
                            }
                        },
                        onFailure = { error ->
                            isSubmitting = false
                            submitError = "❌ $error"
                        }
                    )
                },
                enabled = canSubmit && !isSubmitting,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A6B47),
                    contentColor   = Color.White,
                    disabledContainerColor = Color(0xFF1A6B47).copy(alpha = 0.4f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp),
                        color = Color.White, strokeWidth = 2.5.dp)
                } else {
                    Text("Submit Found Report", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("* Required fields", fontSize = 11.sp, color = Color.Gray,
                modifier = Modifier.align(Alignment.End))
        }
    }
}