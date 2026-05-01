package com.kayzwilson.retrace.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.kayzwilson.retrace.viewmodel.ReportViewModel
import com.kayzwilson.retrace.ui.components.*
import com.kayzwilson.retrace.ui.components.uploadImageToCloudinary
import com.kayzwilson.retrace.ui.components.saveLostItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportLostItemScreen(
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: ReportViewModel = viewModel()
    val state = viewModel.uiState
    val context = LocalContext.current

    var categoryExpanded by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val timePickerState = rememberTimePickerState(is24Hour = false)

    val categories = listOf(
        "💻 Computer / Laptop",
        "📱 Phone",
        "🎧 Earphones / Headphones",
        "🪪 ID Card / Student Card",
        "🎒 Bag / Backpack",
        "📚 Books / Notes",
        "👓 Glasses / Spectacles",
        "🔑 Keys",
        "💳 Wallet / Cards",
        "🧥 Clothing",
        "⌚ Watch / Jewelry",
        "🖊️ Stationery",
        "Other"
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    val canSubmit =
        state.itemName.isNotBlank() &&
                state.category.isNotBlank() &&
                state.location.isNotBlank() &&
                state.time.isNotBlank()

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val hour = timePickerState.hour
                        val minute = timePickerState.minute
                        val amPm = if (hour < 12) "AM" else "PM"
                        val h = if (hour % 12 == 0) 12 else hour % 12
                        val selectedTime = "%d:%02d %s".format(h, minute, amPm)

                        viewModel.updateTime(selectedTime)
                        showTimePicker = false
                    }
                ) {
                    Text("OK", color = Color(0xFF2C7DA0))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            title = {
                Text("Select Time Lost")
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Report Lost Item",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F3B5C)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2C7DA0)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {

            // IMAGE CARD
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "📷 Item Photo (Optional)",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F3B5C)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF0F4FF))
                            .border(
                                2.dp,
                                Color(0xFFDDE3F0),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                imagePickerLauncher.launch("image/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📁", fontSize = 34.sp)
                                Text(
                                    "Tap to upload photo",
                                    color = Color(0xFF2C7DA0)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DETAILS CARD
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        "📋 Item Details",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F3B5C)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = state.itemName,
                        onValueChange = viewModel::updateItemName,
                        label = { Text("Item Name *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = {
                            categoryExpanded = !categoryExpanded
                        }
                    ) {
                        OutlinedTextField(
                            value = state.category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category *") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = categoryExpanded
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = {
                                categoryExpanded = false
                            }
                        ) {
                            categories.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        viewModel.updateCategory(option)
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = state.description,
                        onValueChange = viewModel::updateDescription,
                        label = { Text("Description") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LOCATION CARD
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        "📍 Where & When",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F3B5C)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = state.college,
                        onValueChange = viewModel::updateCollege,
                        label = { Text("College / Area") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = state.location,
                        onValueChange = viewModel::updateLocation,
                        label = { Text("Specific Location *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = state.time,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Time Lost *") },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    showTimePicker = true
                                }
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimePicker = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (state.submitError.isNotBlank()) {
                Text(
                    state.submitError,
                    color = Color.Red,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    viewModel.submitLostItem(
                        context = context,
                        imageUri = imageUri,
                        onSuccess = onSubmit
                    )
                },
                enabled = canSubmit && !state.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2C7DA0)
                )
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Submit Lost Report",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "* Required fields",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}