package com.kayzwilson.retrace.ui.screens



import com.kayzwilson.retrace.ui.components.BottomNavBar
import com.kayzwilson.retrace.ui.components.StatCard
import com.kayzwilson.retrace.ui.components.InfoSectionCard
import com.kayzwilson.retrace.ui.components.InfoRow
import com.kayzwilson.retrace.ui.components.SettingsRow


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kayzwilson.retrace.ui.components.*
import com.kayzwilson.retrace.ui.components.BottomNavBar
import com.kayzwilson.retrace.ui.components.NavTab

// Temporary enum definition if not found elsewhere
enum class NavTab {
    LOST, FOUND, ACCOUNT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onNavigateToLost: () -> Unit,
    onNavigateToFound: () -> Unit,
    onBack: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var name      by remember { mutableStateOf("") }
    var role      by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var phone     by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var year      by remember { mutableStateOf("") }
    var semester  by remember { mutableStateOf("") }
    var college   by remember { mutableStateOf("") }
    var school    by remember { mutableStateOf("") }

    var isSaving        by remember { mutableStateOf(false) }
    var saveMessage     by remember { mutableStateOf("") }
    var isSavingPersonal  by remember { mutableStateOf(false) }
    var savePersonalMessage by remember { mutableStateOf("") }
    var isSavingAcademic  by remember { mutableStateOf(false) }
    var saveAcademicMessage by remember { mutableStateOf("") }

    var lostCount    by remember { mutableIntStateOf(3) }
    var foundCount   by remember { mutableIntStateOf(2) }
    var reportsCount by remember { mutableIntStateOf(12) }

    val auth   = FirebaseAuth.getInstance()
    val db     = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    LaunchedEffect(userId) {
        userId?.let { uid ->
            email = auth.currentUser?.email ?: ""
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        name       = doc.getString("name")       ?: ""
                        role       = doc.getString("role")       ?: ""
                        studentId  = doc.getString("studentId")  ?: ""
                        phone      = doc.getString("phone")      ?: ""
                        department = doc.getString("department") ?: ""
                        year       = doc.getString("year")       ?: ""
                        semester   = doc.getString("semester")   ?: ""
                        college    = doc.getString("college")    ?: ""
                        school     = doc.getString("school")     ?: ""
                    }
                }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = { Text("My Account", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F3B5C)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF2C7DA0))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // BottomNavBar should be defined in com.kayzwilson.retrace.ui.components
            BottomNavBar(
                activeTab           = NavTab.ACCOUNT,
                onNavigateToLost    = onNavigateToLost,
                onNavigateToFound   = onNavigateToFound,
                onNavigateToAccount = { /* already here */ }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally)
                            .background(
                                brush = Brush.linearGradient(colors = listOf(Color(0xFF2C7DA0), Color(0xFF1F5068))),
                                shape = CircleShape
                            )
                            .clickable(enabled = !isEditing) { isEditing = true; saveMessage = "" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.split(" ").filter { it.isNotEmpty() }.take(2)
                                .joinToString("") { it.first().uppercase() },
                            color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold
                        )
                        if (!isEditing) {
                            Box(
                                modifier = Modifier.align(Alignment.BottomEnd).size(32.dp)
                                    .background(Color.White, CircleShape)
                                    .border(2.dp, Color(0xFFE2E8F0), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit",
                                    modifier = Modifier.size(16.dp), tint = Color(0xFF2C7DA0))
                            }
                        }
                    }

                    if (saveMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = saveMessage,
                            color = if (saveMessage.startsWith("✅")) Color(0xFF2C7DA0) else Color.Red,
                            fontSize = 13.sp, textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth())
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isEditing) {
                        Text(text = name.ifEmpty { "Tap avatar to set Profiles" },
                            fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F3B5C))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = role.ifEmpty { "Role not set" }, fontSize = 14.sp, color = Color(0xFF5B6E8C))
                        Spacer(modifier = Modifier.height(16.dp))
                    } else {
                        OutlinedTextField(
                            value = name, onValueChange = { name = it },
                            label = { Text("Full Name", color = Color(0xFF5B6E8C)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedLabelColor = Color(0xFF2C7DA0)),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = role, onValueChange = { role = it },
                            label = { Text("Role", color = Color(0xFF5B6E8C)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedLabelColor = Color(0xFF2C7DA0)),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                isSaving = true; saveMessage = ""
                                userId?.let {
                                    db.collection("users").document(it)
                                        .set(mapOf("name" to name, "role" to role),
                                            com.google.firebase.firestore.SetOptions.merge())
                                        .addOnSuccessListener { isSaving = false; saveMessage = "✅ Profile updated!"; isEditing = false }
                                        .addOnFailureListener { e -> isSaving = false; saveMessage = "❌ ${e.message}" }
                                }
                            },
                            enabled = !isSaving && name.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2C7DA0), contentColor = Color.White,
                                disabledContainerColor = Color(0xFF2C7DA0).copy(alpha = 0.4f))
                        ) {
                            if (isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            else Text("Update Profile", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(20.dp))

            InfoSectionCard(title = "Personal Information", icon = "👤") {
                if (!isEditing) {
                    InfoRow(icon = "📧", label = "Email Address", value = email.ifEmpty { "Not set" })
                    InfoRow(icon = "📞", label = "Contact Number", value = phone.ifEmpty { "Not set" })
                    InfoRow(icon = "🎓", label = "Student Email", value = studentId.ifEmpty { "Not set" })
                } else {
                    OutlinedTextField(
                        value = email, onValueChange = { }, enabled = false,
                        label = { Text("Email Address") },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Color(0xFFE2E8F0), disabledTextColor = Color(0xFF8A99B4),
                            disabledLabelColor = Color(0xFF8A99B4)),
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = phone, onValueChange = { phone = it },
                        label = { Text("Contact Number") }, placeholder = { Text("+256 700 000000") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = studentId, onValueChange = { }, enabled = false,
                        label = { Text("Student Email") },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Color(0xFFE2E8F0), disabledTextColor = Color(0xFF8A99B4),
                            disabledLabelColor = Color(0xFF8A99B4)),
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (savePersonalMessage.isNotEmpty()) {
                        Text(text = savePersonalMessage,
                            color = if (savePersonalMessage.startsWith("✅")) Color(0xFF2C7DA0) else Color.Red,
                            fontSize = 13.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Button(
                        onClick = {
                            isSavingPersonal = true; savePersonalMessage = ""
                            userId?.let {
                                db.collection("users").document(it)
                                    .set(mapOf("phone" to phone), com.google.firebase.firestore.SetOptions.merge())
                                    .addOnSuccessListener { isSavingPersonal = false; savePersonalMessage = "✅ Contact info saved!" }
                                    .addOnFailureListener { e -> isSavingPersonal = false; savePersonalMessage = "❌ ${e.message}" }
                            }
                        },
                        enabled = !isSavingPersonal,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C7DA0), contentColor = Color.White,
                            disabledContainerColor = Color(0xFF2C7DA0).copy(alpha = 0.4f))
                    ) {
                        if (isSavingPersonal) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("Update Contact Info", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            InfoSectionCard(title = "Academic Information", icon = "📚") {
                if (!isEditing) {
                    InfoRow(icon = "🏛️", label = "College",         value = college.ifEmpty { "Not set" })
                    InfoRow(icon = "🏫", label = "School",          value = school.ifEmpty { "Not set" })
                    InfoRow(icon = "📂", label = "Department",      value = department.ifEmpty { "Not set" })
                    InfoRow(icon = "📖", label = "Year / Semester", value = if (year.isEmpty() && semester.isEmpty()) "Not set" else "$year • $semester")
                } else {
                    OutlinedTextField(value = college, onValueChange = { college = it },
                        label = { Text("College") }, placeholder = { Text("e.g. College of Engineering") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = school, onValueChange = { school = it },
                        label = { Text("School") }, placeholder = { Text("e.g. School of Computing") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = department, onValueChange = { department = it },
                        label = { Text("Department") }, placeholder = { Text("e.g. Computer Science") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = year, onValueChange = { year = it },
                            label = { Text("Year") }, placeholder = { Text("e.g. 3rd") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0)),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f))
                        OutlinedTextField(value = semester, onValueChange = { semester = it },
                            label = { Text("Semester") }, placeholder = { Text("e.g. 2") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0)),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (saveAcademicMessage.isNotEmpty()) {
                        Text(text = saveAcademicMessage,
                            color = if (saveAcademicMessage.startsWith("✅")) Color(0xFF2C7DA0) else Color.Red,
                            fontSize = 13.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Button(
                        onClick = {
                            isSavingAcademic = true; saveAcademicMessage = ""
                            userId?.let {
                                db.collection("users").document(it)
                                    .set(mapOf("college" to college, "school" to school,
                                        "department" to department, "year" to year, "semester" to semester),
                                        com.google.firebase.firestore.SetOptions.merge())
                                    .addOnSuccessListener { isSavingAcademic = false; saveAcademicMessage = "✅ Academic info saved!" }
                                    .addOnFailureListener { e -> isSavingAcademic = false; saveAcademicMessage = "❌ ${e.message}" }
                            }
                        },
                        enabled = !isSavingAcademic,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C7DA0), contentColor = Color.White,
                            disabledContainerColor = Color(0xFF2C7DA0).copy(alpha = 0.4f))
                    ) {
                        if (isSavingAcademic) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("Update Academic Info", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            InfoSectionCard(title = "Account Settings", icon = "⚙️") {
                SettingsRow(icon = "🔔", title = "Notification Preferences", subtitle = "Email & Push Notifications")
                HorizontalDivider(color = Color(0xFFF0F2F5), modifier = Modifier.padding(vertical = 8.dp))
                SettingsRow(icon = "🔒", title = "Privacy & Security", subtitle = "Two-factor authentication off")
                HorizontalDivider(color = Color(0xFFF0F2F5), modifier = Modifier.padding(vertical = 8.dp))
                SettingsRow(icon = "🌙", title = "Dark Mode", subtitle = "System default")
            }

            if (isEditing) {
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = { isEditing = false },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5B6E8C)),
                    shape = RoundedCornerShape(44.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) { Text("Cancel", fontSize = 15.sp) }
            }
        }
    }
}


