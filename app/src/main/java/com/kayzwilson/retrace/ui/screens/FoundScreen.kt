package com.kayzwilson.retrace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.kayzwilson.retrace.model.FoundItemData        // ← your model package
import com.kayzwilson.retrace.ui.components.BottomNavBar
import com.kayzwilson.retrace.ui.components.FoundItemCard
import com.kayzwilson.retrace.ui.components.NavTab

@Composable
fun FoundScreen(
    onNavigateToLost: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToReport: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var items by remember { mutableStateOf<List<FoundItemData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()
    val filters = listOf("All", "Electronics", "Bags", "Books", "ID Cards", "Keys", "Other")

    DisposableEffect(Unit) {
        val listener = db.collection("found_items")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                isLoading = false
                if (error != null) {
                    errorMessage = "Failed to load items: ${error.message}"
                    return@addSnapshotListener
                }
                items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FoundItemData::class.java)?.copy(id = doc.id)
                } ?: emptyList()
            }
        onDispose { listener.remove() }
    }

    val filteredItems = items.filter { item ->
        val matchesSearch = search.isBlank() ||
                item.itemName.contains(search, ignoreCase = true) ||
                item.description.contains(search, ignoreCase = true) ||
                item.location.contains(search, ignoreCase = true) ||
                item.category.contains(search, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "All"         -> true
            "Electronics" -> item.category.contains("Computer", ignoreCase = true) ||
                    item.category.contains("Laptop", ignoreCase = true) ||
                    item.category.contains("Phone", ignoreCase = true) ||
                    item.category.contains("Earphone", ignoreCase = true) ||
                    item.category.contains("Headphone", ignoreCase = true)
            "Bags"        -> item.category.contains("Bag", ignoreCase = true) ||
                    item.category.contains("Backpack", ignoreCase = true)
            "Books"       -> item.category.contains("Book", ignoreCase = true) ||
                    item.category.contains("Notes", ignoreCase = true)
            "ID Cards"    -> item.category.contains("ID", ignoreCase = true) ||
                    item.category.contains("Card", ignoreCase = true)
            "Keys"        -> item.category.contains("Key", ignoreCase = true)
            else          -> true
        }
        matchesSearch && matchesFilter
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF0FAF4))) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF0A3D2B), Color(0xFF1A6B47))))
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Found Items", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold,
                            color = Color.White, letterSpacing = 0.5.sp)
                        Text("${items.size} item${if (items.size != 1) "s" else ""} found",
                            fontSize = 13.sp, color = Color.White.copy(alpha = 0.65f))
                    }
                    Button(
                        onClick = onNavigateToReport,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF82)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("+ Found", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = search, onValueChange = { search = it },
                    placeholder = { Text("Search found items...", color = Color.White.copy(alpha = 0.5f)) },
                    leadingIcon = { Text("🔍", modifier = Modifier.padding(start = 4.dp)) },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                        focusedBorderColor = Color(0xFF4CAF82),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        cursorColor = Color.White
                    ),
                    singleLine = true
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isActive = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isActive) Brush.linearGradient(listOf(Color(0xFF0A3D2B), Color(0xFF1A6B47)))
                            else Brush.linearGradient(listOf(Color(0xFFECF8F2), Color(0xFFECF8F2)))
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(filter,
                        color = if (isActive) Color.White else Color(0xFF0A3D2B),
                        fontSize = 13.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF1A6B47), strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Loading found items...", fontSize = 14.sp, color = Color(0xFF5B6E8C))
                    }
                }
            }
            errorMessage.isNotEmpty() -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)) {
                        Text("⚠️", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMessage, fontSize = 14.sp, color = Color(0xFFE53935),
                            textAlign = TextAlign.Center)
                    }
                }
            }
            filteredItems.isEmpty() -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)) {
                        Text(if (search.isNotBlank()) "🔍" else "📭", fontSize = 52.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            if (search.isNotBlank()) "No results for \"$search\""
                            else "No found items yet",
                            fontSize = 17.sp, fontWeight = FontWeight.Bold,
                            color = Color(0xFF0A3D2B), textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            if (search.isNotBlank()) "Try a different search term"
                            else "Found something? Post it here!",
                            fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center
                        )
                        if (search.isBlank()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onNavigateToReport,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A6B47))
                            ) {
                                Text("Report Found Item", color = Color.White,
                                    fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredItems) { item -> FoundItemCard(item = item) }
                }
            }
        }

        BottomNavBar(
            activeTab = NavTab.FOUND,
            onNavigateToLost = onNavigateToLost,
            onNavigateToFound = { },
            onNavigateToAccount = onNavigateToAccount
        )
    }
}